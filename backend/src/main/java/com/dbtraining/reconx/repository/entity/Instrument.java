package com.dbtraining.reconx.repository.entity;

import jakarta.persistence.*;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "instruments")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_class", nullable = false, length = 20)
    private AssetClass assetClass;

    @Column(nullable = false, length = 3)
    private String currency;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    public Instrument() {}

    public Long getId()         { return id; }
    public String getSymbol()   { return symbol; }
    public String getName()     { return name; }
    public AssetClass getAssetClass(){ return assetClass; }
    public String getCurrency() { return currency; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public void setId(Long v)           { this.id = v; }
    public void setSymbol(String v)     { this.symbol = v; }
    public void setName(String v)       { this.name = v; }
    public void setAssetClass(AssetClass v) { this.assetClass = v; }
    public void setCurrency(String v)   { this.currency = v; }
    public void setMetadata(Map<String, Object> v) { this.metadata = v; }
}
