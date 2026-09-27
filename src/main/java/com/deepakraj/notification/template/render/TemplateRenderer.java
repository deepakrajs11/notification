package com.deepakraj.notification.template.render;

import com.deepakraj.notification.common.enums.Channel;

import java.util.Map;

public interface TemplateRenderer {

    RenderedContent render(String code, Channel channel, Map<String, Object> variables);

    boolean contentExists(String code, Channel channel);
}
