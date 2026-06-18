Enterprise Titan Dispatch Backend (Core V1)
🚀 Overview
This PR introduces the foundational enterprise architecture and core business engine for Titan Dispatch, a highly resilient heavy equipment logistics and dynamic costing platform.

🌍 The Problem it Solves
Heavy equipment logistics operates in a highly volatile domain. Construction sites are chaotic, human safety is heavily regulated, and the IoT telematics sensors mounted on heavy machinery (excavators, bulldozers) frequently operate in dead zones, leading to massive, unpredictable bursts of delayed network traffic.

This backend is specifically engineered to guarantee human safety compliance, calculate to-the-minute financial utilization, and survive aggressive IoT network volatility without dropping data or crashing the database.

🏗️ Architectural Highlights & Business Flows
1. The Core Domain: Safety & Dynamic Costing
   Safety-First Dispatching: Implemented a pure, framework-agnostic SafetyInterlockPolicy. Before any dispatch is persisted, the domain validates operator licensure and equipment maintenance status. Violations instantly return an RFC 7807 ProblemDetail 409 Conflict.

Dynamic Financial Engine: Equipment is billed by engine hours, not calendar days. The completeDispatch pipeline mathematically calculates utilization (endHours - startHours * internalRate) and atomically applies it to the JobSite budget.

2. IoT Ingestion & Infrastructure Resilience
   DDoS & Spam Protection: The /webhooks/telematics endpoint is shielded by a Bucket4j Token Bucket rate-limiter (5 requests/min per asset) executing entirely in memory to protect database connection pools (HikariCP tuned to 25 max connections).

Redis Idempotency: Implemented atomic setIfAbsent locks in Redis. If a machine misfires and sends the same GPS ping 10 times in one second, 9 are dropped instantly, preventing double-billing and spatial query overload.

Spatial Mathematics: Integrated PostGIS with hibernate-spatial. Asynchronous workers execute ST_DWithin queries against the earth's curvature to detect if machinery breaches a physical geofence.

3. Distributed Systems & Data Integrity
   Transactional Outbox Pattern: To ensure external billing/analytics services are notified of completed jobs, events are persisted to an outbox_events table within the exact same transaction as the dispatch completion. A background @Scheduled worker drains this queue using PostgreSQL's SELECT ... FOR UPDATE SKIP LOCKED, guaranteeing exactly-once delivery and zero deadlocks in a multi-node deployment.

Immutable Chain of Custody: Hibernate Envers acts as a silent ledger, generating un-tamperable audit histories (_aud tables) for all critical entities.

Relational Safety: Deployed Hibernate 6 @SoftDelete. To avoid LazyInitializationException proxy conflicts on historical invoices, soft-deleted entities are managed via strict FetchType.EAGER mapping on immutable historical records. All schema changes are strictly versioned via Flyway (V1-V8).

4. Security & Access Control (Zero Trust)
   Stateless Authentication: Fully stateless JWT architecture. To prevent Cross-Site Scripting (XSS), refresh tokens are locked in HttpOnly, Secure, SameSite=Strict cookies.

Strict CORS: Configured explicitly to allow credentials for the Angular frontend (http://localhost:4200), completely disabling wildcard origins.

Granular RBAC: Endpoints are guarded by Spring Security @PreAuthorize SpEL annotations, isolating ADMIN, DISPATCH, and MECHANIC roles to their respective domains.

5. Observability & Developer Experience
   Business Metrics: Injected Micrometer MeterRegistry into the domain layer to track active deployments and calculate real-time cumulative revenue, automatically exposed to Prometheus.

Integration Testing: Utilized Testcontainers (specifically bypassing Spring Boot 3.2 naming strictness via asCompatibleSubstituteFor) to run real PostGIS containers for repository validation.

API Contract: Full interactive OpenAPI 3 (Swagger) documentation generated at /swagger-ui.html.

📋 Readiness Checklist
[x] JWT Auth Flow (Login, Refresh, Logout) verified via Postman/Swagger.

[x] Database migrations execute cleanly on a fresh container.

[x] Read-Model DTOs (Dropdowns, Summaries) implemented to decouple entities from the UI.

[x] Actuator and Prometheus endpoints active.

[x] No sensitive endpoints exposed without JWT validation.