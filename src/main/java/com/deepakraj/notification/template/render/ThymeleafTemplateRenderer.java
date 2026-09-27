package com.deepakraj.notification.template.render;

import com.deepakraj.notification.common.enums.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Component
public class ThymeleafTemplateRenderer implements TemplateRenderer {

    private final TemplateEngine htmlTemplateEngine;
    private final TemplateEngine textTemplateEngine;

    public ThymeleafTemplateRenderer(@Qualifier("htmlTemplateEngine") TemplateEngine htmlTemplateEngine,
                                      @Qualifier("textTemplateEngine") TemplateEngine textTemplateEngine) {
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.textTemplateEngine = textTemplateEngine;
    }

    @Override
    public RenderedContent render(String code, Channel channel, Map<String, Object> variables) {
        Context context = new Context(Locale.getDefault(), variables == null ? Map.of() : variables);
        if (channel == Channel.EMAIL) {
            String subject = textTemplateEngine.process("email/" + code + "-subject", context);
            String body = htmlTemplateEngine.process("email/" + code, context);
            return new RenderedContent(subject.strip(), body);
        }
        String title = textTemplateEngine.process("push/" + code + "-title", context);
        String body = textTemplateEngine.process("push/" + code + "-body", context);
        return new RenderedContent(title.strip(), body.strip());
    }

    @Override
    public boolean contentExists(String code, Channel channel) {
        if (channel == Channel.EMAIL) {
            return classpathFileExists("templates/email/" + code + ".html")
                    && classpathFileExists("templates/email/" + code + "-subject.txt");
        }
        return classpathFileExists("templates/push/" + code + "-title.txt")
                && classpathFileExists("templates/push/" + code + "-body.txt");
    }

    private boolean classpathFileExists(String path) {
        return new ClassPathResource(path).exists();
    }
}
