package com.voxai.cc.dialer;

import com.voxai.core.po.dialer.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** This independent service credential never accepts a tenant supplied in the JSON payload. */
@RestController
@RequestMapping("/internal/dialer")
public class DialerControlController {
    private final DialerControlProperties properties;
    private final TaskMediaService media;
    public DialerControlController(DialerControlProperties properties, TaskMediaService media) {
        this.properties = properties; this.media = media;
    }
    void authorize(Long company, String authorization) {
        String token = properties.getTokens().get(company);
        if (!properties.isEnabled() || token == null || authorization == null ||
                !MessageDigest.isEqual(("Bearer " + token).getBytes(StandardCharsets.UTF_8), authorization.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/task/{taskId}/capacity")
    public DialerCapacity capacity(@RequestHeader("X-Company-Id") Long company,
            @RequestHeader(value="Authorization",required=false) String authorization, @PathVariable Long taskId) {
        authorize(company, authorization); return media.capacity(company, taskId);
    }
    @PostMapping("/task/{taskId}/dial")
    public DialerStatus dial(@RequestHeader("X-Company-Id") Long company,
            @RequestHeader(value="Authorization",required=false) String authorization, @PathVariable Long taskId,
            @RequestBody DialerRequest request) {
        authorize(company, authorization); return media.dial(company, taskId, request);
    }
    @PostMapping("/task/{taskId}/status")
    public DialerStatus status(@RequestHeader("X-Company-Id") Long company,
            @RequestHeader(value="Authorization",required=false) String authorization, @PathVariable Long taskId,
            @RequestBody DialerRequest request) {
        authorize(company, authorization); return media.status(company, taskId, request);
    }
}
