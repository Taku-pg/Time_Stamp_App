# Time stamp application

## Overview
This application is designed to facilitate worker's working time stamp service which records their work/break hours and calculate thier salary based on their hourly rate.

It also provides functionality to visualise employees and departments working hour statistics in chart.

## Features
#### This application supports following features:
- Logging
- Authentication
- Role-based authorization
- Error handling & Validation

## User functionalities
### Administrator: 
- Add/Delete employee
- Modify employees’ information (salary, name, etc.)
-	View monthly working hour statistics of employee (how many hours they work in overtime etc.).
-	View department-level working hours statistics 

### Employee:
-	Timestamp work/break/leave
-	View personal information and update password
-	View working history and calculated salary (monthly base)
-	View working time graph in chart (e.g. working hours of normal shift/overtime/night shift etc.)

## Teck
- Java 25
- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- Spring Web
- Tymeleaf
- Mysql
- Mockito

## Database Design
<img width="990" height="491" alt="Image" src="https://github.com/user-attachments/assets/ff9741d9-7835-45f1-bece-9222cfc6694c" />

## Setup
### Data base setup
1. Clone this repository
   
`git clone https://github.com/Taku-pg/time_stamp_app.git`

2. This application is running on lacal Mysql database
   
```
 url: jdbc:mysql://localhost:3306/timestampdb
 username: timestamp
 password: password
```
 
3. Create database and user on local host
   
```
 CREATE DATABASE timestampdb;  
 CREATE USER 'timestamp'@'localhost' IDENTIFIED BY 'password';  
 GRANT ALL PRIVILEGES ON timestampdb.* TO 'timestamp'@'localhost';  
 FLUSH PRIVILEGES;
```

4. Initialaize database schema and data

   You can use provided sql files
   
```
 mysql -u timestamp -p timestampdb < src/main/resources/data/schema.sql
 mysql -u timestamp -p timestampdb < src/main/resources/data/data.sql
```   

5. Run application

`gradle bootrun`

6. Access the URL

`http://localhost:8080/login`

>[!NOTE]
>If you change the DB name, username or password, update application.yaml.

### Inserted data:

#### Admin
- Username: admin
- Password: admin
#### Employee

| Username | Password |
| --- | --- |
| employee1@gmail.com | EmployeeNo1 |
| employee2@gmail.com | EmployeeNo2 |
| employee3@gmail.com | EmployeeNo3 |
