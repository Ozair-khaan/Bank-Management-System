Enterprise Bank Management System

An enterprise-grade core banking application built with Spring Boot and Java 17. This project simulates real-world retail and corporate banking flows, demonstrating robust architectural patterns, ACID-compliant transactions, and strict role-based security.

Designed as a showcase of backend engineering best practices, this system abstracts database interactions via DTOs, utilizes SLF4J for observable logging, and employs Spring Security for session management.

📸 Screenshots

1. Secure Authentication


Role-based access control secured by BCrypt password hashing and Spring Security.

2. Banking Dashboard


Dynamic JSP dashboard aggregating account balances, account types, and recent transaction history.

3. ACID Fund Transfers


Transactional fund routing with strict validation (e.g., insufficient funds checks, zero-amount prevention).

🚀 Key Features

Secure Authentication: User login managed by Spring Security and CustomUserDetailsService with BCrypt password encoding.

ACID Transactions: Fund transfers are strictly bound by @Transactional(isolation = Isolation.READ_COMMITTED) to prevent partial commits or race conditions.

Layered Architecture: Strict separation of concerns (Controller ➔ Service ➔ Repository ➔ Database) utilizing DTOs (Data Transfer Objects) to protect database entities.

Enterprise Observability: Integrated SLF4J & Logback for tiered application logging (INFO, DEBUG, WARN, ERROR) with rolling file policies.

Dynamic UI: Server-side rendering using modern Jakarta JSTL and embedded Tomcat Jasper.

🛠️ Tech Stack

Backend: Java 17, Spring Boot (Web, Data JPA, Security)

Database: MySQL 8.0, Hibernate (ORM)

Frontend: JSP (JavaServer Pages), Jakarta JSTL, HTML/CSS

Tooling: Maven, Lombok, SLF4J (Logback)

⚙️ How to Run the Application

Follow these steps to set up your database, run the Spring Boot application, and view your JSP dashboard.

Step 1: Prepare the MySQL Database

Open MySQL Workbench, connect to your local server, and open a new SQL tab. Run the following commands to create the database:

CREATE DATABASE bank_db;
USE bank_db;


(Note: Make sure the spring.datasource.password in your application.properties matches your actual MySQL root password).

Step 2: Run the Spring Boot Application

Navigate to the root folder of your project in your terminal (where the pom.xml is located) and run:

mvn clean spring-boot:run


When the application starts, Spring Data JPA (Hibernate) will automatically create the required tables.

Step 3: Insert Mock Data (Crucial Step)

Keep the Spring Boot app running, go back to MySQL Workbench, and run this SQL script to insert the initial user data and BCrypt hashed passwords:

USE bank_db;

-- 1. Insert the main user account (Password is: password123)
INSERT INTO accounts (account_number, account_holder_name, balance, account_type, created_at, password, role)
VALUES ('ACC100200300', 'Alice Smith', 25000.00, 'CORPORATE', NOW(), '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGF.4Kq.Sc5bT.GqW5Kc', 'USER');

-- 2. Insert a target account for transfers
INSERT INTO accounts (account_number, account_holder_name, balance, account_type, created_at, password, role)
VALUES ('ACC999888777', 'Tech Corp Inc.', 5000.00, 'RETAIL', NOW(), '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGF.4Kq.Sc5bT.GqW5Kc', 'USER');

-- 3. Insert some initial transaction history for the dashboard
INSERT INTO transactions (reference_number, account_id, type, amount, description, transaction_date)
VALUES ('TXN-A1B2C3D4', 1, 'DEPOSIT', 25000.00, 'Initial Corporate Funding', NOW());


Step 4: Access the Application

Open your web browser and navigate to: http://localhost:8080/login

Username: ACC100200300

Password: password123

Explore the dashboard, initiate a fund transfer, and check your IDE/Terminal console to see the SLF4J logging in action!