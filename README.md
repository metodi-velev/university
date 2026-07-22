# University Management System

A Spring Boot-based RESTful API for managing student information. This project demonstrates best practices in Spring Boot development, including layered architecture, exception handling, data mapping, and comprehensive testing strategies.

## 🚀 Features

- **Student Management**: CRUD operations for students.
- **RESTful API**: Clean and predictable JSON endpoints.
- **Global Exception Handling**: Centralized error management using `@ControllerAdvice`.
- **Database Integration**: JPA/Hibernate with an H2 in-memory database.
- **Testing**: Includes unit, integration, and web layer tests.
- **Container Support**: Integration testing with Testcontainers.

## 🛠️ Technologies & Dependencies

- **Java 26**: The project utilizes the latest Java features.
- **Spring Boot 4.1.0**: Core framework for the application.
- **Spring Data JPA**: For database persistence.
- **H2 Database**: In-memory database for development and testing.
- **Lombok**: To reduce boilerplate code (Getters, Setters, Builders).
- **Maven**: Build and dependency management.
- **Testcontainers**: For running tests with real database containers (PostgreSQL/MySQL ready).

## 🏗️ Project Structure

The project follows a standard Spring Boot layered architecture:

- `com.example.university.controller`: Contains `StudentController` which defines REST endpoints.
- `com.example.university.service`: Contains `StudentService` for business logic and transaction management.
- `com.example.university.repository`: Contains `StudentRepository` (Spring Data JPA) for database access.
- `com.example.university.model`: Contains the `Student` JPA entity.
- `com.example.university.dto`: Contains Data Transfer Objects (`StudentDto`, `ErrorDto`) for API requests and responses.
- `com.example.university.mapper`: Contains `StudentMapper` for converting between Entities and DTOs.
- `com.example.university.exception`: Contains `GlobalExceptionHandler` to manage application-wide exceptions.

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/students` | Retrieve a list of all students |
| GET | `/students/{id}` | Retrieve details of a specific student by ID |
| POST | `/students` | Create a new student |

### Sample POST Request body
```json
{
  "name": "John Doe"
}
```

## 🏃 How to Run

### Prerequisites
- JDK 26
- Maven 3.x

### Run the Application
You can run the application using the Maven wrapper:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

### H2 Console
You can access the H2 in-memory database console at:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (leave empty)

## 🧪 Testing

The project includes several levels of testing:

- **Unit Tests**: `StudentServiceUnitTest` focuses on business logic in isolation.
- **Integration Tests**: `StudentServiceIntegrationTest` tests the service layer with a real database context.
- **Web Layer Tests**: `StudentControllerTest` uses `MockMvc` to test the REST endpoints without starting the full HTTP server.

To run all tests:
```bash
./mvnw test
```

## ⚙️ Configuration

Application configuration can be found in `src/main/resources/application.yml`. It includes database settings, H2 console activation, and JPA properties.
