# AGENTS.md — Retail Backend (Spring Boot)

This document defines **hard constraints**, **project structure**, and **code‑generation rules** for all humans and automated agents contributing to this repository. Treat these rules as **binding** unless an Architecture Decision Record (ADR) explicitly overrides them.

---

## 1. Project DNA (Immutable)

**Runtime**

* Spring Boot **4+**
* Java **25**

**Build**

* Maven **3.9+**
* Multi‑module reactor
* Maven Wrapper (**./mvnw**) is mandatory



**Ports**

* `8080` — Application HTTP
* `8081` — Actuator
* `9090` — Debug (local only)

**Profiles**

| Profile | Database           | Notes                     |
| ------- | ------------------ | ------------------------- |
| local   | H2                 | MySQL compatibility mode  |
| dev     | Postgres           | Non‑prod                  |
| staging | RDS                | Production‑like           |
| prod    | RDS + read‑replica | Flyway callbacks disabled |

---

## 2. Non‑Negotiable Engineering Rules

Violations are considered **build‑blocking defects**.

### Dependency Injection

* **Constructor injection only**
* ❌ Field injection (`@Autowired` on fields) is forbidden

### DTOs & Validation

* Every public HTTP endpoint **must**:

    * Use a request DTO
    * Annotate DTO with `@Validated` / `@Valid`

### Lombok & Data Modeling

* `@Data` is to be used instead of records

### Transactions

* Every **public method** in a `@Service`:

    * **Must** be annotated with `@Transactional`
    * Use `readOnly = true` where applicable


### Logging

* Structured logging only
* Use:

```
net.logstash.logback.marker.StructuredArguments
```

* Logs must be parseable as JSON by ELK

---

## 3. One‑Liner Commands (Canonical)

```bash
# Run application (local profile)
./mvnw spring-boot:run -pl app -Dspring-boot.run.profiles=local

# Start supporting infrastructure
docker compose -f docker/infra.yml up -d

# Full quality gate (unit + integration + contract + arch tests)
./mvnw verify

# Generate OpenAPI specification
./mvnw -pl app spring-boot:run -Dspring-boot.run.profiles=openapi &
curl http://localhost:8080/v3/api-docs > openapi.json
```

---

## 4. Technology Stack Cheatsheet

| Concern       | Technology                    | Config Prefix            |
| ------------- | ----------------------------- | ------------------------ |
| Database      | PostgreSQL 15                 | `spring.datasource`      |
| Cache         | Redis 7 (Lettuce)             | `spring.data.redis`      |
| Messaging     | RabbitMQ                      | `spring.rabbitmq`        |
| Security      | Spring Security + OAuth2      | `spring.security.oauth2` |
| Observability | Micrometer → Prometheus       | `management.metrics`     |

---

## 5. Testing Strategy (Mandatory)

### Unit Tests

* JUnit 5 + Mockito
* Naming: `*Test.java`




## 6. Code‑Generation Rules (For Humans & AI)

When generating or modifying code, **all** of the following apply:

### Package Placement

* REST Controllers:

```
com.scalum.starter.controller
```

* Domain Services:

```
com.scalum.starter.domain
```

* Outbound Adapters:

```
com.scalum.starter.out.*
```

### Required Artifacts per Endpoint

For every new or modified endpoint:

* Controller implementation
* `*ControllerTest` (unit)
* `*IT` (integration)

An endpoint is **not considered complete** without all of the above.

---

## 7. Known Gotchas (Read Before Coding)

* **H2 (local)** runs in MySQL compatibility mode

    * PostgreSQL‑specific SQL will fail locally

* Redis keys **must** be prefixed:

```
{tenant}:<key>
```


* CI parity:

    * Always use `./mvnw`
    * Never rely on system Maven

---


**If a tool, agent, or contributor cannot comply with this document, it must not generate code for this repository.**
