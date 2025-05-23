# User Management System

The **User Management System** is a microservices-based application designed to manage users and their roles, with support for event-driven communication using Kafka.

## Components

### 1. User Service

- User registration and login.
- User CRUD operations.
- Role assignment and management.
- Publishes structured UserEvent objects to a Kafka topic (user-events).
- Persistence: PostgreSQL database with users and roles tables.

### 2. Journal Service

- Subscribes to the user-events Kafka topic.
- Deserializes UserEvent messages.
- Logs relevant actions (CREATE, UPDATE, DELETE, LOGIN) into a journal_entries table.
- Persistence: PostgreSQL database with journal_entries table.

### 3. Kafka
- Topic: user-events
- Used for decoupling user actions from journal/audit persistence.

## Prerequisites

- Docker and Docker Compose installed on your system.

## Event Flow

1. User action (register, update, delete, login) triggers in User Service.

2. KafkaService constructs a UserEvent object with:

- userId
- action (CREATED, UPDATED, DELETED, LOGIN)
- timestamp
- payloadJson (the actual user request serialized)

3. Event is published to Kafka topic user-events.

4. JournalListener in Journal Service listens to the topic.

5. Event is deserialized and persisted in the journal_entries table.

## Endpoints

- POST /auth/register – Register a user.

- POST /auth/login – Authenticate and receive JWT.

- GET /users, PUT, DELETE – Manage users.

- POST /roles, GET /roles – Manage roles.

  (For more details, refer APISpecification.md)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/giriswamy-gan/user-management-system
cd user-management-system
```

### 2. Build and Start the Services

Run the following command to build and start all services:

```bash
docker-compose up -d
```

### NOTE

Please wait 1 minute after the kafka service starts before trying to hit any APIs. You can see docker logs for user-service and journal-service before starting to hit those APIs.

On initial container start-up via docker-compose, the app:
Seeds the roles table with a default ROLE_ADMIN.

Seeds the users table with an admin user:
Username: admin
Password: password

Ensures admin is assigned ROLE_ADMIN.

To access the services, first login using this curl:

```bash
curl --location 'localhost:8081/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7B8890E2CA6955D5C9E41CA0E04D6DC4' \
--data '{
    "username": "admin",
    "password": "password"
}'
```

/auth/login and /auth/register are public APIs and can be accessed by anyone.

To register a new user use this curl:

```bash
curl --location 'localhost:8081/auth/register' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7B8890E2CA6955D5C9E41CA0E04D6DC4' \
--data '{
     "username": "ganesh",
    "password": "password",
    "fullName": "ganesh g",
    "roleName": ["ROLE_USER"]
}'
```

But you have to create the appropriate role first. Refer the postman collection for accessing all the APIs.

On successful login, you can access all the APIs. Copy the token generated in the login API and use it as Bearer token to authorize all the APIs. Some APIs for deleting user, updating user need ROLE_ADMIN to access.

Accessing any of the journal API needs ADMIN access.

(Unit testing is done for all the main services)

### 3. Access the Services

- **User Service**: [http://localhost:8081](http://localhost:8081)
- **Journal Service**: [http://localhost:8082](http://localhost:8082)

### 4. Verify Health Checks

- **User Service Health**: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)
- **Journal Service Health**: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)

### Environment Variables

The following environment variables are used in the `docker-compose.yml` file:

### Database Configuration:
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `POSTGRES_DB`

### Kafka Configuration:
- `KAFKA_BOOTSTRAP_SERVERS`

### JWT Configuration:
- `JWT_SECRET`
- `JWT_EXPIRATION`

## Services Overview

### User Service
- **Port**: 8081
- **Description**: Manages user registration, authentication, and roles.
- **Dependencies**: PostgreSQL (userdb), Kafka.

### Journal Service
- **Port**: 8082
- **Description**: Logs user-related events for auditing purposes.
- **Dependencies**: PostgreSQL (journaldb), Kafka.

### Kafka
- **Port**: 9092
- **Description**: Facilitates communication between services.

### PostgreSQL
- **User Database**:
  - **Port**: 5434
  - **Database Name**: userdb
- **Journal Database**:
  - **Port**: 5435
  - **Database Name**: journaldb

## Troubleshooting

### Common Issues

#### Kafka Connection Issues:
- Ensure Kafka is running and accessible at `localhost:9092`.
- Check the `KAFKA_ADVERTISED_LISTENERS` configuration in `docker-compose.yml`.

#### Database Connection Issues:
- Verify that the PostgreSQL containers (`userdb` and `journaldb`) are running.
- Check the `SPRING_DATASOURCE_URL` environment variables.

#### Build Failures:
- Ensure Maven is installed and run `mvn clean install` in the root directory.

### Logs
To view logs for a specific service, use:

```bash
docker logs <container_id>
```

## Future Scope

### 1. Authentication and Security Enhancements

- Refresh Token Implementation:
Currently, only access tokens are used. Introduce refresh tokens to enable secure re-authentication without requiring users to log in repeatedly.
Store refresh tokens securely (e.g., HTTP-only cookies or encrypted in the DB).

- Token Expiry and Blacklisting:
Implement token expiry validation and maintain a blacklist of revoked JWTs for logout and session invalidation support.

- Role-based Access Control (RBAC):
Enhance the authorization layer to support fine-grained permissions (beyond just role names like "ADMIN", "USER").

- OAuth2/OpenID Connect Support:
Add support for third-party authentication (e.g., Google, GitHub) using Spring Security's OAuth2 modules.

### 2. Kafka Improvements

- Kafka Security:
Enable SSL encryption and SASL authentication between producers, consumers, and brokers.
Set KAFKA_AUTO_CREATE_TOPICS_ENABLE=false and explicitly manage topic creation to avoid unexpected behaviors.

- Kafka Schema Registry Integration:
Use Avro/Protobuf serialization and register schemas with Confluent Schema Registry for strong data contracts between services.

- Kafka Dead Letter Topics:
Implement dead-letter topics for handling and debugging consumer deserialization or processing failures.

- Exactly-once Delivery (EOS):
Enable exactly-once semantics for Kafka transactions in case you evolve toward more critical data flow operations.

### 3. Observability and Monitoring

- Centralized Logging:
Integrate with ELK (Elasticsearch, Logstash, Kibana) or EFK (Fluentd) stack for log aggregation and analysis.

- Distributed Tracing:
Add support for tracing (OpenTelemetry, Zipkin, or Jaeger) to track requests across microservices for debugging and performance optimization.

- Metrics and Dashboards:
Add Prometheus and Grafana to monitor JVM metrics, Kafka lag, HTTP traffic, and DB performance.

### 5. Database & Data Layer

- Flyway or Liquibase Integration:
Add database versioning and migrations to maintain schema changes across environments.

- Read/Write Separation:
In future scaling scenarios, consider separating read-heavy and write-heavy operations across DB replicas.

### 6. Scalability & Resilience

- Rate Limiting and Throttling:
Protect endpoints using libraries like Bucket4j or API gateways with built-in rate limiting.

- Horizontal Scaling:
Configure services to run in scalable environments (Docker Swarm or Kubernetes).
