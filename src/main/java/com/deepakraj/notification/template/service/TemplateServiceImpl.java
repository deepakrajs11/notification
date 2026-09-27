package com.deepakraj.notification.template.service;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.exception.DuplicateResourceException;
import com.deepakraj.notification.common.exception.NotificationException;
import com.deepakraj.notification.common.exception.ResourceNotFoundException;
import com.deepakraj.notification.common.exception.TemplateContentMissingException;
import com.deepakraj.notification.template.dto.TemplateRequest;
import com.deepakraj.notification.template.dto.TemplateResponse;
import com.deepakraj.notification.template.entity.NotificationTemplate;
import com.deepakraj.notification.template.render.RenderedContent;
import com.deepakraj.notification.template.render.TemplateRenderer;
import com.deepakraj.notification.template.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final TemplateRenderer templateRenderer;

    @Override
    public TemplateResponse create(TemplateRequest request) {
        if (notificationTemplateRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("A template already exists with code " + request.code());
        }
        if (!templateRenderer.contentExists(request.code(), request.channel())) {
            throw new TemplateContentMissingException(
                    "No template files found on classpath for code '" + request.code() + "' and channel " + request.channel());
        }
        NotificationTemplate template = new NotificationTemplate();
        template.setCode(request.code());
        template.setChannel(request.channel());
        template.setDescription(request.description());
        template.setActive(true);
        return TemplateResponse.from(notificationTemplateRepository.save(template));
    }

    @Override
    public TemplateResponse setActive(String code, boolean active) {
        NotificationTemplate template = requireByCode(code);
        template.setActive(active);
        return TemplateResponse.from(notificationTemplateRepository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateResponse get(String code) {
        return TemplateResponse.from(requireByCode(code));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplate requireActiveTemplate(String code, Channel channel) {
        NotificationTemplate template = requireByCode(code);
        if (!template.isActive()) {
            throw new NotificationException("Template '" + code + "' is not active");
        }
        if (template.getChannel() != channel) {
            throw new NotificationException(
                    "Template '" + code + "' is registered for channel " + template.getChannel() + ", not " + channel);
        }
        return template;
    }

    @Override
    @Transactional(readOnly = true)
    public RenderedContent render(String code, Channel channel, Map<String, Object> variables) {
        return templateRenderer.render(code, channel, variables);
    }

    private NotificationTemplate requireByCode(String code) {
        return notificationTemplateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No template found with code " + code));
    }
}
