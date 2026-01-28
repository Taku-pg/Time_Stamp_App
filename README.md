# Time stamp application
This is a sample project using spring framework for learning purpose.

This application is designed to facilitate worker's time stamp service.

## Features

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
- Java 17
- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- Spring Web
- Tymeleaf
- Mysql

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
 
3. Create database on local host
   
```
 CREATE DATABASE timestampdb;  
 CREATE USER 'timestamp'@'localhost' IDENTIFIED BY 'password';  
 GRANT ALL PRIVILEGES ON timestampdb.* TO 'timestamp'@'localhost';  
 FLUSH PRIVILEGES;
```

4. Initialaize schema and data

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
| employee3@gmail.com | EmployeeNo3 || 
