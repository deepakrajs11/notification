# Notification Service

A self-contained Spring Boot notification microservice that delivers **email** (SMTP) and **push** (FCM/APNs-shaped) notifications, driven by a database-backed queue and worker with automatic retry, backoff, dead-lettering, and full attempt-level audit logging.

Everything is organized around a handful of small interfaces (`NotificationChannelProvider`, `PlatformPushSender`, `TemplateRenderer`) so new channels, platforms, or template engines can be dropped in without touching the queue, the worker, or existing controllers.

## Contents

- [Architecture](#architecture)
- [The three databases](#the-three-databases)
- [Queue, worker, retry, and dead-lettering](#queue-worker-retry-and-dead-lettering)
- [Audit log](#audit-log)
- [Templates](#templates)
- [Plug-and-play extension points](#plug-and-play-extension-points)
- [Package layout](#package-layout)
- [API](#api)
- [Configuration](#configuration)
- [Running it](#running-it)

## Architecture

```
                 ┌──────────────────────┐
 POST /api/      │  NotificationController
 notifications   └──────────┬───────────┘
                             │ enqueue()
                             ▼
                  ┌─────────────────────┐        ┌───────────────────┐
                  │ NotificationQueue    │        │  contact-db (H2)  │
                  │ Publisher            │        │  UserContact      │
                  │ (validates template, │        └───────────────────┘
                  │  writes NEW entry)   │        ┌───────────────────┐
                  └──────────┬───────────┘        │  device-db (H2)   │
                             │                     │  UserDevice       │
                             ▼                     └───────────────────┘
                  ┌─────────────────────┐        ┌───────────────────┐
                  │ notification_queue_ │◀──────▶│  engine-db (H2)   │
                  │ entry (engine-db)   │        │  templates, queue,│
                  └──────────┬───────────┘        │  audit log        │
                             │ @Scheduled poll     └───────────────────┘
                             ▼
                  ┌─────────────────────┐
                  │  NotificationWorker  │  claim → render → dispatch
                  └──────────┬───────────┘
                             ▼
        ┌────────────────────┴────────────────────┐
        ▼                                          ▼
┌───────────────────┐                    ┌───────────────────────┐
│ EmailChannelProvider│                   │ PushChannelProvider    │
│ (JavaMailSender)    │                   │ → PlatformPushSender   │
└───────────────────┘                    │   (FCM / APNs seam)    │
                                          └───────────────────────┘
                             │
                             ▼
                  ┌─────────────────────┐
                  │ NotificationLog      │  one row per delivery attempt
                  │ (engine-db, audit)   │
                  └─────────────────────┘
```

## The three databases

Per the original design brief, the service is split across **three independent H2 databases**, each with its own `DataSource`, `EntityManagerFactory`, and `TransactionManager` (wired by hand in `config/datasource/*` — Boot's default datasource/JPA autoconfiguration is excluded on `@SpringBootApplication` for this exact reason):

| Database | Holds | Config class |
|---|---|---|
| `contact-db` | `UserContact` — email address, display name, locale, active flag | `ContactDataSourceConfig` |
| `device-db` | `UserDevice` — userId, deviceId, platform, push token, ACTIVE/INACTIVE status | `DeviceDataSourceConfig` |
| `engine-db` | `NotificationTemplate` registry, the notification queue, and the audit log | `EngineDataSourceConfig` (`@Primary`) |

Files live under `./data/*.mv.db` (gitignored). A user can have many devices; push fans out to every `ACTIVE` one.

> **Wiring gotcha worth knowing:** with three `DataSource`/`EntityManagerFactory` beans of the same type and one marked `@Primary`, Spring resolves `@Primary` *before* falling back to parameter-name matching — so every cross-reference between config classes uses an explicit `@Qualifier`, not just a matching parameter name. Omitting it silently wires the engine datasource everywhere.

## Queue, worker, retry, and dead-lettering

`NotificationQueueEntry` (engine-db) is the queue. `NotificationQueuePublisher.enqueue()` validates the template (exists, active, right channel) and inserts a row with `status=NEW`, `attemptCount=0`, `nextAttemptAt=now`.

`NotificationWorker` polls on a fixed delay (`notification.queue.poll-interval-ms`, default 5s):

1. **Select due work** — `status IN (NEW, RETRY_SCHEDULED) AND nextAttemptAt <= now`, batch-limited (`notification.queue.batch-size`).
2. **Claim** — a conditional `UPDATE ... SET status='PROCESSING' WHERE id=:id AND status=:expectedStatus`. If zero rows update, another worker/thread already took it — no pessimistic locks needed, safe under concurrency.
3. **Render + dispatch** (`NotificationDispatchExecutor`, its own transaction per entry) — resolve the template, render it, hand off to the matching `NotificationChannelProvider`.
4. **On success** → `status=SENT`.
5. **On failure** → `attemptCount++`; if `attemptCount >= maxAttempts` → `status=DEAD_LETTER` (terminal); otherwise → `status=RETRY_SCHEDULED` with `nextAttemptAt = now + min(baseDelay * multiplier^attemptCount, maxDelay)` (`RetryPolicy`, exponential backoff). This is the "repush into the queue" behavior — the same row is rescheduled rather than re-inserted.

Push fan-out rule: a `PUSH` entry sends to every active device for the user; the queue entry as a whole succeeds if **any** device accepted it, but each device gets its own audit log row regardless.

## Audit log

`NotificationLog` (engine-db) gets **one row per delivery attempt**, not just the final state — so a notification that failed twice before succeeding leaves three rows behind. Query it via `GET /api/notifications/logs` (filterable by `userId`, `channel`, `status`) or fetch the full history for one send via `GET /api/notifications/{trackingId}`.

## Templates

Templates are rendered with **Thymeleaf**, resolved by file convention from a template's `code` (`NotificationTemplate.code`) — nothing is stored as freeform content in the database, only registry metadata (code, channel, active flag):

| Channel | Files (under `src/main/resources/templates/`) | Thymeleaf mode |
|---|---|---|
| `EMAIL` | `email/{code}.html` (body), `email/{code}-subject.txt` (subject) | HTML / TEXT |
| `PUSH` | `push/{code}-title.txt`, `push/{code}-body.txt` | TEXT |

`TemplateService.create()` refuses to register a template whose backing files don't exist on the classpath (`ThymeleafTemplateRenderer.contentExists`), so a bad template code fails fast at registration time, not at send time. Two seed templates (`welcome_email`, `welcome_push`) are inserted on startup by `TemplateSeeder`.

## Plug-and-play extension points

Three interfaces are the seams. Add an implementation, and the worker/controllers need no changes:

- **`TemplateRenderer`** (`template/render`) — swap Thymeleaf for another engine by implementing this and re-pointing the `ThymeleafTemplateRenderer` bean.
- **`NotificationChannelProvider`** (`dispatch`) — one per `Channel` (`EMAIL`, `PUSH` today). Add a new channel (e.g. SMS, Slack) by implementing this and registering it; `ChannelProviderRegistry` picks it up automatically by scanning all beans of the type.
- **`PlatformPushSender`** (`dispatch/push`) — one per push platform. `FcmNoOpPushSender` (ANDROID + WEB) and `ApnsNoOpPushSender` (IOS) are log-only stand-ins today, each gated by `@ConditionalOnProperty("notification.push.{fcm,apns}.enabled")` defaulting to `false`. **To wire real delivery:** add the Firebase Admin SDK / Pushy dependency, implement `PlatformPushSender`, annotate the bean `@ConditionalOnProperty(..., havingValue = "true")`, flip the flag. `PlatformSenderRegistry` picks it up the same way `ChannelProviderRegistry` does — no changes to `PushChannelProvider` or the worker.

## Package layout

Domain-first, one package per bounded concern:

```
common/          enums (Channel, Platform, DeviceStatus, QueueStatus, LogStatus), exceptions, GlobalExceptionHandler
config/          datasource wiring (one class per DB), mail, Thymeleaf engines, scheduling, seed data
contact/         UserContact — entity, repository, service, controller, dto   (contact-db)
device/          UserDevice  — entity, repository, service, controller, dto   (device-db)
template/        NotificationTemplate registry + render/ (TemplateRenderer, ThymeleafTemplateRenderer)  (engine-db)
queue/           NotificationQueueEntry, publisher, query service, RetryPolicy, worker/  (engine-db)
log/             NotificationLog — audit entity, repository, service, controller  (engine-db)
dispatch/        NotificationChannelProvider + ChannelProviderRegistry, email/, push/ (PlatformPushSender + registry, NoOp senders)
api/             top-level NotificationController (send / status) + shared request/response DTOs
```

## API

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/contacts` | Register a user's email contact |
| `PUT` | `/api/contacts/{userId}` | Update it |
| `GET` | `/api/contacts/{userId}` | Fetch it |
| `DELETE` | `/api/contacts/{userId}` | Deactivate it |
| `POST` | `/api/devices` | Register/refresh a device (upsert by `deviceId`) |
| `GET` | `/api/devices?userId=` | List a user's devices |
| `DELETE` | `/api/devices/{deviceId}` | Deactivate (logout) |
| `POST` | `/api/templates` | Register a template (validates backing files exist) |
| `GET` | `/api/templates/{code}` | Fetch a template |
| `PATCH` | `/api/templates/{code}/active?active=` | Enable/disable a template |
| `POST` | `/api/notifications` | Enqueue a send → `202` + `trackingId` |
| `GET` | `/api/notifications/{trackingId}` | Queue status + full attempt history |
| `GET` | `/api/notifications/logs?userId=&channel=&status=` | Audit search |

## Configuration

All settings live under `notification.*` in `application.yaml`:

```yaml
notification:
  datasource: { contact: {...}, device: {...}, engine: {...} }   # per-DB url/driver/username/password
  mail: { host, port, username, password, from, properties }     # SMTP, env-var driven
  queue: { poll-interval-ms, batch-size, default-max-attempts }
  retry: { base-delay-ms, multiplier, max-delay-ms }              # exponential backoff
  push: { fcm: { enabled }, apns: { enabled } }                   # false = NoOp senders
```

SMTP credentials are read from `SMTP_HOST` / `SMTP_PORT` / `SMTP_USERNAME` / `SMTP_PASSWORD` / `SMTP_FROM` env vars (see `application.yaml`), so no secrets need to be committed.

## Running it

```bash
./mvnw spring-boot:run
```

```bash
# register a contact and a device
curl -X POST localhost:8080/api/contacts -H 'Content-Type: application/json' \
  -d '{"userId":"user-1","email":"user1@example.com","displayName":"Ada"}'
curl -X POST localhost:8080/api/devices -H 'Content-Type: application/json' \
  -d '{"userId":"user-1","deviceId":"device-1","platform":"ANDROID","pushToken":"tok-abc"}'

# send using the seeded templates
curl -X POST localhost:8080/api/notifications -H 'Content-Type: application/json' \
  -d '{"channel":"PUSH","templateCode":"welcome_push","userId":"user-1","variables":{"name":"Ada","appName":"Notify"}}'

# check status a few seconds later (worker polls every 5s by default)
curl localhost:8080/api/notifications/{trackingId}
```

Without SMTP configured, email sends will fail fast and visibly walk through `RETRY_SCHEDULED` → (eventually) `DEAD_LETTER` — a convenient way to see the retry path in action. Push sends succeed immediately via the NoOp FCM/APNs senders and log a `[FCM-NOOP]` / `[APNS-NOOP]` line.

Run the test suite with `./mvnw test` — it boots the full three-datasource context against in-memory H2 and covers retry backoff math and template rendering.
