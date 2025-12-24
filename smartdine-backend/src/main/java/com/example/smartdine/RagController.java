package com.example.smartdine;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RagController {

    private final RagRecommendationService ragRecommendationService;

    public RagController(RagRecommendationService ragRecommendationService) {
        this.ragRecommendationService = ragRecommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<RagRecommendationService.RagResponse> getRecommendation(
            @RequestBody RagRequest request
    ) {
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RagRecommendationService.RagResponse response =
                ragRecommendationService.getRecommendation(request.getQuery());

        return ResponseEntity.ok(response);
    }

    public static class RagRequest {
        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}
