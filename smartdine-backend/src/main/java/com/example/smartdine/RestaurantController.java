package com.example.smartdine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private RestaurantTagRepository tagRepo;

    @Autowired
    private SimilarityService similarityService;

    @PostMapping
    public Restaurant createRestaurant(@RequestBody Restaurant r) {

      
        r.setRating(0.0);

        Restaurant saved = restaurantRepo.save(r);
        saveTags(saved.getId(), r.getTags());
        
        similarityService.refreshFoodKeywords();
        
        return saved;
    }

    @PutMapping("/{id}")
public ResponseEntity<Restaurant> updateRestaurant(
        @PathVariable Long id,
        @RequestBody Restaurant r) {

    return restaurantRepo.findById(id)
            .map(db -> {

                if (r.getName() != null && !r.getName().isEmpty())
                    db.setName(r.getName());

                if (r.getCuisine() != null && !r.getCuisine().isEmpty())
                    db.setCuisine(r.getCuisine());

                if (r.getPriceRange() != null && !r.getPriceRange().isEmpty())
                    db.setPriceRange(r.getPriceRange());

                if (r.getLocation() != null && !r.getLocation().isEmpty())
                    db.setLocation(r.getLocation());

                if (r.getArea() != null && !r.getArea().isEmpty())
                    db.setArea(r.getArea());

                if (r.getLatitude() != null)
                    db.setLatitude(r.getLatitude());

                if (r.getLongitude() != null)
                    db.setLongitude(r.getLongitude());

                if (r.getDescription() != null && !r.getDescription().isEmpty())
                    db.setDescription(r.getDescription());

                if (r.getTags() != null && !r.getTags().isEmpty()) {
                    db.setTags(r.getTags());
                    tagRepo.deleteAll(tagRepo.findByRestaurantId(id));
                    saveTags(id, r.getTags());
                    similarityService.refreshFoodKeywords();
                }

                if (r.getImages() != null && !r.getImages().isEmpty())
                    db.setImages(r.getImages());

                Restaurant saved = restaurantRepo.save(db);
                return ResponseEntity.ok(saved);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
        }

    

   
    private void saveTags(Long restaurantId, String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) return;

        String[] tagArray = tagsString.split(",");
        for (String tag : tagArray) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                tagRepo.save(new RestaurantTag(restaurantId, trimmed));
            }
        }
    }
}
