package com.deepakraj.notification.template.render;

import com.deepakraj.notification.common.enums.Channel;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ThymeleafTemplateRendererTest {

    private final ThymeleafTemplateRenderer renderer = new ThymeleafTemplateRenderer(htmlEngine(), textEngine());

    private static TemplateEngine htmlEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    private static TemplateEngine textEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".txt");
        resolver.setTemplateMode(TemplateMode.TEXT);
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    @Test
    void rendersEmailSubjectAndHtmlBodyWithVariables() {
        RenderedContent content = renderer.render("welcome_email", Channel.EMAIL,
                Map.of("name", "Ada", "appName", "Notify"));

        assertThat(content.subjectOrTitle()).isEqualTo("Welcome aboard, Ada!");
        assertThat(content.body()).contains("Ada").contains("Notify");
    }

    @Test
    void rendersPushTitleAndBodyWithVariables() {
        RenderedContent content = renderer.render("welcome_push", Channel.PUSH,
                Map.of("name", "Grace", "appName", "Notify"));

        assertThat(content.subjectOrTitle()).isEqualTo("Welcome, Grace!");
        assertThat(content.body()).isEqualTo("Thanks for joining Notify. Tap to get started.");
    }

    @Test
    void contentExistsIsTrueForSeededTemplatesAndFalseForUnknownCode() {
        assertThat(renderer.contentExists("welcome_email", Channel.EMAIL)).isTrue();
        assertThat(renderer.contentExists("welcome_push", Channel.PUSH)).isTrue();
        assertThat(renderer.contentExists("does_not_exist", Channel.EMAIL)).isFalse();
    }
}
