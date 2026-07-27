```mermaid
C4Component
    title C4 Component — recon-service API

    Container_Ext(reactSpa, "Recon UI", "React")
    ContainerDb_Ext(postgres, "PostgreSQL")
    ContainerQueue_Ext(kafka, "Kafka")

    Container_Boundary(api, "recon-service API") {
        Component(authCtl, "AuthController", "Spring REST", "/api/auth/login, /refresh")
        Component(tradeCtl, "TradeController", "Spring REST", "/api/v1/trades CRUD")
        Component(reconCtl, "ReconController", "Spring REST", "/api/v1/recon/breaks")
        Component(analyticsCtl, "AnalyticsController", "Spring REST", "/api/v1/analytics")

        Component(jwtFilter, "JwtAuthFilter", "OncePerRequestFilter", "Parses + validates JWT, sets SecurityContext")
        Component(rbac, "MethodSecurity", "@PreAuthorize", "Role gate per endpoint")

        Component(tradeSvc, "TradeService", "@Service", "Trade lifecycle business rules")
        Component(reconSvc, "ReconciliationService", "@Service", "Match + break detection")
        Component(analyticsSvc, "AnalyticsService", "@Service", "Analytics and reporting")

        Component(tradeRepo, "TradeRepository", "JpaRepository + Specs", "Paged + filtered queries")
        Component(instrumentRepo, "InstrumentRepository", "JpaRepository", "Instrument queries")
        Component(counterpartyRepo, "CounterpartyRepository", "JpaRepository", "Counterparty queries")

        Component(producer, "KafkaProducer", "KafkaTemplate", "Publishes trade-events on commit")
        Component(consumer, "KafkaConsumer", "@KafkaListener", "Consumes recon-results from engine")
    }

    Rel(reactSpa, authCtl, "POST /login", "HTTPS")
    Rel(reactSpa, tradeCtl, "REST", "HTTPS + JWT")
    Rel(reactSpa, reconCtl, "REST", "HTTPS + JWT")
    Rel(reactSpa, analyticsCtl, "REST", "HTTPS + JWT")

    Rel(jwtFilter, rbac, "Sets SecurityContext")
    Rel(tradeCtl, tradeSvc, "calls")
    Rel(reconCtl, reconSvc, "calls")
    Rel(analyticsCtl, analyticsSvc, "calls")

    Rel(tradeSvc, tradeRepo, "uses")
    Rel(reconSvc, tradeRepo, "uses")
    Rel(tradeSvc, instrumentRepo, "uses")
    Rel(tradeSvc, counterpartyRepo, "uses")
    
    Rel(tradeRepo, postgres, "JDBC")
    Rel(instrumentRepo, postgres, "JDBC")
    Rel(counterpartyRepo, postgres, "JDBC")

    Rel(tradeSvc, producer, "emits event")
    Rel(producer, kafka, "publish trade-events")
    Rel(consumer, kafka, "subscribe recon-results")
    Rel(consumer, reconSvc, "callback")
```
