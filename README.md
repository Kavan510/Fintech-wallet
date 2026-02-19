# High-Impact FinTech Wallet Service

A robust, production-ready Backend Wallet System built with **Java Spring Boot**. This project demonstrates advanced backend concepts required for financial applications, focusing on data integrity, concurrency control, and system reliability.

## Key Engineering Features

* **Financial Integrity (Double-Entry Ledger):** Implemented a ledger-based system where every balance change is backed by an immutable transaction record to ensure auditability.
* **Concurrency Management:** Solved the "Double-Spending" problem using **JPA Optimistic Locking (@Version)**, ensuring data consistency even under high-concurrency scenarios.
* **API Idempotency:** Integrated a custom idempotency mechanism using unique `X-Idempotency-Key` headers to prevent duplicate charges from network retries.
* **ACID Compliance:** Guaranteed atomic operations across multiple database updates using **Spring Declarative Transaction Management**.
* **Professional Error Handling:** Implemented a Global Exception Handler to map business logic failures (like `InsufficientFunds`) into standardized, secure REST responses.

## Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA
* **Database:** PostgreSQL (Primary Store)
* **Cache/Store:** Redis (Potential for Session/Idempotency)
* **DevOps:** Docker, Docker Compose
* **Testing:** JUnit 5, AssertJ

## Architecture Detail

The system follows a **Service-Layer Architecture** with a focus on atomic transitions:
1.  **Request Validation:** Checks for valid inputs and account existence.
2.  **Idempotency Check:** Verifies if the request was previously processed.
3.  **Optimistic Lock Check:** Compares versions to prevent race conditions.
4.  **Balance Update:** Atomic debit/credit operations.
5.  **Audit Logging:** Success/Failure recorded in the Transaction table.

## Getting Started

### Prerequisites
* Docker & Docker Compose
* JDK 17+
* IntelliJ IDEA (Recommended)

### Installation & Run
1. Clone the repository:
2. Start the infrastructure (Postgres/Redis):
```bash
    docker-compose up -d
```
3. Run the application via IntelliJ or CLI: