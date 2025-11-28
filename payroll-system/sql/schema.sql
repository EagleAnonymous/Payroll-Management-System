-- Create database and tables
CREATE DATABASE IF NOT EXISTS payroll_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payroll_db;

-- Users table
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);
INSERT INTO users (username, password) VALUES ('admin', 'root')
ON DUPLICATE KEY UPDATE password=VALUES(password);

-- Employees table
DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  age INT,
  dob DATE NOT NULL,
  gender ENUM('MALE','FEMALE','OTHER') NOT NULL,
  position ENUM('BUSINESS ANALYST','PROJECT MANAGER','UI/UX DESIGNER','WEB DEVELOPER','QA ENGINEER','DEVOPS ENGINEER') NOT NULL,
  email VARCHAR(150),
  contact VARCHAR(30) 
) AUTO_INCREMENT = 10000;

-- Payroll table
DROP TABLE IF EXISTS payroll;
CREATE TABLE payroll (
  id INT PRIMARY KEY AUTO_INCREMENT,
  employee_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  position VARCHAR(60) NOT NULL,
  days_worked DECIMAL(10,2) NOT NULL,
  rate_per_day DECIMAL(10,2) NOT NULL,
  salary DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Reports table
DROP TABLE IF EXISTS reports;
CREATE TABLE reports (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(150) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  notes TEXT
);

INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('David Taylor', 45, '1980-08-19', 'MALE', 'PROJECT MANAGER', 'david.taylor0@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Mary Miller', 33, '1992-01-25', 'FEMALE', 'QA ENGINEER', 'mary.miller1@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('John Williams', 28, '1997-03-10', 'MALE', 'WEB DEVELOPER', 'john.williams2@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Susan Brown', 49, '1976-05-15', 'FEMALE', 'DEVOPS ENGINEER', 'susan.brown3@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Michael Davis', 38, '1987-07-20', 'MALE', 'BUSINESS ANALYST', 'michael.davis4@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Linda Wilson', 42, '1983-09-05', 'FEMALE', 'UI/UX DESIGNER', 'linda.wilson5@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('William Moore', 25, '2000-11-30', 'MALE', 'PROJECT MANAGER', 'william.moore6@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Karen Taylor', 31, '1994-02-12', 'FEMALE', 'QA ENGINEER', 'karen.taylor7@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Peter Anderson', 48, '1977-04-22', 'MALE', 'WEB DEVELOPER', 'peter.anderson8@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Jane Smith', 29, '1996-06-28', 'FEMALE', 'DEVOPS ENGINEER', 'jane.smith9@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('David Jones', 35, '1990-08-19', 'MALE', 'BUSINESS ANALYST', 'david.jones10@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Mary Williams', 41, '1984-01-25', 'FEMALE', 'UI/UX DESIGNER', 'mary.williams11@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('John Brown', 27, '1998-03-10', 'MALE', 'PROJECT MANAGER', 'john.brown12@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Susan Davis', 46, '1979-05-15', 'FEMALE', 'QA ENGINEER', 'susan.davis13@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Michael Miller', 37, '1988-07-20', 'MALE', 'WEB DEVELOPER', 'michael.miller14@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Linda Wilson', 43, '1982-09-05', 'FEMALE', 'DEVOPS ENGINEER', 'linda.wilson15@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('William Moore', 24, '2001-11-30', 'MALE', 'BUSINESS ANALYST', 'william.moore16@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Karen Taylor', 32, '1993-02-12', 'FEMALE', 'UI/UX DESIGNER', 'karen.taylor17@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Peter Anderson', 47, '1978-04-22', 'MALE', 'PROJECT MANAGER', 'peter.anderson18@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Jane Smith', 30, '1995-06-28', 'FEMALE', 'QA ENGINEER', 'jane.smith19@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('David Jones', 36, '1989-08-19', 'MALE', 'WEB DEVELOPER', 'david.jones20@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Mary Williams', 40, '1985-01-25', 'FEMALE', 'DEVOPS ENGINEER', 'mary.williams21@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('John Brown', 26, '1999-03-10', 'MALE', 'BUSINESS ANALYST', 'john.brown22@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Susan Davis', 45, '1980-05-15', 'FEMALE', 'UI/UX DESIGNER', 'susan.davis23@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Michael Miller', 39, '1986-07-20', 'MALE', 'PROJECT MANAGER', 'michael.miller24@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Linda Wilson', 44, '1981-09-05', 'FEMALE', 'QA ENGINEER', 'linda.wilson25@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('William Moore', 23, '2002-11-30', 'MALE', 'WEB DEVELOPER', 'william.moore26@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Karen Taylor', 33, '1992-02-12', 'FEMALE', 'DEVOPS ENGINEER', 'karen.taylor27@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Peter Anderson', 46, '1979-04-22', 'MALE', 'BUSINESS ANALYST', 'peter.anderson28@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Jane Smith', 31, '1994-06-28', 'FEMALE', 'UI/UX DESIGNER', 'jane.smith29@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('David Jones', 37, '1988-08-19', 'MALE', 'PROJECT MANAGER', 'david.jones30@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Mary Williams', 39, '1986-01-25', 'FEMALE', 'QA ENGINEER', 'mary.williams31@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('John Brown', 25, '2000-03-10', 'MALE', 'WEB DEVELOPER', 'john.brown32@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Susan Davis', 44, '1981-05-15', 'FEMALE', 'DEVOPS ENGINEER', 'susan.davis33@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Michael Miller', 40, '1985-07-20', 'MALE', 'BUSINESS ANALYST', 'michael.miller34@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Linda Wilson', 45, '1980-09-05', 'FEMALE', 'UI/UX DESIGNER', 'linda.wilson35@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('William Moore', 22, '2003-11-30', 'MALE', 'PROJECT MANAGER', 'william.moore36@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Karen Taylor', 34, '1991-02-12', 'FEMALE', 'QA ENGINEER', 'karen.taylor37@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Peter Anderson', 45, '1980-04-22', 'MALE', 'WEB DEVELOPER', 'peter.anderson38@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Jane Smith', 32, '1993-06-28', 'FEMALE', 'DEVOPS ENGINEER', 'jane.smith39@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('David Jones', 38, '1987-08-19', 'MALE', 'BUSINESS ANALYST', 'david.jones40@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Mary Williams', 38, '1987-01-25', 'FEMALE', 'UI/UX DESIGNER', 'mary.williams41@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('John Brown', 24, '2001-03-10', 'MALE', 'PROJECT MANAGER', 'john.brown42@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Susan Davis', 43, '1982-05-15', 'FEMALE', 'QA ENGINEER', 'susan.davis43@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Michael Miller', 41, '1984-07-20', 'MALE', 'WEB DEVELOPER', 'michael.miller44@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Linda Wilson', 46, '1979-09-05', 'FEMALE', 'DEVOPS ENGINEER', 'linda.wilson45@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('William Moore', 21, '2004-11-30', 'MALE', 'BUSINESS ANALYST', 'william.moore46@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Karen Taylor', 35, '1990-02-12', 'FEMALE', 'UI/UX DESIGNER', 'karen.taylor47@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Peter Anderson', 44, '1981-04-22', 'MALE', 'PROJECT MANAGER', 'peter.anderson48@example.com', '09123456789');
INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES ('Jane Smith', 33, '1992-06-28', 'FEMALE', 'QA ENGINEER', 'jane.smith49@example.com', '09123456789');
