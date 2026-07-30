package com.dbtraining.reconx.audit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

public class AuditEventPublisher {
    
    private final ApplicationEventPublisher publisher;
    private final AuditProperties properties;
    
    public AuditEventPublisher(ApplicationEventPublisher publisher, AuditProperties properties) {
        this.publisher = publisher;
        this.properties = properties;
    }
    
    public void publish(String event) {
        if (properties.isEnabled()) {
            publisher.publishEvent(event);
        }
    }
}
