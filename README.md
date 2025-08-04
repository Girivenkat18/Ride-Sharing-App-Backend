# 🚗 Ride-Sharing App — Backend

Backend service for a ride-sharing platform built to connect users who want to share rides with others traveling to the same destination. The project aims to promote more efficient and sustainable travel by enabling ride coordination through a secure and scalable system. Developed using Spring Boot and PostgreSQL, the backend provides RESTful APIs for user authentication, ride posting, ride requests, and ride history tracking. JWT is used for authentication, and the codebase follows a clean layered architecture with separation of concerns across controller, service, and repository layers. Designed to integrate seamlessly with the mobile frontend.

---

## 🔧 Tech Stack

- Java 23
- Spring Boot
- PostgreSQL
- Spring Security (JWT Authentication)
- JPA / Hibernate
- Maven

---

## 📦 Features

- JWT-based user authentication
- Create and manage ride posts
- Request to join rides
- Accept/reject/cancel ride requests
- View categorized ride history
- Role-based access control
- RESTful API architecture

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ (Java 23 preferred)
- PostgreSQL
- Maven
- IDE (IntelliJ, VS Code, Eclipse, etc.)

### Clone and Run

```bash
git clone https://github.com/Girivenkat18/Ride-Sharing-Backend.git
cd Ride-Sharing-Backend
```

### Configure Database
Update application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```
