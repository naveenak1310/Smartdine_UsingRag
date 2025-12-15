package com.example.smartdine;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatbotController {

    private final String API_KEY = System.getenv("OPENROUTER_API_KEY");

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        String userCity = body.get("city");

        try {
            String intent = detectIntent(question);

            if (intent.equals("CASUAL")) {
                String casualPrompt = """
You are SmartDine AI.
Respond friendly and naturally.
""";
                return ResponseEntity.ok(
                        Map.of("reply", callModel(casualPrompt, question))
                );
            }

            RecommendationResponse rec = recommendationService.recommend(question);

            if (rec.getBestMatch() == null) {
                return ResponseEntity.ok(Map.of("reply", "No matching restaurants found"));
            }

            StringBuilder context = new StringBuilder();

            Restaurant best = rec.getBestMatch();
            context.append(best.getName()).append(" - ")
                    .append(best.getCuisine()).append(", ")
                    .append(best.getPriceRange()).append(", ")
                    .append(best.getRating()).append(" stars, ")
                    .append("Location: ").append(best.getLocation())
                    .append("\n");

            for (Restaurant r : rec.getAlternatives()) {
                context.append(r.getName()).append(" - ")
                        .append(r.getCuisine()).append(", ")
                        .append(r.getPriceRange()).append(", ")
                        .append(r.getRating()).append(" stars, ")
                        .append("Location: ").append(r.getLocation())
                        .append("\n");
            }

            String systemPrompt = """
You are SmartDine AI — a friendly restaurant recommendation assistant.
This prompt is used ONLY when the user's intent is FOOD or BOOKING.
Recommend ONLY from the restaurant list below. Never create fake restaurants.

IMPORTANT CONTEXT:
• The restaurant list below is already FILTERED by the backend using database tags
• You must NOT assume any other restaurants exist beyond this list

CRITICAL RULES:
FINAL OUTPUT RULES:
• After listing restaurants, STOP. Do not add alternatives, suggestions, or extra text
• NEVER use words like "Alternatively", "You might also consider", "Other options"
• NEVER mention restaurants not present in the list

• You MUST recommend ONLY restaurants that appear EXACTLY in the list below
• If ONLY ONE restaurant matches the request, suggest ONLY THAT ONE
• DO NOT add extra restaurants to reach 2 or 3
• DO NOT invent names, descriptions, or places
• If fewer matches exist, show fewer results

When the user asks for suggestions:
• Suggest 1-3 restaurants only IF THEY EXIST
• For each, include these details:
 <name>
(Cuisine:<cuisine>
 Price:<price>
 Rating:<rating>⭐)
• After the list, add AT MOST one short sentence ONLY if more than one restaurant is listed
• Speak in a friendly natural style — not robotic

📌 The user's city is: %s

📌 Restaurants:
%s

If the user wants to book/reserve/table/seat:
Reply ONLY in this exact format:
BOOK:<restaurant_name>
NO other text. NO explanation. NO emojis.
""".formatted(userCity, context);

            String reply = callModel(systemPrompt, question);

            if (reply.startsWith("BOOK:")) {
                String name = reply.replace("BOOK:", "").trim().toLowerCase();
                if (best.getName().toLowerCase().contains(name)) {
                    return ResponseEntity.ok(Map.of("reply", "BOOK:" + best.getId()));
                }
            }

            return ResponseEntity.ok(Map.of("reply", reply));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("reply", "⚠ SmartDine AI is offline"));
        }
    }

    private String detectIntent(String question) throws Exception {
        String intentPrompt = """
Classify the user message into exactly ONE label:
CASUAL
FOOD
BOOKING

Reply ONLY with the label.
""";
        return callModel(intentPrompt, question).toUpperCase();
    }

    private String callModel(String systemPrompt, String userPrompt) throws Exception {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String payload = """
        {
          "model": "meta-llama/llama-3.2-3b-instruct",
          "messages": [
            {"role": "system", "content": "%s"},
            {"role": "user", "content": "%s"}
          ]
        }
        """.formatted(systemPrompt.replace("\"", "'"), userPrompt.replace("\"", "'"));

        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(payload, mediaType);

        Request request = new Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        String resBody = response.body().string();

        JSONObject json = new JSONObject(resBody);
        JSONArray choices = json.getJSONArray("choices");

        return choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }
}
