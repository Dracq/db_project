package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeRequest;
import com.dbtraining.reconx.exception.DuplicateTradeRefException;
import com.dbtraining.reconx.exception.TradeNotFoundException;
import com.dbtraining.reconx.kafka.TradeEventProducer;
import com.dbtraining.reconx.observability.TradeMetrics;
import com.dbtraining.reconx.repository.CounterpartyRepository;
import com.dbtraining.reconx.repository.InstrumentRepository;
import com.dbtraining.reconx.repository.TradeRepository;
import com.dbtraining.reconx.repository.entity.Trade;
import com.dbtraining.reconx.dto.TradeEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static com.dbtraining.reconx.repository.TradeSpecification.*;

/**
 * ============================================================================
 * TICKET-ADV064 — TradeService.create (POST endpoint backing)
 * TICKET-ADV065 — update
 * TICKET-ADV066 — updateStatus (PATCH)
 * TICKET-ADV067 — softDelete
 * TICKET-ADV083 — increments trade_created_total Counter on create
 * TICKET-ADV129 — publishes TradeEvent on every state change
 * TICKET-ADV055/ADV056 — list() uses Specifications + filter query
 * ============================================================================
 */
@Service
@Transactional
public class TradeService {

    private final TradeRepository tradeRepo;
    private final CounterpartyRepository cpRepo;
    private final InstrumentRepository instRepo;
    private final TradeEventProducer events;
    private final TradeMetrics metrics;

    public TradeService(TradeRepository tradeRepo,
                        CounterpartyRepository cpRepo,
                        InstrumentRepository instRepo,
                        TradeEventProducer events,
                        TradeMetrics metrics) {
        this.tradeRepo = tradeRepo;
        this.cpRepo = cpRepo;
        this.instRepo = instRepo;
        this.events = events;
        this.metrics = metrics;
    }

    public Trade create(TradeRequest req, String actor) {
        if (tradeRepo.findByTradeRef(req.tradeRef()).isPresent()) {
            throw new DuplicateTradeRefException(req.tradeRef());
        }

        var inst = instRepo.findById(req.instrumentId())
                .orElseThrow(() -> new TradeNotFoundException("Instrument id=" + req.instrumentId()));
        var cp = cpRepo.findById(req.counterpartyId())
                .orElseThrow(() -> new TradeNotFoundException("Counterparty id=" + req.counterpartyId()));

        Trade trade = new Trade();
        trade.setTradeRef(req.tradeRef());
        trade.setInstrument(inst);
        trade.setCounterparty(cp);
        trade.setQuantity(req.quantity());
        trade.setPrice(req.price());
        trade.setTradeDate(req.tradeDate());
        trade.setAssetClass("EQUITY");
        trade.setSide("BUY");
        trade.setStatus(com.dbtraining.reconx.repository.entity.TradeStatus.PENDING);

        Trade saved = tradeRepo.save(trade);

        metrics.incrementTradeCreated();
        if (saved.getQuantity() != null && saved.getPrice() != null) {
            metrics.recordTradeValue(saved.getQuantity().multiply(saved.getPrice()).doubleValue());
        }

        try {
            events.publish(new TradeEvent(
                    UUID.randomUUID(),
                    saved.getTradeRef(),
                    TradeEvent.EventType.TRADE_CREATED,
                    Instant.now(),
                    actor,
                    null,
                    null
            ));
        } catch (UnsupportedOperationException ignored) {
            // Kafka event producer not yet wired (TICKET-ADV129)
        }

        return saved;
    }

    public Trade update(Long id, TradeRequest req, String actor) {
        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() -> new TradeNotFoundException("id=" + id));

        var inst = instRepo.findById(req.instrumentId())
                .orElseThrow(() -> new TradeNotFoundException("Instrument id=" + req.instrumentId()));
        var cp = cpRepo.findById(req.counterpartyId())
                .orElseThrow(() -> new TradeNotFoundException("Counterparty id=" + req.counterpartyId()));

        trade.setTradeRef(req.tradeRef());
        trade.setInstrument(inst);
        trade.setCounterparty(cp);
        trade.setQuantity(req.quantity());
        trade.setPrice(req.price());
        trade.setTradeDate(req.tradeDate());

        Trade saved = tradeRepo.save(trade);

        try {
            events.publish(new TradeEvent(
                    UUID.randomUUID(),
                    saved.getTradeRef(),
                    TradeEvent.EventType.TRADE_UPDATED,
                    Instant.now(),
                    actor,
                    null,
                    null
            ));
        } catch (UnsupportedOperationException ignored) {
            // Kafka event producer not yet wired (TICKET-ADV129)
        }

        return saved;
    }

    public Trade updateStatus(Long id, String status, String actor) {
        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() -> new TradeNotFoundException("id=" + id));

        com.dbtraining.reconx.repository.entity.TradeStatus newStatus;
        try {
            newStatus = com.dbtraining.reconx.repository.entity.TradeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        trade.setStatus(newStatus);
        Trade saved = tradeRepo.save(trade);

        try {
            events.publish(new TradeEvent(
                    UUID.randomUUID(),
                    saved.getTradeRef(),
                    TradeEvent.EventType.TRADE_UPDATED,
                    Instant.now(),
                    actor,
                    null,
                    saved.getStatus() != null ? saved.getStatus().name() : null
            ));
        } catch (UnsupportedOperationException ignored) {
            // Kafka event producer not yet wired (TICKET-ADV129)
        }

        return saved;
    }

    public void softDelete(Long id, String actor) {
        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() -> new TradeNotFoundException("id=" + id));

        trade.softDelete();
        Trade saved = tradeRepo.save(trade);

        try {
            events.publish(new TradeEvent(
                    UUID.randomUUID(),
                    saved.getTradeRef(),
                    TradeEvent.EventType.TRADE_CANCELLED,
                    Instant.now(),
                    actor,
                    null,
                    null
            ));
        } catch (UnsupportedOperationException ignored) {
            // Kafka event producer not yet wired (TICKET-ADV129)
        }
    }

    @Transactional(readOnly = true)
    public Page<Trade> list(LocalDate from, LocalDate to, String status, Long counterpartyId, Pageable pageable) {
        com.dbtraining.reconx.repository.entity.TradeStatus tradeStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                tradeStatus = com.dbtraining.reconx.repository.entity.TradeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status filter: " + status);
            }
        }
        
        Specification<Trade> spec = Specification.where(tradeDateBetween(from, to))
            .and(hasStatus(tradeStatus))
            .and(forCounterparty(counterpartyId));
            
        return tradeRepo.findAll(spec, pageable);
    }
}
