# SmartDine – Restaurant Recommendation by AI Assistant

SmartDine is a full-stack web application that helps users discover nearby restaurants, get food recommendations based on their preferences,their moods and book tables easily.  

The project demonstrates full-stack development using React, Spring Boot, and MySQL.

---
## How It Works

### 🤖 Conversational Search
Type naturally like you'd ask a friend:
- *"something healthy like salad"*
- *"Spicy Biryani cheap and best"*
- *"i want french fries and burger"*
### 🎲 "Surprise Me" Feature
Can't decide? Let SmartDine pick something perfect for you based on random suggestion.
## Project Overview
## 🤖 AI Chatbot (RAG-Based Recommendation System)

SmartDine includes an AI-powered chatbot built using **RAG (Retrieval-Augmented Generation)** to provide accurate, relevant, and trustworthy restaurant recommendations.

Unlike a normal chatbot that may guess answers, SmartDine first **retrieves real restaurant data from the database** and then asks the AI to respond **using only that data**.

---

## How the RAG Chatbot Works (High Level)

### 1) Embedding & Index Creation
Restaurant details such as **name, cuisine, tags, and description** are converted into numeric vectors (embeddings) during application startup.  
These embeddings represent the meaning of each restaurant in vector space.

### 2) Query Understanding
When a user enters a query like:
> *"cheap spicy biryani"*

The query is converted into an embedding using the same embedding logic.

### 3) Retrieval & Ranking
The system compares the query embedding with stored restaurant embeddings using:
- **Cosine similarity** (semantic match)
- **Keyword matching** (exact food matches)

The most relevant restaurants are selected (Top-K results).

### 4) Context Building
Only the retrieved restaurants are formatted into a compact context containing:
- Name
- Cuisine
- Price range
- Rating
- Tags
- Description

This ensures the AI has limited, accurate information.

### 5) AI Response Generation
The context is sent to the LLM through **OpenRouter API** with strict instructions:
- Use **only the provided restaurant data**
- Return a **clean JSON response**

### 6) Safe Mapping to Database Records
The AI response is parsed and mapped back to real restaurant records from the database.  
If parsing fails, the system safely falls back to the top retrieved results.

### 7) Booking Integration
If the user responds with:BOOK: Restaurant Name

The chatbot resolves the correct restaurant ID and continues the booking flow securely.

---

## Why RAG Is Used in SmartDine

- Prevents AI hallucination  
- Ensures recommendations come only from real database entries  
- Improves accuracy for food-specific and mood-based queries  
- Makes recommendations explainable and reliable  

---


SmartDine allows users to:
- Register and log in
- Search restaurants using simple text queries
- View restaurant details such as cuisine, price range, ratings, reviews and images
- Book tables and view booking history
- Add restaurants to a wishlist
- Submit ratings and reviews

An admin dashboard is included for managing restaurant data and bookings.
Admin can add a new restauarant.

---

## Tech Stack

**Frontend**
- React JS
- JavaScript
- HTML
- CSS

**Backend**
- Java
- Spring Boot
- Spring Web (REST APIs)
- JPA Data

**Database**
-  MySQL

**AI/ML**
- Retrieval-Augmented Generation (RAG)
- Vector embeddings for semantic search
- LLM integration via OpenRouter

**Tools**
- Postman – API testing and debugging
- LottieFiles – animations used in the UI
- OpenRouter API (Chatbot feature)

---

## Project Structure
```
smartdine-mysql-project
├── smartdine-frontend
│   └── src
├── smartdine-backend
│   └── src
├── README.md
└── .gitignore
```

---

## Instructions to Setup and Run the Application

### 1. Clone the Repository
```bash
git clone https://github.com/naveenak1310/SmartDine_project.git
```

### 2. Navigate to Backend Folder
```bash
cd smartdine-backend
```

### 3. Configure MySQL Database

- Create a MySQL database (example: `smartdine_db`)
- Update database credentials in:
```
src/main/resources/application.properties
```

### 4. Set OpenRouter API Key (Environment Variable)

**Windows (PowerShell)**
```powershell
setx OPENROUTER_API_KEY "your_api_key_here"
```

Restart the terminal after setting the variable.

### 5. Run the Backend
```bash
mvn spring-boot:run
```

Backend will start at:
```
http://localhost:8080
```

---

## Frontend Setup

### 6. Navigate to Frontend Folder
```bash
cd smartdine-frontend
```

### 7. Install Dependencies
```bash
npm install
```

### 8. Start the Frontend
```bash
npm run dev
```

Frontend will start at:
```
http://localhost:5173
```

---

## Database Tables

- users
- restaurants
- bookings
- reviews
- restaurant_tags

---

## Security Notes

- API keys are stored using environment variables
- `.env`, `node_modules`, `target`, and build folders are ignored via `.gitignore`
- No sensitive information is committed to GitHub

---

## Features

- User authentication
- Restaurant search and recommendations
- Table booking system
- Booking history
- Wishlist management
- Ratings and reviews
- Admin dashboard
- Chatbot integration
- 16 Animation for UI

---

## Author

**Naveen**  
Full Stack Developer  
Java | Spring Boot | React | MySQL
