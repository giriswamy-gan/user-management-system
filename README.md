# User Management System

The **User Management System** is a microservices-based application designed to manage users and their roles, with support for event-driven communication using Kafka. It consists of the following components:

- **User Service**: Handles user management, authentication, and role assignments.
- **Journal Service**: Logs user-related events for auditing purposes.
- **Kafka**: Facilitates communication between services.
- **PostgreSQL**: Serves as the database for both the User and Journal services.

## Features

- User registration, authentication, and role management.
- Event-driven architecture using Kafka.
- Health checks for all services.
- Dockerized deployment for easy setup.

## Prerequisites

- Docker and Docker Compose installed on your system.
- Java 17+ installed for local development.
- Maven installed for building the services locally.

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

## NOTE:

Please wait 1 minute after the kafka service starts before trying to hit any APIs. You can see docker logs for user-service and journal-service before starting to hit those APIs.

A user with the role "ROLE_ADMIN" and username "admin" and password "password" is created automatically upon starting the services.

To access the services, first login using this curl:

curl --location 'localhost:8081/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7B8890E2CA6955D5C9E41CA0E04D6DC4' \
--data '{
    "username": "admin",
    "password": "password"
}'

/auth/login and /auth/register are public APIs and can be accessed by anyone.

To register a new user use this curl:

curl --location 'localhost:8081/auth/register' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=7B8890E2CA6955D5C9E41CA0E04D6DC4' \
--data '{
	"username": "ganesh",
    "password": "password",
    "fullName": "ganesh g",
    "roleName": ["ROLE_USER"]
}'

But you have to create the appropriate role first. Refer the postman collection for accessing all the APIs.

On successful login, you can access all the APIs. Copy the token generated in the login API and use it as Bearer token to authorize all the APIs. Some APIs for deleting user, updating user need ROLE_ADMIN to access.

Accessing any of the journal API needs ADMIN access.

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
docker logs <container_name>
```
