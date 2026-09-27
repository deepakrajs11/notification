package com.deepakraj.notification.template.api;

import com.deepakraj.notification.template.dto.TemplateRequest;
import com.deepakraj.notification.template.dto.TemplateResponse;
import com.deepakraj.notification.template.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@Valid @RequestBody TemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.create(request));
    }

    @GetMapping("/{code}")
    public TemplateResponse get(@PathVariable String code) {
        return templateService.get(code);
    }

    @PatchMapping("/{code}/active")
    public TemplateResponse setActive(@PathVariable String code, @RequestParam boolean active) {
        return templateService.setActive(code, active);
    }
}
