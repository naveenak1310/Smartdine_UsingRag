# 🧪 SmartDine RAG System - Testing Guide

## ✅ What Was Implemented

### Backend (Java + Spring Boot)
- **EmbeddingService.java** - Generates local TF-IDF embeddings (100-dimensional vectors)
- **SimilarityService.java** - Computes cosine similarity for semantic search
- **RagRecommendationService.java** - Full RAG pipeline with OpenRouter integration
- **RagController.java** - REST API at `/api/rag/recommend`
- **EmbeddingInitializer.java** - Auto-generates embeddings on startup
- **Restaurant.java** - Added `embedding` JSON field to database

### Frontend (React + Vite)
- **RagSearch.jsx** - New RAG-powered search interface
- **Result.jsx** - Updated to display AI explanations
- **App.jsx** - Added "🤖 RAG" navigation link

---

## 📋 Prerequisites

1. **MySQL running** with `smartdine_db` database
2. **OpenRouter API key** (free tier available at https://openrouter.ai/)
3. **Node.js & npm** installed
4. **Maven** installed

---

## 🚀 Step-by-Step Testing

### **1. Backend Setup**

#### Start Backend:
```powershell
cd smartdine-backend

# Clean build
mvn clean package

# Run the application
mvn spring-boot:run
```

#### Watch Console Output:
You should see:
```
Generating embeddings for restaurants...
Embeddings generated and saved!
Started SmartDineApplication in X.XXX seconds
```

This means embeddings were successfully created and stored in MySQL.

---

### **2. Test RAG API Directly**

Open a **new terminal** and test the RAG endpoint:

#### Test 1: Spicy Food Query
```powershell
curl -X POST http://localhost:8080/api/rag/recommend `
  -H "Content-Type: application/json" `
  -d '{\"query\": \"spicy authentic biryani\"}'
```

#### Test 2: Budget Query
```powershell
curl -X POST http://localhost:8080/api/rag/recommend `
  -H "Content-Type: application/json" `
  -d '{\"query\": \"cheap delicious pizza under 200 rupees\"}'
```

#### Test 3: Fine Dining
```powershell
curl -X POST http://localhost:8080/api/rag/recommend `
  -H "Content-Type: application/json" `
  -d '{\"query\": \"romantic fine dining italian restaurant\"}'
```

#### Expected Response Format:
```json
{
  "bestRestaurant": {
    "id": 123,
    "name": "Restaurant Name",
    "cuisine": "Italian",
    "priceRange": "$$$",
    "rating": 4.5,
    "tags": "romantic, fine-dining",
    "description": "...",
    ...
  },
  "alternatives": [
    { "id": 456, "name": "Alternative 1", ... },
    { "id": 789, "name": "Alternative 2", ... }
  ],
  "explanation": "This restaurant is perfect because it matches your preference for romantic fine dining with authentic Italian cuisine and excellent ambiance."
}
```

**✅ If you see this structure, the RAG system works!**

---

### **3. Frontend Setup**

#### Install Dependencies & Start:
```powershell
cd smartdine-frontend

# Install packages (if not done)
npm install

# Start dev server
npm run dev
```

Frontend should start at: **http://localhost:5173**

---

### **4. Test RAG in Browser**

1. **Open Browser:** Go to `http://localhost:5173`

2. **Login/Signup:** Create an account or login

3. **Set Location:** 
   - Click "Search" or "🤖 RAG" in the navigation
   - Select your city (e.g., Coimbatore, Chennai, etc.)

4. **Navigate to RAG Search:**
   - Click the **"🤖 RAG"** link in the navigation bar
   - You'll see "RAG-Powered Search" heading

5. **Test Queries:**

   **Query 1: Spicy Food**
   ```
   spicy authentic biryani with lots of flavor
   ```

   **Query 2: Budget-Friendly**
   ```
   cheap but delicious pizza under 300 rupees
   ```

   **Query 3: Romantic Dining**
   ```
   romantic candlelight dinner with italian food
   ```

   **Query 4: Voice Input**
   - Click "Speak" button
   - Say: "I want spicy south indian food"

6. **Check Results:**
   - You'll see the loading animation
   - Results page shows:
     - ✅ Best matched restaurant
     - ✅ **AI Explanation** (blue box with 🤖)
     - ✅ Alternative restaurants
     - ✅ Map links

---

## 🔍 What Makes This RAG?

### **Traditional Search (Old `/api/recommend`):**
- Keyword matching
- Simple scoring (tags + price)
- No semantic understanding

### **RAG System (New `/api/rag/recommend`):**
1. **Local Embeddings** - Converts restaurant data to vectors (TF-IDF based)
2. **Semantic Retrieval** - Finds top 5 similar restaurants using cosine similarity
3. **Context Injection** - Sends only retrieved restaurants to LLM
4. **AI Generation** - OpenRouter's Mistral-7B generates explanation
5. **Structured Response** - Returns best + alternatives + reasoning

---

## 🐛 Troubleshooting

### Backend Won't Start
```powershell
# Check if port 8080 is in use
netstat -ano | findstr :8080

# Kill the process if needed
taskkill /PID <PID> /F
```

### "Embeddings generation failed"
- Check MySQL is running: `Get-Service MySQL*`
- Verify database exists: `smartdine_db`
- Check application.properties has correct DB credentials

### "OpenRouter API failed"
- Verify API key in `application.properties`:
  ```properties
  openrouter.api.key=sk-or-v1-YOUR_KEY_HERE
  ```
- Get free key at: https://openrouter.ai/
- Check you have credits (free tier included)

### Frontend Shows Empty Results
- Check browser console (F12) for errors
- Verify backend is running on port 8080
- Check CORS is enabled in `RagController.java`

### RAG Returns Generic Explanation
- This means OpenRouter couldn't parse response
- Check your API key has credits
- Try with simpler query first

---

## 🎯 Success Criteria

✅ Backend starts without errors  
✅ Console shows "Embeddings generated and saved!"  
✅ curl test returns JSON with `bestRestaurant`, `alternatives`, `explanation`  
✅ Frontend RAG page loads at `/rag-search`  
✅ Search returns results with AI explanation (blue box)  
✅ Explanation is contextual and mentions specific restaurant features

---

## 📊 Verify Embeddings in Database

```sql
USE smartdine_db;

-- Check if embeddings exist
SELECT id, name, 
       CASE 
         WHEN embedding IS NULL THEN 'NO EMBEDDING'
         ELSE CONCAT('HAS EMBEDDING (', LENGTH(embedding), ' chars)')
       END as embedding_status
FROM restaurants
LIMIT 5;

-- View sample embedding
SELECT id, name, LEFT(embedding, 100) as embedding_sample
FROM restaurants
WHERE embedding IS NOT NULL
LIMIT 1;
```

You should see JSON arrays like: `[0.123456, -0.234567, ...]`

---

## 🎉 What to Test

1. **Different Query Types:**
   - Cuisine preference: "authentic south indian"
   - Budget: "cheap eats under 200"
   - Mood: "romantic date night"
   - Mixed: "spicy vegetarian budget friendly"

2. **Compare Results:**
   - Try same query in regular "Search" vs "🤖 RAG"
   - Notice how RAG provides reasoning

3. **Edge Cases:**
   - Very specific: "cheesy pizza with thin crust under 400 rupees"
   - Vague: "something delicious"
   - Mixed language: "spicy biryani with extra masala"

---

## 📝 Key Files Reference

### Backend
- [application.properties](smartdine-backend/src/main/resources/application.properties) - OpenRouter API key
- [RagController.java](smartdine-backend/src/main/java/com/example/smartdine/RagController.java) - REST endpoint
- [RagRecommendationService.java](smartdine-backend/src/main/java/com/example/smartdine/RagRecommendationService.java) - RAG logic
- [EmbeddingService.java](smartdine-backend/src/main/java/com/example/smartdine/EmbeddingService.java) - Local embeddings

### Frontend
- [RagSearch.jsx](smartdine-frontend/src/pages/RagSearch.jsx) - RAG search UI
- [Result.jsx](smartdine-frontend/src/pages/Result.jsx) - Results display
- [App.jsx](smartdine-frontend/src/App.jsx) - Navigation

---

## 💡 Next Steps

After testing works:
1. Tune embedding dimensions in `EmbeddingService` (current: 100)
2. Adjust TOP_K in `RagRecommendationService` (current: 5)
3. Experiment with different OpenRouter models
4. Add caching for embeddings
5. Implement user feedback loop

---

## ⚡ Quick Test Commands

```powershell
# Terminal 1: Backend
cd smartdine-backend
mvn spring-boot:run

# Terminal 2: Test API
curl -X POST http://localhost:8080/api/rag/recommend -H "Content-Type: application/json" -d '{\"query\": \"spicy biryani\"}'

# Terminal 3: Frontend
cd smartdine-frontend
npm run dev
```

**Then visit:** http://localhost:5173 → Login → Click "🤖 RAG" → Test!

---

🎊 **You've successfully implemented a 100% free RAG system!**
