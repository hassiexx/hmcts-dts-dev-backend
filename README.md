# HMCTS DTS Developer - Backend Tasks API

A Spring Boot REST API for managing tasks. This backend service provides CRUD operations for task management with database persistence using PostgreSQL.

This was developed as part of the coding challenge for the HMCTS DTS Developer role.

## Project Overview

- **Framework**: Spring Boot 4.0.6
- **Language**: Java 25
- **Database**: PostgreSQL
- **Database migrations**: Flyway
- **Build Tool**: Gradle
- **API Documentation**: Swagger OpenAPI 3.0

## Project Structure

```
src/
├── main/
│   ├── java/boo/hassie/java/hmcts/dts/tasks/
│   │   ├── TasksApplication.java          # Main Spring Boot application
│   │   ├── config/                        # Spring Boot configurations
│   │   ├── controller/                    # REST controllers
│   │   ├── dto/                           # Data transfer objects
│   │   ├── entity/                        # JPA entities
│   │   ├── exception/                     # Custom exceptions
│   │   ├── mapper/                        # DTO and entity mappers
│   │   ├── repository/                    # JPA repositories for data access
│   │   ├── service/                       # Business logic
│   │   └── validator/                     # Input validators
│   └── resources/
│       ├── application.yaml               # Application configuration
│       └── db/migration/                  # Flyway database migrations
│
└── test/...                               # Unit tests
```


## Prerequisites

- **Java 25** or later
- **A recent version of PostgreSQL** (v18 was used during development)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone git@github.com:hassiexx/hmcts-dts-dev-backend.git
cd hmcts-dts-dev-backend
```

### 2. Configure PostgreSQL Database

Ensure PostgreSQL is running and create the database:

```bash
psql -U postgres -c "CREATE DATABASE hmcts_tasks;"
```

### 3. Update application configuration (including database credentials) if needed

See `src/main/resources/application-dev.yaml`

### 4. Build the Project

```bash
./gradlew clean build
```

Or on Windows:

```bash
gradlew.bat clean build
```

### 5. Run the Application

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The application will start on `http://localhost:8080` or whichever port is configured in application.yml

## Swagger API Documentation

The API documentation is available at the root URL e.g.

```
http://localhost:8080/
```

## Testing

```bash
./gradlew test
```
