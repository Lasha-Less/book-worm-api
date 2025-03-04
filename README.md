# 📚 Spring Boot Database API  

## 🚀 Overview  
This is a **Spring Boot API** for managing database records with full CRUD operations. It includes authentication, authorization, structured data handling, and **Docker containerization**.  

## 🛠 Tech Stack  
- **Language:** Java  
- **Framework:** Spring Boot  
- **Database:** SQL (JPA, Hibernate)  
- **Containerization:** Docker  
- **Security:** Spring Security 

## 📦 Features  
✅ CRUD operations (Create, Read, Update, Delete)  
✅ RESTful API endpoints  
✅ Docker containerization for deployment  
✅ Authentication & Authorization (currently in progress)  
✅ Unit testing (currently in progress)  

## 🔧 Installation & Running  
To run the project locally:  
```bash
# Clone the repository
git clone https://github.com/Lasha-Less/book-worm-api.git

# Navigate into the project folder
cd book-worm-api

# Build and run the application
mvn spring-boot:run

# Build Docker image
docker build -t book-worm-api .

# Run Docker container
docker run -p 8080:8080 book-worm-api
