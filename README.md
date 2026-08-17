# 🥗 NutriScan — AI-Powered Health Assistant

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![Keycloak](https://img.shields.io/badge/Keycloak-26.6-blueviolet)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL%2017-4169E1)
![Elasticsearch](https://img.shields.io/badge/Search-Elasticsearch%209.4.2-005571)
![Redis](https://img.shields.io/badge/Cache-Redis%207-dc382d)
![RabbitMQ](https://img.shields.io/badge/Events-RabbitMQ%204-ff6600)
![AI](https://img.shields.io/badge/AI-Gemini%2C%20Bedrock%2C%20Tavily-orange)
![Media](https://img.shields.io/badge/Media-Cloudinary-3448c5)
![Deploy](https://img.shields.io/badge/Deployment-Railway-purple)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088ff)

> **NutriScan** is an **AI-powered food-safety & nutrition scanner**. Snap a photo of a product — or scan its barcode — and NutriScan extracts the ingredients, cross-checks them against **your allergies and medical conditions**, and returns a **personalized safety verdict** — building a clean, event-driven, role-secured Spring Boot platform on a modern micro-services-style infrastructure.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Project Goals / Motivation](#project-goals--motivation)
- [Features](#features)
- [AI Food-Safety Pipeline](#ai-food-safety-pipeline)
- [System Architecture](#system-architecture)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Technology Stack](#technology-stack)
- [Event-Driven Programming (RabbitMQ)](#event-driven-programming-rabbitmq)
- [Caching (Redis + Spring Cache)](#caching-redis--spring-cache)
- [Search & Observability (Elasticsearch + Kibana)](#search--observability-elasticsearch--kibana)
- [Authentication & Authorization (Keycloak)](#authentication--authorization-keycloak)
- [API Reference](#api-reference)
- [Design Patterns](#design-patterns)
- [Run the Application](#run-the-application)
- [Deployment (Railway)](#deployment-railway)
- [CI/CD Pipeline (GitHub Actions)](#cicd-pipeline-github-actions)
- [UI State & Mobile Applications](#ui-state--mobile-applications)
- [Demo Video](#demo-video)
- [Team Members](#team-members)

---

## 🧭 Overview

**NutriScan** lets users discover exactly what is in the food they eat and whether it is safe *for them specifically*.

1. **Scan by image** — a photo of a product label or meal is analyzed with a vision-capable LLM (**Gemini OCR**).
2. **Scan by barcode** — the product is looked up in the **OpenFoodFacts** open dataset.
3. **AI safety judgment** — a "judge" LLM combines the extracted ingredients with the **user's stored allergies and medical conditions** to produce a **personalized verdict** (Safe / Caution / Unsafe) with reasons and flagged ingredients.
4. **Enrichment** — nutrition facts are gathered from OpenFoodFacts and **Tavily web search**, with results cached for speed.

Beyond scanning, NutriScan tracks **daily meals & nutrition**, manages **family members**, and keeps every scan **searchable** in Elasticsearch with **autocomplete suggestions**.

### Built for scale, correctness & resilience
- **Keycloak 26.6** as the OAuth2 / OIDC identity provider, extended with a **custom email-HTTP SPI** (Resend SMTP) and a **branded email theme**.
- **RabbitMQ** decouples scan indexing, deletion and user deletion into durable, retrying queues with **DLX/DLQ**.
- **Elasticsearch + Kibana** provide full-text search and observability, with a **PostgreSQL fallback** if ES is unavailable.
- **Redis + Spring Cache** cache AI results so repeat scans don't burn API tokens.
- **Railway** deployment with a custom domain and GitHub Actions CI.

---

## 🎯 Project Goals / Motivation

Every day, people make food choices without full knowledge of what they're consuming. Labels are dense, ingredients are hard to interpret, and — most importantly — what's safe for one person may be **dangerous for another**. A product that is perfectly fine for most people can trigger a life-threatening reaction for someone with a food allergy or a serious medical condition.

NutriScan exists to close that gap. Instead of forcing the user to manually cross-check every ingredient against a list of allergies, NutriScan **automates the judgment**:

- **Scan a product** by photo (OCR) or barcode — the platform extracts the real ingredients for you.
- **Personalized safety verdict** — an AI "judge" compares those ingredients against the user's allergies and medical conditions and returns **Safe / Caution / Unsafe** with the flagged ingredients and reasons.
- **Whole-family coverage** — because safety isn't just about one person, NutriScan lets the user maintain **family member profiles**, each with their own independent allergies and medical conditions. When shopping, the user can verify that a product is safe **not only for themselves, but for each family member**, turning a confusing, stressful grocery run into a confident, informed decision.
- **Daily nutrition tracking** — beyond safety, the app tracks meals and nutrition to support healthier everyday habits.

The motivation is simple: **make food safety knowledge accessible, instant, and personal** — so users can trust what's on their plate, and shop confidently for the people they care about.

---

## ✨ Features

- 🔍 **Image scanning (OCR)** — snap a product label, get its ingredients extracted by a Gemini vision model.
- 🏷️ **Barcode scanning** — resolve a barcode against the **OpenFoodFacts** dataset.
- 🛡️ **Personalized food-safety verdicts** — ingredients checked against the user's **allergies & medical conditions** (Safe / Caution / Unsafe, with flagged ingredients and reasons).
- 🍽️ **Meal analysis** — a meal photo is decomposed into ingredients and safety-checked.
- 📊 **Nutrition facts** — enrichment from OpenFoodFacts and **Tavily** web search.
- 📅 **Daily tracking** — meals, per-meal quantities and aggregated nutrition per day.
- 👨‍👩‍👦 **Family members** — independent allergy/condition profiles for the whole household.
- 🔎 **Elasticsearch search** — full-text search across scans with filters (verdict, status, date) and **autocomplete suggestions**.
- 🔔 **Real-time notifications** — scan status updates pushed over **WebSocket**.
- 🔐 **Authentication & roles** — Keycloak-managed JWT, OAuth2 resource server, custom registration & verification flow.
- 🖼️ **Cloudinary media** — profile & family-member picture upload.
- ⏲️ **Account lifecycle** — soft-delete with a 15-day grace period and restore.
- 🧹 **Operational tooling** — admin reindex endpoint + reconciliation scheduler; scheduled user reconciliation & account deletion.

---

## 🤖 AI Food-Safety Pipeline

We designed the food-safety analysis around an agentic pipeline rather than a fixed linear chain. After OCR, a dedicated model determines whether the image is a **meal** or a **packaged product**, and each path is handled by its own specialized model. For products, the **OCR output itself is evaluated for completeness** — if the extracted data isn't sufficient, a **search model** (via Tavily) is triggered to fill the gaps *before* the data ever reaches the judge. This gives the system the flexibility to self-correct and gather more context instead of failing or guessing on incomplete label scans.

```mermaid
flowchart TD
A[User Action] --> B[Snap a Photo]
A --> C[Scan a Barcode]

    B --> D[Gemini OCR<br/>Vision Model]
    C --> E[OpenFoodFacts<br/>Barcode Lookup]

    D -->|Irrelevant Image| DF[Scan Failed]
    D -->|Detects: Meal| M["Gemini 'Meal Safety' Model<br/>Extracts meal ingredients"]
    D -->|Detects: Product| PCheck{OCR Data<br/>Sufficient?}

    PCheck -->|Yes| F[Extracted Product Data]
    PCheck -->|No| SModel["Gemini 'Search' Model<br/>Tavily Tool Call<br/>Web Search for Product Info"]
    SModel -->|Extracted Ingredients| F

    E --> F

    M --> MC["Checks ingredients against user's<br/>allergies + medical conditions"]
    MC --> H

    F --> G["Gemini 'Judge' Model<br/>Combines product data with user's<br/>allergies + medical conditions"]

    G --> H[Personalized Verdict<br/>SAFE / CAUTION / UNSAFE]
```

### How it works
1. **Image capture** — the user submits a product photo or scans a barcode.
2. **OCR extraction** — a dedicated **Gemini** vision model reads the image. If it's irrelevant (not food-related), the scan fails immediately. Otherwise, it classifies the image as either a **meal** or a **packaged product**.
3. **Meal path** — if OCR detects a **meal**, a separate **Gemini "meal safety" model** takes over: it extracts the meal's ingredients and directly checks them against the user's allergies and medical conditions to produce a verdict.
4. **Product path** — if OCR detects a **packaged product**, the OCR output (ingredients, nutrition facts, product name) is evaluated for completeness.
5. **Search fallback** — if the OCR data is **insufficient**, a dedicated **Gemini "search" model** is triggered before the judge runs. It uses the **Tavily** tool to search the web and is responsible only for **extracting ingredients** from the results, which are then merged into the product data.
6. **Barcode path** — for barcodes, **OpenFoodFacts** is used instead of OCR to retrieve product data directly.
7. **Safety judgment** — for the product path, a **Gemini "judge"** prompt combines the final product data (OCR-only, or OCR + search) with the user's allergies and medical conditions.
8. **Verdict** — the judge (product path) or meal safety model (meal path) returns a structured **SAFE / CAUTION / UNSAFE** verdict personalized to the user's health profile.

### AI providers used

| Provider                      | Role                                                      | Model / Service |
| ----------------------------- | --------------------------------------------------------- | --------------- |
| **Google Gemini** (Spring AI) | OCR / Vision, meal classification, meal safety, judge, search extraction | Gemini Flash    |
| **AWS Bedrock** (Spring AI)   | Alternative structured chat                               | Claude (Sonnet) |
| **OpenAI-compatible**         | Alternative chat client                                   | via opencode.ai |
| **Tavily** (tool)             | Web search fallback when product image data is incomplete | API             |
| **OpenFoodFacts**             | Barcode-based product information lookup                  | REST API        |
---

## 🏗️ System Architecture

```mermaid
flowchart TD
    Clients["Clients<br/>(Web / Mobile)"] --> API

    subgraph API["Spring Boot API (:8080)"]
        direction TB
        Controllers[Controllers]
        Services[Services]
        Repositories[Repositories]
        WebSocket["WebSocket"]

        Controllers --> Orchestration
        Services --> Orchestration
        Repositories --> Orchestration

        subgraph Orchestration["AI Orchestration"]
            AIDesc["Gemini (OCR / Search / Judge)<br/>+ Tavily + Bedrock"]
        end
    end

    API --> Postgres[("PostgreSQL<br/>(app + KC)")]
    API --> Elastic[("Elasticsearch<br/>+ Kibana")]
    API --> Redis[("Redis<br/>(cache)")]
    API --> RabbitMQ[("RabbitMQ<br/>(event bus)")]

    Postgres --> Keycloak["Keycloak 26.6<br/>(auth.nutriscan.dev)<br/>custom email-HTTP SPI<br/>+ branded theme"]
```

**Key architectural principles**
- **Layered architecture** — Controller → Service → Repository (DTO + Mapper separation).
- **Event-driven** — cross-cutting work (indexing, deletion) is decoupled through RabbitMQ.
- **Stateless API** — JWT/OAuth2 resource server, no HTTP sessions.
- **Cache-first AI** — Redis + Spring Cache shrink LLM cost & latency for repeated scans.
- **Fail-fast validation** — Jakarta Bean Validation on every request DTO.
- **Resilience** — DB fallback when Elasticsearch is down; DLQ/retry for failed events.

---

## 📂 Project Structure

```
NutriScan-AI/
│
├── docker-compose.yml                    # PostgreSQL, Keycloak, Redis, ES, Kibana, RabbitMQ, app
├── Dockerfile                            # Multi-stage build (Temurin 21 JRE)
├── pom.xml                               # Spring Boot 4.1.0, Java 21, Spring AI 2.0
├── .env.example                          # Template for all secrets/config
│
├── keycloak/
│   ├── Dockerfile                              # Custom Keycloak 26.6 image (loads theme + SPI)
│   ├── spi/keycloack-email-http-provider/      # Custom email-HTTP SPI (Resend)
│   │   └── src/main/java/gov/iti/jets/keycloak/email/
│   │       ├── HttpEmailSenderProvider.java
│   │       └── HttpEmailSenderProviderFactory.java
│   ├── realm-config/import/nutriscan-realm.json
│   └── themes/nutriscan/                 # Branded login + email templates (FreeMarker)
│       ├── login/*.ftl + css
│       └── email/html/*.ftl
│
├── .github/
│   └── workflows/ci.yml                  # Lint (Spotless) + Build jobs
│
└── src/
    ├── main/java/gov/iti/jets/NutriScan/
    │   ├── NutriScanApplication.java      # Spring Boot entry point
    │   │
    │   ├── config/                        # Security, WebSocket, Security (JWT), Redis, Rabbit, Cloudinary, AI (Gemini/Bedrock/OpenAI), Async, Swagger, Time, HTTP
    │   ├── controller/                    # 8 REST controllers
    │   ├── service/                       # 12 service classes (+ AI orchestrators)
    │   ├── repository/                    # JPA repositories + specifications
    │   ├── listener/                      # Scan status / search sync / event publisher listeners
    │   ├── scheduler/                     # Reconciliation, user & account-deletion schedulers
    │   ├── security/                      # JWT converter, WebSocket interceptor, deletion filter
    │   ├── mapper/                        # MapStruct mappers
    │   ├── model/                         # 16+ JPA entities & value objects
    │   ├── dto/                           # Request/Response DTOs
    │   ├── ai/                            # AI clients, JSON-schema definitions, Tavily tool
    │   ├── exception/                     # Global handler + custom exceptions
    │   └── util/                          # Prompts, cache keys, image validation
    │
    └── main/resources/
       ├── application.properties              # default (local, docker profile)
       ├── application-docker.properties       # internal docker-network overrides
       ├── application-production.properties   # Railway/Cloud overrides
       └── db/migration/                       # Flyway migrations
    
```

---

## 🗄️ Database Schema

The application schema is managed and versioned by **Flyway** migrations (`src/main/resources/db/migration`). The domain model is organized around a central `User` entity with related entities for safety, nutrition, and family coverage:

- **User & profile** — `users`, profile data, `user_allergy` / `user_disease` link tables, and account lifecycle state.
- **Family members** — `family_member` with independent `family_member_allergy` / `family_member_disease` profiles, so the safety verdict can be computed for each household member.
- **Scans** — `scan` records with ingredients, verdict, flagged ingredients, and status (the async pipeline output).
- **Daily tracking** — `daily_tracking` and `daily_tracking_meal` (join to scans) for per-day meal & nutrition aggregation.
- **Search sync** — `elasticsearch_sync` for the ES ↔ DB reconciliation and indexing bookkeeping.

![Database Schema](images/database_schema.webp)

---

## 🛠️ Technology Stack

### Backend

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Language | Java | 21 (Temurin) | Core backend development |
| Framework | Spring Boot | 4.1.0 | Application framework |
| ORM | Hibernate / Spring Data JPA | - | Object-Relational Mapping |
| Migrations | Flyway | (Boot-managed) | Versioned DB schema migrations |
| Security | Spring Security + OAuth2 Resource Server | - | JWT validation, role-based access |
| IAM | Keycloak | 26.6 | OAuth2 / OIDC identity provider |
| Event Bus | RabbitMQ (AMQP) | 4 | Event-driven messaging, DLX/DLQ |
| Cache | Redis + Spring Cache | 7 | AI result caching, data caching |
| Search | Elasticsearch | 9.4.2 | Full-text scan search + autocomplete |
| Observability | Kibana | 9.1.4 | Dashboards over ES indices |
| AI / LLM | Spring AI | 2.0.0 | Gemini, Bedrock, OpenAI-compatible clients |
| Web Search | Tavily (tool) | API | Nutrition fact web search |
| Food Dataset | OpenFoodFacts | REST API | Barcode → product/ingredient lookup |
| Media | Cloudinary | 1.39.0 | Image upload & hosting |
| Real-time | Spring WebSocket (STOMP) | - | Scan status push notifications |
| API Docs | SpringDoc OpenAPI | 2.8.5 | Swagger UI / OpenAPI spec |
| Email | Resend (HTTP) via custom Keycloak SPI | - | Verification & password-reset emails |
| Parsing | Jsoup | 1.18.1 | HTML content handling |
| Mapping | MapStruct | 1.5.5 | Bean mapping |
| Boilerplate | Lombok | - | Reduces boilerplate |

### Infrastructure

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| App Database | PostgreSQL | 17 (Alpine) | Application relational data |
| IAM Database | PostgreSQL | 17 (Alpine) | Keycloak data |
| Search Engine | Elasticsearch | 9.4.2 | Product/scan indexing |
| Dashboards | Kibana | 9.1.4 | Search observability |
| Message Broker | RabbitMQ | 4 (management) | Event queues + DLQs |
| Cache Store | Redis | 7 (Alpine) | Spring Cache backend |
| Auth Server | Keycloak | 26.6 | Custom build (theme + SPI) |
| Reverse proxy / hosting | Railway | - | Managed deployment + custom DNS |

### Build & Deployment

| Tool | Purpose |
|------|---------|
| Build Tool | Maven (with wrapper) |
| Package Format | JAR (Spring Boot executable) |
| Formatting | Spotless Maven Plugin (Eclipse style) |
| CI | GitHub Actions (lint + build) |
| Hosting | Railway (`nutriscan.dev` + custom DNS) |
| IAM Hosting | Railway (`auth.nutriscan.dev` + custom DNS, SMTP via Resend) |

---

## 📬 Event-Driven Programming (RabbitMQ)

NutriScan decouples expensive/cross-cutting work from request handling using RabbitMQ, with **publisher-confirms** and **mandatory returns** for reliable delivery.

```mermaid
flowchart TD
    MainExchange["scan.exchange<br/>(topic)"]
    DLXExchange["scan.dlx<br/>(topic - dead letter)"]

    MainExchange -->|scan.index| IndexQueue["scan.index.queue"]
    MainExchange -->|scan.index.retry| IndexRetryQueue["scan.index.retry.queue"]
    MainExchange -->|scan.delete| DeleteQueue["scan.delete.queue"]
    MainExchange -->|user.delete| UserDeleteQueue["user.delete.queue"]

    IndexQueue -.dead-letter.-> DLXExchange
    DeleteQueue -.dead-letter.-> DLXExchange
    UserDeleteQueue -.dead-letter.-> DLXExchange

    DLXExchange -->|scan.index| IndexDLQ["scan.index.dlq"]
    DLXExchange -->|scan.delete| DeleteDLQ["scan.delete.dlq"]
    DLXExchange -->|user.delete| UserDeleteDLQ["user.delete.dlq"]

    IndexRetryQueue -.requeue after delay.-> MainExchange
```

### Queue topology
| Queue | Routing key | Purpose | Dead-letter queue |
|-------|------------|---------|-------------------|
| `scan.index.queue` | `scan.index` | Elasticsearch indexing of a new scan | `scan.index.dlq` |
| `scan.index.retry.queue` | `scan.index.retry` | Retry after transient failures | — |
| `scan.delete.queue` | `scan.delete` | Remove scan from Elasticsearch / cleanup | `scan.delete.dlq` |
| `user.delete.queue` | `user.delete` | Cascade user data deletion on account delete | `user.delete.dlq` |

### Reliability guarantees
- **DLX/DLQ per queue** — failed messages are routed to a dead-letter queue for later inspection, never silently lost.
- **Retry queue** — transient failures go through a dedicated retry queue before final rejection.
- **Publisher confirms & returns** — the producer is notified when a message is confirmed or returned (routed nowhere), enabling compensating logic.
- **Exactly-once indexing intent** — events are published after the DB transaction commits (`@TransactionalEventListener`), keeping PostgreSQL and Elasticsearch consistent.

---

## ⚡ Caching (Redis + Spring Cache)

NutriScan uses **Redis 7** as the Spring Cache backend. Expensive work — LLM inference, external API calls, and frequently read reference data — is memoized with `@Cacheable` so repeat requests resolve instantly and never hammer upstream systems. Each cache has its own TTL, configured in `RedisCacheConfig` (the default TTL is 15 days).

| Cache name | Key | TTL | What it caches |
|-----------|-----|-----|----------------|
| `aiJudge` | ingredients + allergies + conditions | 1 day | Personalized food-safety verdict |
| `aiBarcode` | barcode + allergies + conditions | 3 days | Barcode-derived safety verdict |
| `aiSearch` | product name | 2 days | Nutrition facts from Tavily/search |
| `openFoodFacts` | barcode | default (15 days) | OpenFoodFacts product lookup response (unless `null`) |
| `userAllergiesAndConditions` | user id | 24 hours | User's allergies & medical conditions |
| `userProfile` | user `sub` | 1 day | User profile data |
| `userSummary` | user `sub` | 1 day | User summary |
| `scans` | `scanId` + user `sub` | 1 day | Old scan results (not cached while still `PROCESSING`) |
| `allergies` | id / name / `all` | default (15 days) | Allergies reference data |
| `diseases` | id / name / `all` | default (15 days) | Diseases / medical conditions reference data |

Why it matters:
- Repeated scans of the same product with the same user profile return **instantly** without calling the LLM.
- Frequently read reference data (allergies, diseases, OpenFoodFacts, profiles, old scans) is served from Redis instead of the database or external APIs.
- **Key generation** is centralized in `CacheKeys` so cache keys stay stable across invocations.
- **Selective caching** — `aiSearch` skips caching "unknown"/blank product names, and `scans` only caches completed (non-`PROCESSING`) results, so users always see live progress.

---

## 🔎 Search & Observability (Elasticsearch + Kibana)

### Elasticsearch search
- Full-text search across scans with filters on **verdict, scan status, and date**.
- **Fuzzy search** — typo-tolerant matching. Queries of **3+ characters** enable `fuzziness: AUTO`, so even misspelled product names (e.g. "choclete" → "chocolate") still match. Short queries (1–2 chars) are matched exactly to avoid noisy results.
- **Autocomplete suggestions** (`/api/v1/scans/suggestions`) using edge n-gram analyzers (`productName.suggest._2gram` / `_3gram`) plus boolean-prefix multi-match.
- **Indexing is event-driven** via RabbitMQ (see above) so the DB and index stay in sync.
- **PostgreSQL fallback** — if Elasticsearch is down, or returns no results yet, queries gracefully fall back to the database so the API never breaks.
- A **reconciliation scheduler** re-indexes any out-of-sync rows, and an **admin reindex** endpoint rebuilds the whole index on demand.

#### Search flow: Elasticsearch + RabbitMQ + PostgreSQL fallback

```mermaid
flowchart TD
    subgraph Indexing["Event-Driven Indexing"]
        direction TB
        Event["Scan created / updated / deleted"]
        Event -->|"@TransactionalEventListener<br/>(only after DB COMMIT succeeds)"| Publisher[ScanEventPublisher]
        Publisher -->|publish| Exchange["RabbitMQ<br/>scan.exchange"]

        Exchange -->|routing key| IndexQueue["scan.index.queue<br/>(or retry queue)"]
        Exchange -->|routing key| DeleteQueue["scan.delete.queue"]

        IndexQueue -->|index/update doc| ES[("Elasticsearch<br/>scans index")]
        DeleteQueue -->|delete doc| ES
    end

    subgraph Search["User Search Flow"]
        direction TB
        Request["User Search Request<br/>(query / verdict / status / date)"]
        Request --> HasFilters{Any filters?}

        HasFilters -->|No| PGOnly["PostgreSQL only<br/>(Specification)"]

        HasFilters -->|Yes| SearchService["ScanSearchService<br/>query Elasticsearch<br/>(fuzzy match)"]

        SearchService -->|"success (hits > 0)"| FetchDetails["Fetch scan details from<br/>PostgreSQL by IDs,<br/>preserve ES ordering"]

        SearchService -->|"Elasticsearch down<br/>OR returns 0 results"| Fallback["FALLBACK → PostgreSQL<br/>searchUsingSpecification()<br/>(JPA Criteria + Specification)"]
    end
```

### Kibana
- **Kibana 9.1.4** connects to Elasticsearch to visualize scan data, indices and search health — the platform's observability layer.

---

## 🔐 Authentication & Authorization (Keycloak)

NutriScan uses **Keycloak 26.6** as its identity provider, deployed as a **custom image** on `https://auth.nutriscan.dev`.

<p align="center">
  <img src="images/authentication.png" alt="authentication">
</p>

### Customization
- **Custom email-HTTP SPI** — a bespoke `EmailSenderProvider` that sends verification and password-reset emails through the **Resend HTTP API** instead of Keycloak's SMTP provider.
- **Branded theme** — a `nutriscan` theme with custom FreeMarker **login**, **error**, **password-reset** and **user-info** templates plus CSS, and branded **email templates**.
- **Realm** — `nutriscan` realm imported from `keycloak/realm-config/import/nutriscan-realm.json` (clients, roles, SMTP config).
- **Custom DNS** — Keycloak served behind `auth.nutriscan.dev`.

During Spring Boot startup, the app calls the **Keycloak Admin Client** (`keycloak-admin-client 26.0.8`) to ensure users exist locally, and a scheduled **user-reconciliation** keeps identities in sync.

---

## 📖 API Reference

Interactive docs are available at `/swagger-ui.html` when the app is running (`/v3/api-docs` for the OpenAPI spec).

### Public / Discovery endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/allergies` | All allergies |
| GET | `/api/v1/allergies/{id}` | Allergy by id |
| GET | `/api/v1/diseases` | All diseases/conditions |
| GET | `/api/v1/diseases/{id}` | Disease by id |

### Authentication endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/register` | Register a new user (Keycloak + local provisioning) |
| POST | `/api/v1/auth/resend-verification` | Resend verification email |
| POST | `/api/v1/auth/forgot-password` | Send password-reset email |
| POST | `/api/v1/auth/login` | Login (Keycloak OAuth2) |

### User endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/users/me` | Current user summary |
| GET | `/api/v1/users/profile` | Current user profile |
| PATCH | `/api/v1/users/profile` | Update profile |
| POST | `/api/v1/users/profile/image` | Upload profile picture |
| PATCH | `/api/v1/users/family-member/{id}/image` | Upload family-member picture |
| POST | `/api/v1/users/me/daily-streak` | Update daily streak |
| DELETE | `/api/v1/users/profile` | Schedule account deletion (grace period) |
| POST | `/api/v1/users/profile/restore` | Restore a soft-deleted account |

### Scan endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/scans` | Scan a product image (OCR) — async, returns 202 |
| POST | `/api/v1/scans/barcode` | Scan a barcode (OpenFoodFacts) — 202 |
| GET | `/api/v1/scans` | Search scans (query, verdict, status, date, page) |
| GET | `/api/v1/scans/favorites` | Favorite scans |
| GET | `/api/v1/scans/{scanId}` | Scan result |
| PATCH | `/api/v1/scans/{scanId}` | Rename / favorite a scan |
| DELETE | `/api/v1/scans/{scanId}` | Delete a scan |
| GET | `/api/v1/scans/suggestions` | Autocomplete suggestions |

### Daily tracking endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/daily-tracking/today` | Today's tracking |
| GET | `/api/v1/daily-tracking/{date}` | Tracking for a date |
| GET | `/api/v1/daily-tracking` | All tracking (paginated) |
| PATCH | `/api/v1/daily-tracking/{date}` | Update a day's tracking |
| POST | `/api/v1/daily-tracking/{date}/meals` | Add a meal to a day |
| PUT | `/api/v1/daily-tracking/{date}/meals/{scanId}` | Update a meal quantity |
| DELETE | `/api/v1/daily-tracking/{date}/meals/{scanId}` | Remove a meal |
| DELETE | `/api/v1/daily-tracking/{date}` | Delete a day's tracking |

### Admin endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/admin/search/reindex` | Rebuild the full Elasticsearch index |

All scan/health data endpoints are secured with **OAuth2 / JWT** and role-based authorization enforced by `SecurityConfig`.

---

## 🚀 Deployment (Railway)

NutriScan is deployed on **Railway** using separate services for the application and its supporting infrastructure.

### Production Services

| Service                   | URL                            | Purpose                                        |
|---------------------------| ------------------------------ | ---------------------------------------------- |
| **NutriScan Application** | **https://nutriscan.dev**      | Production REST API                            |
| **Keycloak**              | **https://auth.nutriscan.dev** | OAuth2 / OIDC authentication and authorization |
| **Swagger UI**            | **https://nutriscan.dev/swagger-ui/index.html** | The production REST API is documented using Swagger / OpenAPI |

The application uses the `nutriscan` Keycloak realm, with JWT validation configured against:

```text
https://auth.nutriscan.dev/realms/nutriscan
```

### Infrastructure

Railway hosts the following managed services:

* **PostgreSQL** — application and Keycloak persistence
* **Redis** — caching and temporary data
* **RabbitMQ** — asynchronous messaging
* **Elasticsearch** — search and indexing
* **Kibana** — Elasticsearch management and visualization
* **RabbitMQ Web UI** — messaging administration

Persistent volumes are used for stateful services to preserve data across deployments and restarts.

### Email

Transactional emails, including **account verification** and **password reset**, are delivered through **Resend**.

### Configuration & Security

Production configuration is provided through Railway environment variables. Sensitive credentials and secrets are not stored in the repository.

### Railway deployment Architecture
![deployment.png](images/deployment.png)

---

## 🔄 CI/CD Pipeline (GitHub Actions)

Continuous integration runs via **`.github/workflows/ci.yml`** and is triggered on **push to any branch** and **pull requests to `main` / `develop`**. The pipeline contains **two jobs**:

| Job | Runs on | Steps |
|-----|---------|-------|
| **lint** | `ubuntu-latest` | `actions/checkout` → `setup-java` (Temurin 21, Maven cache) → `mvn spotless:check` (formatting validation) |
| **build** | `ubuntu-latest` (`needs: lint`) | `actions/checkout` → `setup-java` (Temurin 21, Maven cache) → `mvn clean package -DskipTests` |

The build job only runs **after** the lint job passes, ensuring only well-formatted, compiling code reaches later stages.

```yaml
# .github/workflows/ci.yml — summary
jobs:
  lint:   # Spotless formatting check
  build:  # mvn clean package (depends on lint)
```
---

## 🧩 Design Patterns

NutriScan applies a set of established design patterns to keep the codebase layered, decoupled, testable, and resilient:

| Pattern | Where it's applied | Purpose |
|---------|--------------------|---------|
| **Layered Architecture** | `controller` → `service` → `repository` split | Separates HTTP, business logic, and persistence concerns |
| **DTO + Mapper** | `dto/` + `mapper/` (MapStruct) | Decouples API contracts from domain entities |
| **Repository** | Spring Data JPA `repository/` | Hides data-access details behind domain-oriented interfaces |
| **Specification / Criteria** | `ScanSpecification` + JPA Criteria | Dynamic, type-safe query building for search & filters |
| **Controller Adapter** | `controller/` | Translates HTTP requests into service calls and DTO responses |
| **Factory** | `AiConfig` (`BedrockGatewayStructuredChatClient`, ...) | Creates the right AI provider / client instances |
| **Strategy (Pluggable AI)** | `StructuredChatClient` + implementations | Swaps between Gemini / Bedrock / OpenAI-compatible chats |
| **Observer / Event-Driven** | `event/` + `listener/` (RabbitMQ) | Decouples side-effects (indexing, deletion) from request flow |
| **Template Method** | `ai/` JSON-schema driven prompts (`Prompts`) | Shares a fixed analysis flow while varying provider details |
| **Command / Scheduler** | `scheduler/` (reconciliation, account deletion) | Encapsulates scheduled jobs as dedicated components |
| **Strategy (Cache keys)** | `util/CacheKeys` | Centralizes cache-key generation for stable `@Cacheable` lookups |
| **Global Exception Handling** | `exception/GlobalExceptionHandler` | Centralizes error mapping to consistent API responses |
| **Facade (Orchestration)** | `AiService` orchestrating OCR, barcode, judge, Tavily | Simplifies a complex AI pipeline into one entry point |
| **Value Object** | `model/` (`ScanStatus`, `Verdict`, `Gender`, ...) | Encapsulates small, well-typed concepts |
| **Builder (Lombok)** | throughout the model/DTO layer | Reduces boilerplate for immutable object construction |

> **Note on Event-Driven & Layered** — the combination of a **Layered Architecture** with an **Event-Driven** backbone via RabbitMQ (`@TransactionalEventListener` + DLX/DLQ) is what makes the platform scalable and resilient to partial infrastructure failures.

---

## ▶️ Run the Application

### Prerequisites

- **JDK 21** (Temurin)
- **Maven 3.9+** (or the included `./mvnw`)
- **Docker & Docker Compose**
- API keys: **Gemini**, **Tavily**, **Cloudinary**, **Resend**, and optionally **Bedrock** / **opencode.ai**

### Step 1 — Configure environment

Copy `.env.example` to `.env` and fill in your secrets:

```properties
DB_URL=jdbc:postgresql://localhost:5432/nutri_scan
DB_USERNAME=admin
DB_PASSWORD=admin

KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOCK_SMTP_PASSWORD=secret

RESEND_API_KEY=re_...
OPENCODE_API_KEY=...

GEMINI_API_KEY_OCR=...
GEMINI_API_KEY_JUDGE=...
GEMINI_API_KEY_SEARCH=...

CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...

TAVILY_API_KEY=tvly-...

BEDROCK_API_KEY=...

RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

### Step 2 — Start the infrastructure + app with Docker Compose

```bash
docker compose up --build
```

This starts **PostgreSQL (app + Keycloak), Keycloak, Redis, Elasticsearch, Kibana, RabbitMQ**, and the **NutriScan** application itself — all with healthchecks and auto-restart.

### Step 3 — Verify & access

| Service | URL |
|---------|-----|
| **NutriScan API** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI Spec** | `http://localhost:8080/v3/api-docs` |
| **Keycloak** | `http://localhost:8081` |
| **Elasticsearch** | `http://localhost:9200` |
| **Kibana** | `http://localhost:5601` |
| **RabbitMQ Management** | `http://localhost:15672` |
| **App Health** | `http://localhost:8080/actuator/health` |

---

## 📱 UI State & Mobile Applications

This REST API is backend-ready and designed to be consumed by **mobile applications**. Another team builds the mobile clients on top of this backend, sharing the same endpoints, DTO contracts, security flow (Keycloak OAuth2), WebSocket notifications, and feature set described throughout this document.

### Mobile clients

| Platform | Language | Repository                                                     |
|----------|----------|----------------------------------------------------------------|
| **iOS** | Swift | [`NutriScan-iOS`](https://github.com/OTech-Company/NutriScan)  |
| **Android** | Kotlin | [`NutriScan-Android`](https://github.com/yusefellban/NutriScan) |

Both apps authenticate through the same Keycloak realm, call the same `/api/v1` endpoints, and consume the identification/role model exactly as described in **Authentication & Authorization**.

---
## 📽️ Demo Video

---

## 👥 Team Members
- **Ibrahim Gad**
- **Ibrahim Soliman**
- **Reem Mohy**

---

Thank you for exploring **NutriScan — Stay Safe** ✨
