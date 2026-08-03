package com.dbtraining.reconx.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * ============================================================================
 * DLQ via DeadLetterPublishingRecoverer (failed messages
 *                routed to {topic}-dlq with the same partition number)
 * Retry strategy: 3 attempts with exponential backoff
 *                (1s, 2s, 4s) before giving up to DLQ
 * ============================================================================
 */
@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                template,
                (ConsumerRecord<?, ?> rec, Exception ex) ->
                        new TopicPartition(rec.topic() + "-dlq", rec.partition())
        );
        ExponentialBackOff backoff = new ExponentialBackOff(1000L, 2.0);
        backoff.setMaxElapsedTime(8_000L);
        
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(
                DeserializationException.class, 
                IllegalArgumentException.class
        );
        return handler;
    }
}
