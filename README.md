# 📚 book-worm-api

## 🚀 Overview  
**book-worm-api** is a Spring Boot microservice that manages structured book metadata using a MySQL relational database. 
It provides full CRUD operations over books, people (authors, editors, translators), and collections, and serves as the 
core SQL-based metadata store for a larger **Book Metadata Management System**.

---

## 🧱 Tech Stack  
- **Language:** Java 17  
- **Framework:** Spring Boot  
- **Persistence:** Spring Data JPA + Hibernate  
- **Database:** MySQL  
- **Containerization:** Docker  
- **Security:** Spring Security (WIP)  
- **Testing:** JUnit (WIP)  

---

## 📦 Features  
✅ Full CRUD operations for:
- Books  
- Authors, Editors, Translators  
- Collections  

✅ RESTful API endpoints  
✅ Dockerized setup for easy deployment  
✅ Authentication & Authorization *(in progress)*  
✅ Unit testing *(in progress)*  

---

## 🧩 Part of: Book Metadata Management System

This service is part of a modular microservices architecture, consisting of:

| Service          | Responsibility                                   |
|------------------|---------------------------------------------------|
| `book-worm-api`  | Manages finalized, enriched book metadata in SQL |
| `book-bridge-api`| Imports book metadata from Google Books into MongoDB |
| `book-scribe`    | Enriches, transforms, and imports Mongo data into SQL |
| `control-panel` *(planned)* | Orchestrates and monitors the entire system via UI & API |

---

## 🔧 Getting Started

To run the project locally:

```bash
# Clone the repository
git clone https://github.com/Lasha-Less/book-worm-api.git
cd book-worm-api

# Run with Maven
mvn spring-boot:run


# Build Docker image
docker build -t book-worm-api .

# Run container
docker run -p 8080:8080 book-worm-api
