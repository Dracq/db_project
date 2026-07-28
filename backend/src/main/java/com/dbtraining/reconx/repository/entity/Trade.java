package com.dbtraining.reconx.repository.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "trades", indexes = {
    @Index(name = "idx_trades_trade_date", columnList = "trade_date"),
    @Index(name = "idx_trades_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Audited
@SQLRestriction("deleted_at IS NULL")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_ref", nullable = false, unique = true, length = 30)
    private String tradeRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;

    @Column(name = "asset_class", nullable = false, length = 20)
    private String assetClass;

    @Column(nullable = false, length = 4)
    private String side;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeStatus status = TradeStatus.PENDING;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Instant modifiedAt;

    public Trade() {}

    /** Soft-delete: set deletedAt so @SQLRestriction filters this out. */
    public void softDelete() { this.deletedAt = Instant.now(); }

    public Long getId()                  { return id; }
    public String getTradeRef()          { return tradeRef; }
    public Instrument getInstrument()    { return instrument; }
    public Counterparty getCounterparty(){ return counterparty; }
    public String getAssetClass()        { return assetClass; }
    public String getSide()              { return side; }
    public BigDecimal getQuantity()      { return quantity; }
    public BigDecimal getPrice()         { return price; }
    public LocalDate getTradeDate()      { return tradeDate; }
    public TradeStatus getStatus()            { return status; }
    public Instant getDeletedAt()        { return deletedAt; }
    public Instant getCreatedAt()        { return createdAt; }
    public Instant getModifiedAt()       { return modifiedAt; }

    public void setTradeRef(String v)         { this.tradeRef = v; }
    public void setInstrument(Instrument v)   { this.instrument = v; }
    public void setCounterparty(Counterparty v){ this.counterparty = v; }
    public void setAssetClass(String v)       { this.assetClass = v; }
    public void setSide(String v)             { this.side = v; }
    public void setQuantity(BigDecimal v)     { this.quantity = v; }
    public void setPrice(BigDecimal v)        { this.price = v; }
    public void setTradeDate(LocalDate v)     { this.tradeDate = v; }
    public void setStatus(TradeStatus v)      { this.status = v; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
