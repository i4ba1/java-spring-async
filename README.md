# Movie API with Spring Boot, JDK 21, and Virtual Threads

This is a comprehensive RESTful API for a movie platform, built with Spring Boot and Java 21, featuring asynchronous processing with virtual threads, PostgreSQL database integration, and user authentication with email and mobile verification.

## Features

### Core Features
- Complete CRUD operations for movie data
- Advanced search functionality with multiple parameters
- Asynchronous processing using JDK 21 virtual threads
- PostgreSQL database integration
- Robust error handling
- Data validation
- Pagination and sorting

### Authentication and Security Features
- User registration with email and mobile verification
- OTP (One-Time Password) verification for both email and mobile
- JWT-based authentication with refresh tokens
- Role-based access control
- Secure password storage with BCrypt encryption
- Logout functionality

### Purchase Features
- Movie purchase functionality
- Multiple payment method support (credit card, debit card, PayPal)
- Purchase history tracking

## Prerequisites

- JDK 21 or higher
- PostgreSQL 13 or higher
- Maven 3.8 or higher
- SMTP server for email verification (can use Gmail SMTP)

## Getting Started

### Database Setup

1. Install PostgreSQL if you haven't already
2. Create a new database named `moviedb`:
```sql
CREATE DATABASE moviedb;
```

### Application Configuration

1. Clone this repository
2. Configure the database connection in `src/main/resources/application.yml`
3. Configure email settings in `src/main/resources/application.yml`
4. Build the application:
```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start at http://localhost:8080

## API Endpoints

### Movie Endpoints

- `POST /api/movies` - Create a new movie
- `GET /api/movies/{id}` - Get a movie by ID
- `GET /api/movies` - Get all movies
- `PUT /api/movies/{id}` - Update a movie
- `DELETE /api/movies/{id}` - Delete a movie
- `POST /api/movies/search` - Search for movies

### Authentication Endpoints

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/verify/email` - Verify email with OTP
- `POST /api/auth/verify/mobile` - Verify mobile with OTP
- `POST /api/auth/resend-otp` - Resend verification OTP
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Logout (invalidate token)

### Purchase Endpoints

- `POST /api/purchases` - Purchase a movie
- `GET /api/purchases` - Get user's purchase history
- `GET /api/purchases/payment-methods` - Get available payment methods

## Sample Requests

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123",
    "email": "john.doe@example.com",
    "mobileNumber": "+1234567890",
    "fullName": "John Doe"
  }'
```

### Verify Email

```bash
curl -X POST http://localhost:8080/api/auth/verify/email \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "code": "123456",
    "type": "email"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### Purchase a Movie

```bash
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "movieId": 1,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "cardExpiry": "12/25",
    "cardCvv": "123",
    "cardHolderName": "John Doe"
  }'
```

## Technical Implementation Details

### Virtual Threads

This application leverages JDK 21's virtual threads for efficient asynchronous processing. Virtual threads are lightweight threads that don't map 1:1 to OS threads, allowing for high concurrency without the overhead of traditional threads.

### Email and Mobile Verification

The application supports sending verification codes via email and simulates SMS sending for mobile verification. In a production environment, you would integrate with an actual SMS service provider.

### Security Implementation

The security is implemented using Spring Security with JWT (JSON Web Tokens):
- Tokens are signed using HMAC SHA-512
- Access tokens expire after 24 hours by default
- Refresh tokens enable getting new access tokens without re-authentication
- Passwords are encrypted using BCrypt

### Payment Processing

The payment processing is mocked for demonstration purposes. In a production environment, you would integrate with an actual payment gateway.

## Project Structure

```
src/main/java/com/example/movieapi/
├── config/
│   ├── AsyncConfig.java
│   ├── DataInitializer.java
│   ├── RoleInitializer.java
│   └── WebSecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── MovieController.java
│   └── PurchaseController.java
├── dto/
│   ├── MovieDTO.java
│   ├── MovieSearchDTO.java
│   ├── AuthDTOs.java
│   └── PurchaseDTOs.java
├── exception/
│   ├── CustomExceptions.java
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Movie.java
│   ├── User.java
│   ├── Role.java
│   ├── Verification.java
│   └── Purchase.java
├── repository/
│   ├── MovieRepository.java
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── VerificationRepository.java
│   ├── PurchaseRepository.java
│   └── MovieSpecifications.java
├── security/
│   ├── AuthEntryPointJwt.java
│   ├── AuthTokenFilter.java
│   ├── JwtUtils.java
│   ├── UserDetailsImpl.java
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── MovieService.java
│   ├── MovieServiceImpl.java
│   ├── AuthService.java
│   ├── VerificationService.java
│   └── PurchaseService.java
└── MovieApiApplication.java
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
