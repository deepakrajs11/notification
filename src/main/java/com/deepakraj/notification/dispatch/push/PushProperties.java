package com.deepakraj.notification.dispatch.push;

import lombok.Data;

@Data
public class PushProperties {

    private Fcm fcm = new Fcm();
    private Apns apns = new Apns();

    @Data
    public static class Fcm {
        private boolean enabled = false;
    }

    @Data
    public static class Apns {
        private boolean enabled = false;
    }
}
