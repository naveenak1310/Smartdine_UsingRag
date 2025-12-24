package com.example.smartdine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class RagRecommendationService {

    private final RestaurantRepository restaurantRepository;
    private final EmbeddingService embeddingService;
    private final SimilarityService similarityService;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "mistralai/mistral-7b-instruct";
    private static final int TOP_K = 5;

    public RagRecommendationService(
            RestaurantRepository restaurantRepository,
            EmbeddingService embeddingService,
            SimilarityService similarityService
    ) {
        this.restaurantRepository = restaurantRepository;
        this.embeddingService = embeddingService;
        this.similarityService = similarityService;
    }

    public RagResponse getRecommendation(String query) {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();

        if (allRestaurants.isEmpty()) {
            return new RagResponse(null, List.of(), "No restaurants available");
        }

        embeddingService.updateIdf(allRestaurants);

        double[] queryEmbedding = embeddingService.generateEmbedding(query);

        List<SimilarityService.ScoredRestaurant> topRestaurants =
                similarityService.rankBySimilarity(
                        queryEmbedding,
                        allRestaurants,
                        embeddingService,
                        TOP_K,
                        query
                );

        List<Restaurant> retrievedRestaurants = topRestaurants.stream()
                .map(SimilarityService.ScoredRestaurant::getRestaurant)
                .collect(Collectors.toList());

        String context = buildContext(retrievedRestaurants);

        String llmResponse = callOpenRouter(query, context);

        return parseResponse(llmResponse, retrievedRestaurants);
    }

    private String buildContext(List<Restaurant> restaurants) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            context.append(String.format(
                    "%d. %s - Cuisine: %s, Price: %s, Rating: %.1f, Tags: %s, Description: %s\n",
                    i + 1,
                    r.getName(),
                    r.getCuisine(),
                    r.getPriceRange(),
                    r.getRating(),
                    r.getTags(),
                    r.getDescription()
            ));
        }
        return context.toString();
    }

    private String callOpenRouter(String query, String context) {
        OkHttpClient client = new OkHttpClient();

        String systemPrompt = "You are an intelligent food recommendation assistant. " +
                "Use ONLY the provided restaurant data to make recommendations. " +
                "Consider price, cuisine, ratings, tags, and descriptions when making recommendations. " +
                "IMPORTANT: Respond with ONLY a valid JSON object, no additional text before or after.";

        String userPrompt = String.format(
                "User query: \"%s\"\n\nTop matching restaurants (already filtered by relevance):\n%s\n\n" +
                "Task: Pick the BEST restaurant from this list that matches the user's query. " +
                "Consider:\n" +
                "- If they asked for specific food (e.g., pizza, waffle), prioritize restaurants with that item\n" +
                "- If they mentioned price (cheap, budget, expensive), consider the price range\n" +
                "- If they mentioned mood/occasion (romantic, casual, date night), use tags and description\n" +
                "- Rating and cuisine type matter for quality\n\n" +
                "Respond with ONLY this JSON format (no markdown, no extra text):\n" +
                "{\"bestRestaurant\": \"Exact Restaurant Name\", " +
                "\"alternatives\": [\"Alternative Name 1\", \"Alternative Name 2\"], " +
                "\"explanation\": \"Brief explanation (2-3 sentences) why this restaurant best matches their request, mentioning specific features\"}",
                query,
                context
        );

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
        messages.put(new JSONObject().put("role", "user").put("content", userPrompt));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 500);

        Request request = new Request.Builder()
                .url(OPENROUTER_URL)
                .addHeader("Authorization", "Bearer " + openRouterApiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "{\"bestRestaurant\": \"Error\", \"alternatives\": [], " +
                       "\"explanation\": \"Failed to get LLM response\"}";
            }
        } catch (IOException e) {
            return "{\"bestRestaurant\": \"Error\", \"alternatives\": [], " +
                   "\"explanation\": \"API call failed: " + e.getMessage() + "\"}";
        }
    }

    private RagResponse parseResponse(String llmResponse, List<Restaurant> retrievedRestaurants) {
        try {
            String jsonStr = llmResponse.trim();
            
            if (jsonStr.contains("```json")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```json") + 7);
                if (jsonStr.contains("```")) {
                    jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
                }
            } else if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```") + 3);
                if (jsonStr.contains("```")) {
                    jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
                }
            }
            
            jsonStr = jsonStr.trim();
            
            int firstBrace = jsonStr.indexOf('{');
            int lastBrace = jsonStr.lastIndexOf('}');
            
            if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
                jsonStr = jsonStr.substring(firstBrace, lastBrace + 1);
            }

            JSONObject json = new JSONObject(jsonStr);

            String bestRestaurantName = json.optString("bestRestaurant", "");
            List<String> alternativeNames = new ArrayList<>();
            
            if (json.has("alternatives")) {
                JSONArray alts = json.getJSONArray("alternatives");
                for (int i = 0; i < alts.length(); i++) {
                    alternativeNames.add(alts.getString(i));
                }
            }

            String explanation = json.optString("explanation", "");

            Restaurant bestRestaurant = findRestaurantByName(bestRestaurantName, retrievedRestaurants);

            List<Restaurant> alternatives = alternativeNames.stream()
                    .map(name -> findRestaurantByName(name, retrievedRestaurants))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return new RagResponse(bestRestaurant, alternatives, explanation);

        } catch (Exception e) {
            return new RagResponse(
                    retrievedRestaurants.isEmpty() ? null : retrievedRestaurants.get(0),
                    retrievedRestaurants.size() > 1 ? retrievedRestaurants.subList(1, retrievedRestaurants.size()) : List.of(),
                    "LLM returned: " + llmResponse.substring(0, Math.min(200, llmResponse.length())) + "... (parsing failed)"
            );
        }
    }

    private Restaurant findRestaurantByName(String name, List<Restaurant> restaurants) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return restaurants.stream()
                .filter(r -> r.getName() != null && 
                            r.getName().toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public static class RagResponse {
        private final Restaurant bestRestaurant;
        private final List<Restaurant> alternatives;
        private final String explanation;

        public RagResponse(Restaurant bestRestaurant, List<Restaurant> alternatives, String explanation) {
            this.bestRestaurant = bestRestaurant;
            this.alternatives = alternatives;
            this.explanation = explanation;
        }

        public Restaurant getBestRestaurant() {
            return bestRestaurant;
        }

        public List<Restaurant> getAlternatives() {
            return alternatives;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}
