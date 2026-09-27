package com.deepakraj.notification.template.service;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.template.dto.TemplateRequest;
import com.deepakraj.notification.template.dto.TemplateResponse;
import com.deepakraj.notification.template.entity.NotificationTemplate;
import com.deepakraj.notification.template.render.RenderedContent;

import java.util.Map;

public interface TemplateService {

    TemplateResponse create(TemplateRequest request);

    TemplateResponse setActive(String code, boolean active);

    TemplateResponse get(String code);

    NotificationTemplate requireActiveTemplate(String code, Channel channel);

    RenderedContent render(String code, Channel channel, Map<String, Object> variables);
}
