package com.example.smartdine;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private final Map<String, Double> idf = new HashMap<>();
    private static final int EMBEDDING_DIM = 100;

    public double[] generateEmbedding(String text) {
        if (text == null || text.isEmpty()) {
            return new double[EMBEDDING_DIM];
        }

        String[] words = text.toLowerCase()
            .replaceAll("[^a-z0-9 ]", " ")
            .split("\\s+");

        Map<String, Integer> termFreq = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                termFreq.put(word, termFreq.getOrDefault(word, 0) + 1);
            }
        }

        double[] embedding = new double[EMBEDDING_DIM];
        int idx = 0;

        List<String> sortedTerms = termFreq.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String term : sortedTerms) {
            if (idx >= EMBEDDING_DIM) break;
            
            double tf = (double) termFreq.get(term) / words.length;
            double idfVal = idf.getOrDefault(term, 1.0);
            double tfidf = tf * idfVal;
            
            int hash = Math.abs(term.hashCode());
            for (int i = 0; i < 3 && idx < EMBEDDING_DIM; i++, idx++) {
                embedding[idx] = tfidf * Math.sin(hash + i);
            }
        }

        double norm = 0.0;
        for (double val : embedding) {
            norm += val * val;
        }
        norm = Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= norm;
            }
        }

        return embedding;
    }

    public double[] generateRestaurantEmbedding(Restaurant restaurant) {
        StringBuilder text = new StringBuilder();
        
        if (restaurant.getName() != null) {
            text.append(restaurant.getName()).append(" ");
            text.append(restaurant.getName()).append(" ");
        }
        
        if (restaurant.getCuisine() != null) {
            text.append(restaurant.getCuisine()).append(" ");
        }
        
        if (restaurant.getPriceRange() != null) {
            text.append(restaurant.getPriceRange()).append(" ");
        }
        
        if (restaurant.getTags() != null) {
            text.append(restaurant.getTags()).append(" ");
        }
        
        if (restaurant.getDescription() != null) {
            text.append(restaurant.getDescription()).append(" ");
        }

        return generateEmbedding(text.toString());
    }

    public String embeddingToJson(double[] embedding) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) json.append(",");
            json.append(String.format("%.6f", embedding[i]));
        }
        json.append("]");
        return json.toString();
    }

    public double[] jsonToEmbedding(String json) {
        if (json == null || json.isEmpty()) {
            return new double[EMBEDDING_DIM];
        }

        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        String[] parts = json.split(",");
        double[] embedding = new double[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                embedding[i] = Double.parseDouble(parts[i].trim());
            } catch (NumberFormatException e) {
                embedding[i] = 0.0;
            }
        }

        return embedding;
    }

    public void updateIdf(List<Restaurant> restaurants) {
        Map<String, Integer> docFreq = new HashMap<>();
        int totalDocs = restaurants.size();

        for (Restaurant restaurant : restaurants) {
            StringBuilder text = new StringBuilder();
            if (restaurant.getName() != null) text.append(restaurant.getName()).append(" ");
            if (restaurant.getCuisine() != null) text.append(restaurant.getCuisine()).append(" ");
            if (restaurant.getTags() != null) text.append(restaurant.getTags()).append(" ");
            if (restaurant.getDescription() != null) text.append(restaurant.getDescription()).append(" ");

            Set<String> uniqueWords = new HashSet<>();
            String[] words = text.toString().toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .split("\\s+");

            for (String word : words) {
                if (!word.isEmpty()) {
                    uniqueWords.add(word);
                }
            }

            for (String word : uniqueWords) {
                docFreq.put(word, docFreq.getOrDefault(word, 0) + 1);
            }
        }

        idf.clear();
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            idf.put(entry.getKey(), Math.log((double) totalDocs / entry.getValue()));
        }
    }
}
