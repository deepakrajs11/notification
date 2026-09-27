package com.deepakraj.notification.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MailProperties {

    private String host = "localhost";
    private int port = 25;
    private String username;
    private String password;
    private String from = "no-reply@example.com";
    private Map<String, String> properties = new HashMap<>();
}
