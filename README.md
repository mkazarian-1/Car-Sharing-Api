# 🚗 Carsharing API

## 📌 Overview
The **Carsharing API** is a robust backend service designed for efficient car-sharing operations. It enables seamless management of cars, rentals, payments, and users while integrating modern technologies like **Spring Boot, Hibernate, MapStruct, Swagger, Stripe**, and **Telegram notifications** to enhance user experience and administrative efficiency.

---
## ✨ Features
✅ **Car Management**: Add, update, delete, and retrieve car details.  
✅ **Rental Management**: Book, return, and monitor car rentals.  
✅ **User Management**: Manage user roles and personal information.  
✅ **Payment Processing**: Create and verify payment sessions using Stripe.  
✅ **Telegram Notifications**: Receive real-time alerts about payments and bookings.  
✅ **Security**: Role-based access control for endpoint protection.

---
## 🛠 Technologies Used
- **🖥 Java 21** – Primary programming language.
- **🚀 Spring Boot** – Backend framework.
- **🗄 Hibernate** – ORM for database operations.
- **🐘 PostgreSQL** – Database management system.
- **💳 Stripe** – Secure payment processing.
- **📢 Telegram Bots** – Real-time notifications.
- **📜 Liquibase** – Database schema version control.
- **🐳 Docker** Containerization tool for easy deployment.

---
## ⚙️ Prerequisites
Before running the project, ensure you have:
- **Java 21** or higher installed.
- **PostgreSQL** database set up.
- **Stripe Account** with API credentials.
- **Telegram Bot Token** (Created via BotFather).
- **Maven** installed for project dependencies.

---
## 📌 Environment Variables
Ensure the following environment variables are configured before running the application:

```env
DB_DATASOURCE_URL=jdbc:postgresql://localhost:5432/CarSharingDB
DB_DATASOURCE_USERNAME=your_db_username
DB_DATASOURCE_PASSWORD=your_db_password

JWT_SECRET_KEY=your_jwt_secret
JWT_EXPIRATION=your_jwt_expiration_time

STRIPE_SECRET_KEY=your_stripe_secret_key

TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_NAME=your_telegram_bot_name

DB_DATASOURCE_NAME=CarSharingDB
DB_DATASOURCE_LOCAL_PORT=5433
DB_DATASOURCE_DOCKER_PORT=5432

SPRING_LOCAL_PORT=8088
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005
```

---
## 🚀 Setup Instructions
1️⃣ **Clone the repository:**
```bash
git clone https://github.com/mkazarian-1/Car-Sharing-Api.git
```

2️⃣ **Configure environment variables:**  
Set them directly in your environment or use a `.env` file.

3️⃣ **Build the project using Maven:**
```bash
mvn clean install
```

4️⃣ **Run the application:**
```bash
java -jar target/Car-Sharing-Api-0.0.1-SNAPSHOT.jar
```

5️⃣ **Base Admin Credentials:**
```plaintext
Login: admin@gmail.com
Password: password12345
```

## 🐳 Running the Project with Docker

### **1. Build and Run with Docker Compose**

Make sure you have **Docker** and **Docker Compose** installed.

1. **Create docker image:**

   ```bash
   git clone https://github.com/mkazarian-1/Car-Sharing-Api.git
   cd Car-Sharing-Api
   ```

2. **Build and start the services:**

   ```bash
   docker-compose up --build -d
   ```

3. **Verify the containers are running:**

   ```bash
   docker ps
   ```
   
4. **Access the API at:**

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---
## 📌 API Endpoints

### 🚗 Car Management
- **GET /cars** – Retrieve all cars (Public).
- **GET /cars/{id}** – Retrieve car details by ID (Public).
- **POST /cars** – Add a new car (**MANAGER** role required).
- **PATCH /cars/{id}** – Update car details (**MANAGER** role required).
- **DELETE /cars/{id}** – Delete a car (**MANAGER** role required).

### 📅 Rental Management
- **GET /rentals** – Retrieve user rentals (**MANAGERS** can view all users).
- **GET /rentals/{id}** – Retrieve rental by ID (User-specific access).
- **POST /rentals** – Create a new rental (Authenticated users only).
- **POST /rentals/return/{id}** – Return a rental (**MANAGERS** can update any rental).

### 💳 Payment Processing
- **GET /payments** – Retrieve payments (**MANAGERS** can view all, others see own).
- **POST /payments** – Create a payment session (Authenticated users only).
- **GET /payments/success** – Handle successful payments.
- **GET /payments/cancel** – Handle canceled payments.

### 👤 User Management
- **GET /users/me** – Retrieve current authenticated user details.
- **PUT /users/me** – Update personal information.
- **PUT /users/{id}/role** – Update user role (**MANAGER** role required).

### 🔐 User Authentication
- **POST /auth/registration** – Register a new user (Unique email required).
- **POST /auth/login** – Authenticate user, return JWT token (Valid for 60 minutes).

---
## 📖 API Documentation
Access the **Swagger UI** for detailed API documentation:
```
http://localhost:8080/swagger-ui/index.html
```

---
## 📢 Telegram Notifications
Real-time notifications via **Telegram** keep users and admins updated on payment creation and rental bookings. Ensure the following environment variables are set up for this feature:
- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_BOT_NAME`

---
### 🎯 **Enjoy seamless car-sharing with Car-Sharing-Api!** 🚀

