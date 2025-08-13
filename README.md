# Password-Manager-Generator-Using-Java-Swing-and-MySQL

A **Java Swing-based desktop application** that allows users to:
- Generate strong, random passwords.
- Save passwords securely in a MySQL database.
- View saved passwords.
- Delete saved passwords.
- User authentication (Login & Register system).

## 🚀 Features
- **Secure Password Generator**  
  Generates random passwords containing:
  - Uppercase letters
  - Lowercase letters
  - Numbers
  - Special characters  
- **MySQL Database Integration**  
  Stores and retrieves passwords for each logged-in user.
- **User Authentication**  
  Register or login before accessing password storage.
- **Auto Refresh Password**  
  Option to automatically regenerate password every 30 seconds.

## 📂 Project Structure
- PasswordGeneratorSwing.java
- README.md

## 🛠️ Requirements
- **Java JDK 17+** (or compatible version)
- **MySQL Database** installed and running
- **MySQL Connector/J** library

## 🗄️ Database Setup
Run the following SQL commands to create the database:

```sql
CREATE DATABASE password_manager;

USE password_manager;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100)
);

CREATE TABLE passwords (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    username VARCHAR(100),
    password VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```
## ⚙️ Configuration
In PasswordGeneratorSwing.java, update the database credentials:
```
private static final String DB_URL = "jdbc:mysql://localhost:3306/password_manager";
private static final String DB_USER = "YOUR_DB_USERNAME";
private static final String DB_PASS = "YOUR_DB_PASSWORD";
```
## ▶️ How to Run
1. Compile the program:
```
javac -cp "lib/mysql-connector-j-8.4.0.jar;." src/PasswordGeneratorSwing.java -d bin
```
2. Run the program:
```
java -cp "lib/mysql-connector-j-8.4.0.jar;bin" PasswordGeneratorSwing
```

## 📜 License
This project is licensed under the MIT License.
If you want, I can also **add `.gitignore`** for Java + IntelliJ/Eclipse so that you don’t upload `bin/`, `.class` files, and private configs to GitHub. That will make your repo cleaner and safer.
