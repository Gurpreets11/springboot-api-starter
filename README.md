# Spring Boot API Starter

A reusable Spring Boot starter/template for building secure REST API applications.

The goal of this project is to provide a common backend foundation that can be reused across multiple applications instead of implementing the same authentication, security, and API infrastructure repeatedly.

## Features

* Spring Boot REST API
* JWT-based authentication
* Role-based authorization
* Spring Security configuration
* Password encryption
* Login API
* JWT token generation and validation
* Common REST API structure
* Exception handling
* Validation
* PostgreSQL support
* Reusable configuration

## Project Goals

This project is intended to act as a common backend foundation for future applications.

Instead of creating authentication and security functionality from scratch for every project, this starter can be reused and extended with application-specific business modules.

Example:

```text
Spring Boot API Starter
        │
        ├── Authentication
        ├── JWT
        ├── Authorization
        ├── Security
        ├── Common API Components
        │
        └── Application-specific modules
                ├── Product
                ├── Category
                ├── Order
                ├── Customer
                └── etc.
```

## Intended Usage

This project can be used as the foundation for applications such as:

* Admin APIs
* Web application APIs
* Mobile application APIs
* CRM systems
* Inventory systems
* E-commerce systems
* Internal business applications

## Technology Stack

* Java
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* PostgreSQL
* Maven

## Project Structure

The project will be organized into reusable modules/components such as:

```text
src/main/java
└── com.example
    ├── auth
    ├── security
    ├── user
    ├── exception
    ├── common
    └── ...
```

The exact package structure may evolve as the starter is developed.

## Authentication Flow

```text
Client
   │
   │ Login
   ▼
Auth API
   │
   │ Validate credentials
   ▼
JWT Token
   │
   ▼
Client
   │
   │ Authorization: Bearer <token>
   ▼
Authenticated APIs
```

## Reusing the Starter

Future Spring Boot projects should be able to use this project as a dependency or template and then add their own business modules.

For example:

```text
Spring Boot API Starter
        +
Product Module
        +
Category Module
        +
Order Module
        =
Application Backend
```

## Development Status

🚧 Under Development

The initial version is being developed by extracting and generalizing an existing JWT authentication and role-based authorization implementation.

## Versioning

This project will follow semantic versioning:

```text
MAJOR.MINOR.PATCH
```

Example:

```text
1.0.0
1.1.0
1.1.1
```

## License

Private project. License and usage terms will be defined when the project is ready for wider reuse.
