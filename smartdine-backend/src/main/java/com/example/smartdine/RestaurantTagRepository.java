package com.example.smartdine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RestaurantTagRepository extends JpaRepository<RestaurantTag, Long> {

    @Query("SELECT DISTINCT t.tag FROM RestaurantTag t")
    List<String> findAllTags();

    @Query("SELECT DISTINCT t.restaurantId FROM RestaurantTag t WHERE t.tag IN :tags")
    List<Long> findRestaurantIdsByTags(List<String> tags);

    List<RestaurantTag> findByRestaurantId(Long restaurantId);
}
