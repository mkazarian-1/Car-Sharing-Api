# Carsharing API

## Overview

The Carsharing API is a robust backend service that facilitates car sharing operations. It enables efficient management of cars, rentals, payments, and users while integrating modern technologies such as Spring Boot, Hibernate, MapStruct, Swagger for API documentation, and Stripe for payment processing. It also includes notifications via Telegram to improve user experience and enhance administrative capabilities.

## Features

- **Car Management**: Add, update, delete, and retrieve car details.
- **Rental Management**: Book, return, and monitor car rentals.
- **User Management**: Manage user roles and personal information.
- **Payment Processing**: Create and verify payment sessions using Stripe.
- **Telegram Notifications**: Notify users and administrators about important events, such as payment creation and successful rental bookings, via Telegram.
- **Security**: Secure endpoints with role-based access control.

## Technologies Used

- **Java 21**: Programming language.
- **Spring Boot**: Framework for building the application.
- **Hibernate**: ORM for database operations.
- **PostgreSQL**: Database management.
- **Stripe**: Payment processing.
- **Telegram Bots**: For real-time notifications.

## Prerequisites

- **Java 21** or higher.
- **PostgreSQL**: Ensure the database is set up and accessible.
- **Stripe Account**: Obtain the secret key for payment processing.
- **Telegram Bot Token**: Create a bot via BotFather and obtain the token.
- **Maven**: Build tool for managing dependencies and building the project.

## Environment Variables

The application uses the following environment variables. These must be configured for the application to run correctly:

```env
DB_DATASOURCE_URL=jdbc:postgresql://localhost:5432/CarSharingDB
DB_DATASOURCE_USERNAME=your_db_username
DB_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret
JWT_EXPIRATION=your_jwt_expiration_time
STRIPE_SECRET_KEY=your_stripe_secret_key
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_NAME=your_telegram_bot_name
```

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/mkazarian-1/Car-Sharing-Api.git
   ```

2. Configure environment variables. You can use a `.env` file or set them directly in your environment.

3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   java -jar target/Car-Sharing-Api-0.0.1-SNAPSHOT.jar
   ```
6. Base admin credentials:
   ```bash
   login: admin@gmail.com
   password: password12345
   ```

## API Documentation

Access the Swagger UI for API documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

## Telegram Notifications

Telegram notifications are integrated into the application to provide real-time updates to users and administrators. Events such as payment creation and successful rental bookings trigger notifications, ensuring timely and efficient communication. To enable this feature, ensure that the `TELEGRAM_BOT_TOKEN` and `TELEGRAM_BOT_NAME` environment variables are correctly configured.


