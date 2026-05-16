# Titan-Dispatch
Enterprise-grade Spring Boot 3 backend for Titan Dispatch: a heavy equipment logistics, dynamic costing, and telematics management engine. Features strict RBAC, IoT webhooks, and complex safety interlock policies.


# 🚜 Titan Dispatch - Core Backend

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20DDD-purple.svg)

Titan Dispatch is an enterprise-grade Heavy Equipment Logistics & Costing Engine designed for construction firms and machinery rental agencies. This repository contains the core backend API, engineered with strict architectural boundaries, asynchronous event-driven processing, and robust domain policies.

## 🌟 Core Value Proposition
* **Prevent Machinery Downtime:** Automated maintenance schedules driven by actual engine hours.
* **Ensure Legal Compliance:** Hard-coded "Safety Interlocks" prevent the dispatch of equipment with expired insurance or operators with expired licenses.
* **Automate Dynamic Costing:** Real-time job costing driven by telematics and engine hour tracking.

## 🏗️ Architecture & Tech Stack

This project strictly adheres to a **Pragmatic Clean / Layered Architecture** incorporating elements of Domain-Driven Design (DDD) and Command Query Responsibility Segregation (CQRS).

* **Language:** Java 17 (Amazon Corretto)
* **Framework:** Spring Boot 3.2.x
* **Data Persistence:** PostgreSQL, Spring Data JPA, Hibernate 6 (Soft Deletes & Envers Auditing)
* **Caching & Idempotency:** Redis (Spring Data Redis)
* **Security:** Spring Security, Stateless JWT Auth, SpEL Method-Level Authorization
* **Async Processing:** Spring `@Async` and Application Events for IoT webhook ingestion

## ✨ Key Features

1. **Strict RBAC (Role-Based Access Control):** Granular permissions for `ADMIN` (Fleet Managers), `DISPATCH` (Dispatchers), and `MECHANIC` (Maintenance).
2. **Domain-Driven Safety Interlocks:** Business rules are isolated into pure, testable domain policies rather than sprawling service layers.
3. **Telematics Webhooks (IoT):** Idempotent, high-throughput endpoints that asynchronously process machinery pings to update engine hours and trigger geofence alerts.
4. **Immutable Audit Trail:** Chain of Custody logging using JPA Auditing (`@CreatedBy`, `@LastModifiedBy`) and Hibernate soft-deletes.

## 📂 Package Structure

The system enforces strict boundary isolation:
```text
com.titan.dispatch
├── domain         # Core entities, pure business policies, and domain events
├── repository     # Spring Data JPA interfaces
├── service        # Application logic, transaction boundaries, and event handlers
├── web            # REST Controllers, DTO records (Commands/Queries), and Exception Handlers
└── infrastructure # Security configurations, JWT filters, and Redis caches
