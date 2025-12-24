package com.example.smartdine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SimilarityService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Set<String> foodTagsFromDB = new HashSet<>();

    private static final Set<String> CORE_FOODS = Set.of(
        "pizza", "burger", "biryani", "pasta", "waffle", "waffles", "pancake", "pancakes",
        "sandwich", "sushi", "ramen", "noodles", "dosa", "idli",
        "vada", "samosa", "paratha", "kebab", "shawarma", "falafel",
        "tacos", "taco", "burrito", "nachos", "ice cream", "icecream", "cake", "cakes", "brownie",
        "cookie", "cookies", "donut", "donuts", "croissant", "bagel", "muffin", "cupcake", "cupcakes",
        "momos", "dimsum", "spring roll", "fried rice", "manchurian"
    );

    private static final Set<String> NON_FOOD_KEYWORDS = Set.of(
        "budget", "cheap", "expensive", "cozy", "romantic", "wifi", "parking",
        "comfort", "healthy", "spicy", "sweet", "study", "night", "late",
        "fastfood", "street", "fine", "casual", "formal", "treat", "snacks"
    );

    @PostConstruct
    public void initializeFoodKeywords() {
        try {
            foodTagsFromDB = restaurantRepository.findAll()
                .stream()
                .filter(r -> r.getTags() != null)
                .flatMap(r -> Arrays.stream(r.getTags().split(",")))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isEmpty() && isFoodTag(tag))
                .collect(Collectors.toSet());
            
            System.out.println("Loaded " + foodTagsFromDB.size() + " food keywords from database");
        } catch (Exception e) {
            System.err.println("Failed to load food keywords from DB: " + e.getMessage());
        }
    }

    private boolean isFoodTag(String tag) {
        return !NON_FOOD_KEYWORDS.contains(tag);
    }

    public double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }

    private double keywordMatchScore(String query, Restaurant restaurant) {
        String queryLower = query.toLowerCase().trim();
        String[] queryWords = queryLower.split("\\s+");
        
        double score = 0.0;
        int matchedKeywords = 0;
        
        for (String word : queryWords) {
            if (word.length() < 3) continue;
            
            String name = restaurant.getName() != null ? restaurant.getName().toLowerCase() : "";
            String cuisine = restaurant.getCuisine() != null ? restaurant.getCuisine().toLowerCase() : "";
            String tags = restaurant.getTags() != null ? restaurant.getTags().toLowerCase() : "";
            String description = restaurant.getDescription() != null ? restaurant.getDescription().toLowerCase() : "";
            
            boolean foundInName = name.contains(word);
            boolean foundInCuisine = cuisine.contains(word);
            boolean foundInTags = tags.contains(word);
            boolean foundInDescription = description.contains(word);
            
            if (foundInName || foundInCuisine || foundInTags || foundInDescription) {
                matchedKeywords++;
                score += 1.0;
                
                if (foundInName) score += 3.0;
                if (foundInTags) score += 2.0;
                if (foundInCuisine) score += 2.0;
            }
        }
        
        if (matchedKeywords == 0) {
            return 0.0;
        }
        
        return score / Math.max(queryWords.length, 1);
    }

    public List<ScoredRestaurant> rankBySimilarity(
            double[] queryEmbedding,
            List<Restaurant> restaurants,
            EmbeddingService embeddingService,
            int topK,
            String originalQuery
    ) {
        List<ScoredRestaurant> scored = new ArrayList<>();
        boolean hasSpecificKeywords = hasSpecificFoodKeywords(originalQuery);

        for (Restaurant restaurant : restaurants) {
            double[] restaurantEmbedding;
            
            if (restaurant.getEmbedding() != null && !restaurant.getEmbedding().isEmpty()) {
                restaurantEmbedding = embeddingService.jsonToEmbedding(restaurant.getEmbedding());
            } else {
                restaurantEmbedding = embeddingService.generateRestaurantEmbedding(restaurant);
            }

            double semanticSimilarity = cosineSimilarity(queryEmbedding, restaurantEmbedding);
            double keywordScore = keywordMatchScore(originalQuery, restaurant);
            
            if (hasSpecificKeywords && keywordScore == 0.0) {
                continue;
            }
            
            double semanticWeight = hasSpecificKeywords ? 0.3 : 0.7;
            double keywordWeight = hasSpecificKeywords ? 0.7 : 0.3;
            
            double combinedScore = (semanticSimilarity * semanticWeight) + (keywordScore * keywordWeight);
            
            scored.add(new ScoredRestaurant(restaurant, combinedScore));
        }

        scored.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return scored.subList(0, Math.min(topK, scored.size()));
    }

    private boolean hasSpecificFoodKeywords(String query) {
        String queryLower = query.toLowerCase();
        
        for (String food : CORE_FOODS) {
            if (queryLower.contains(food)) {
                return true;
            }
        }
        
        for (String tag : foodTagsFromDB) {
            if (queryLower.contains(tag)) {
                return true;
            }
        }
        
        return false;
    }

    public void refreshFoodKeywords() {
        initializeFoodKeywords();
    }

    public List<ScoredRestaurant> rankBySimilarity(
            double[] queryEmbedding,
            List<Restaurant> restaurants,
            EmbeddingService embeddingService,
            int topK
    ) {
        return rankBySimilarity(queryEmbedding, restaurants, embeddingService, topK, "");
    }

    public static class ScoredRestaurant {
        private final Restaurant restaurant;
        private final double score;

        public ScoredRestaurant(Restaurant restaurant, double score) {
            this.restaurant = restaurant;
            this.score = score;
        }

        public Restaurant getRestaurant() {
            return restaurant;
        }

        public double getScore() {
            return score;
        }
    }
}
