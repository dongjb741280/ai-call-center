package com.voxai.cc.cdr;

import com.voxai.core.entity.CdrNotify;
import com.voxai.core.mapper.CdrNotifyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

/** At-least-once delivery after the CDR transaction commits, with crash-recoverable leases. */
@Component
public class CdrNotifyDispatcher {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired private CdrNotifyMapper notifications;
    @Autowired private RestTemplate restTemplate;
    @Value("${cdr.notify.maxAttempts:10}") private int maxAttempts;

    @Scheduled(fixedDelayString = "${cdr.notify.pollMillis:5000}")
    public void dispatch() {
        for (CdrNotify notify : notifications.pending(System.currentTimeMillis())) {
            long now = System.currentTimeMillis();
            notify.setCts(now);
            notify.setLeaseUntil(now + 60_000L);
            notify.setLeaseToken(UUID.randomUUID().toString());
            if (notifications.claim(notify) == 0) { continue; }
            int attempts = notify.getPushTimes() + 1;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-CDR-Id", String.valueOf(notify.getCallId()));
                ResponseEntity<String> response = restTemplate.postForEntity(notify.getCdrNotifyUrl(),
                        new HttpEntity<>(notify.getContent(), headers), String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new IllegalStateException("HTTP " + response.getStatusCodeValue());
                }
                notify.setStatus(2);
                notify.setPushResponse(truncate(response.getBody()));
            } catch (Exception error) {
                notify.setStatus(attempts >= maxAttempts ? 3 : 1);
                notify.setPushResponse(truncate(error.getMessage()));
                logger.warn("CDR notify failed, callId:{}, attempt:{}", notify.getCallId(), attempts);
            }
            notify.setUts(System.currentTimeMillis() + Math.min(3_600_000L, 5000L << Math.min(attempts, 9)));
            notifications.finish(notify);
        }
    }

    private String truncate(String value) {
        return value == null ? "" : value.substring(0, Math.min(255, value.length()));
    }
}
