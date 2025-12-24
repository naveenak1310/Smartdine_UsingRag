package com.example.smartdine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmbeddingInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final EmbeddingService embeddingService;

    public EmbeddingInitializer(
            RestaurantRepository restaurantRepository,
            EmbeddingService embeddingService
    ) {
        this.restaurantRepository = restaurantRepository;
        this.embeddingService = embeddingService;
    }

    @Override
    public void run(String... args) {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        boolean needsUpdate = restaurants.stream()
                .anyMatch(r -> r.getEmbedding() == null || r.getEmbedding().isEmpty());

        if (needsUpdate) {
            System.out.println("Generating embeddings for restaurants...");

            embeddingService.updateIdf(restaurants);

            for (Restaurant restaurant : restaurants) {
                if (restaurant.getEmbedding() == null || restaurant.getEmbedding().isEmpty()) {
                    double[] embedding = embeddingService.generateRestaurantEmbedding(restaurant);
                    restaurant.setEmbedding(embeddingService.embeddingToJson(embedding));
                }
            }

            restaurantRepository.saveAll(restaurants);
            System.out.println("Embeddings generated and saved!");
        } else {
            System.out.println("All restaurants already have embeddings.");
        }
    }
}
