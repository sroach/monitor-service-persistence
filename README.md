# Monitor Service Persistence

A Spring Boot service for storing and retrieving monitoring data. This service provides a REST API for persisting monitoring records and retrieving them based on various criteria.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Building and Running](#building-and-running)
- [API Documentation](#api-documentation)
  - [Endpoints](#endpoints)
  - [Request/Response Examples](#requestresponse-examples)
- [Configuration](#configuration)
  - [Application Properties](#application-properties)
  - [Data Retention](#data-retention)
- [Database](#database)
- [Monitoring and Management](#monitoring-and-management)

## Overview

The Monitor Service Persistence is designed to store monitoring data from various sources. It provides endpoints for:

- Creating new monitoring records
- Retrieving monitoring records by ID, name, or time range
- Deleting monitoring records
- Automatic cleanup of old monitoring data

The service uses an H2 database for storage and provides a Swagger UI for API documentation and testing.

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (or use the included Gradle wrapper)

### Environment Variables

The following environment variables are required:

- `DB_USER` - Database username
- `DB_PASSWORD` - Database password

### Building and Running

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/monitor-service-persistence.git
   cd monitor-service-persistence
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run the application:
   ```
   ./gradlew bootRun
   ```

4. Access the application:
   - API: http://localhost:7201/monitor-persistence/api/records
   - Swagger UI: http://localhost:7201/monitor-persistence/swagger-ui.html
   - H2 Console: http://localhost:7201/monitor-persistence/h2-console

## API Documentation

### OpenAPI Specification and Swagger UI

The service provides an OpenAPI specification that documents all available endpoints, request/response formats, and authentication requirements:

- **OpenAPI JSON**: http://localhost:7201/monitor-persistence/api-docs
- **Swagger UI**: http://localhost:7201/monitor-persistence/swagger-ui.html

The Swagger UI provides an interactive interface to:
- Explore all available API endpoints
- Test API calls directly from your browser
- View request/response schemas and examples
- Understand authentication requirements

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/records` | Create a new monitor record |
| GET | `/api/records` | Get all monitor records |
| GET | `/api/records/{id}` | Get a monitor record by ID |
| GET | `/api/records/name/{name}` | Get monitor records by name |
| GET | `/api/records/last/{seconds}` | Get monitor records from the last n seconds |
| DELETE | `/api/records/{id}` | Delete a monitor record by ID |

### Request/Response Examples

#### Create a new monitor record

Request:
```http
POST /monitor-persistence/api/records
Content-Type: application/json

{
  "name": "Google",
  "url": "https://www.google.com",
  "statusCode": 200,
  "responseTimeMs": 150,
  "errorMessage": null
}
```

Response:
```json
{
  "id": 1,
  "name": "Google",
  "url": "https://www.google.com",
  "statusCode": 200,
  "timestamp": "2023-06-15T10:15:30",
  "responseTimeMs": 150,
  "errorMessage": null
}
```

#### Get all monitor records

Request:
```http
GET /monitor-persistence/api/records
```

Response:
```json
[
  {
    "id": 1,
    "name": "Google",
    "url": "https://www.google.com",
    "statusCode": 200,
    "timestamp": "2023-06-15T10:15:30",
    "responseTimeMs": 150,
    "errorMessage": null
  },
  {
    "id": 2,
    "name": "GitHub",
    "url": "https://github.com",
    "statusCode": 200,
    "timestamp": "2023-06-15T10:16:45",
    "responseTimeMs": 250,
    "errorMessage": null
  }
]
```

#### Get monitor records by name

Request:
```http
GET /monitor-persistence/api/records/name/Google
```

Response:
```json
[
  {
    "id": 1,
    "name": "Google",
    "url": "https://www.google.com",
    "statusCode": 200,
    "timestamp": "2023-06-15T10:15:30",
    "responseTimeMs": 150,
    "errorMessage": null
  }
]
```

#### Get monitor records from the last n seconds

Request:
```http
GET /monitor-persistence/api/records/last/3600
```

Response:
```json
[
  {
    "id": 1,
    "name": "Google",
    "url": "https://www.google.com",
    "statusCode": 200,
    "timestamp": "2023-06-15T10:15:30",
    "responseTimeMs": 150,
    "errorMessage": null
  },
  {
    "id": 2,
    "name": "GitHub",
    "url": "https://github.com",
    "statusCode": 200,
    "timestamp": "2023-06-15T10:16:45",
    "responseTimeMs": 250,
    "errorMessage": null
  }
]
```

## Configuration

### Application Properties

The application can be configured using the following properties in `application.yml`:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 7201 |
| `server.servlet.context-path` | Context path | /monitor-persistence |
| `spring.datasource.url` | Database URL | jdbc:h2:file:./data/monitordb |
| `spring.datasource.driver-class-name` | Database driver | org.h2.Driver |
| `spring.datasource.username` | Database username | ${DB_USER} |
| `spring.datasource.password` | Database password | ${DB_PASSWORD} |
| `spring.h2.console.enabled` | Enable H2 console | true |
| `spring.h2.console.path` | H2 console path | /h2-console |
| `springdoc.api-docs.path` | OpenAPI docs path | /api-docs |
| `springdoc.swagger-ui.path` | Swagger UI path | /swagger-ui.html |

### Data Retention

The application automatically cleans up old monitoring data to keep the database size manageable. This behavior can be configured using the following properties:

| Property | Description | Default |
|----------|-------------|---------|
| `monitor.data.retention.days` | Number of days to keep monitor records | 7 |
| `monitor.data.cleanup.cron` | Cron expression for the cleanup job | 0 0 0 * * ? (midnight every day) |

## Database

The application uses an H2 database for storage. The database file is stored in the `data` directory by default. The database schema is automatically created and updated by Hibernate.

## Monitoring and Management

The application exposes various endpoints for monitoring and management through Spring Boot Actuator:

- Health: http://localhost:7201/monitor-persistence/actuator/health
- Info: http://localhost:7201/monitor-persistence/actuator/info
- Metrics: http://localhost:7201/monitor-persistence/actuator/metrics
- Prometheus: http://localhost:7201/monitor-persistence/actuator/prometheus

These endpoints can be used to monitor the health and performance of the application.
