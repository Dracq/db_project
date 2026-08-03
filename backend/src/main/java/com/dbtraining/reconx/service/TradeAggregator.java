package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeEvent;
import com.dbtraining.reconx.repository.entity.AuditLogEntry;
import com.dbtraining.reconx.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TradeAggregator {

    private final AuditLogRepository auditRepo;
    private final ObjectMapper mapper;

    public TradeAggregator(AuditLogRepository auditRepo, ObjectMapper mapper) {
        this.auditRepo = auditRepo;
        this.mapper = mapper;
    }

    public Optional<JsonNode> rebuild(String tradeRef) {
        List<AuditLogEntry> events = auditRepo.findByTradeRefOrderByEventTimestampAsc(tradeRef);
        if (events.isEmpty()) {
            return Optional.empty();
        }

        JsonNode state = null;
        for (AuditLogEntry e : events) {
            switch (TradeEvent.EventType.valueOf(e.getEventType())) {
                case TRADE_CREATED, TRADE_UPDATED -> {
                    try {
                        state = e.getAfterState() != null ? mapper.readTree(e.getAfterState()) : null;
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to parse JSON state", ex);
                    }
                }
                case TRADE_CANCELLED -> state = null;
            }
        }
        return Optional.ofNullable(state);
    }
}
