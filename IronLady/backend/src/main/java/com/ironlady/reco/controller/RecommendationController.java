package com.ironlady.reco.controller;

import com.ironlady.reco.dto.RecommendationResponse;
import com.ironlady.reco.dto.UserInput;
import com.ironlady.reco.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Dev-friendly; tighten in production
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @PostMapping(value = "/recommend-course", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationResponse recommend(@Valid @RequestBody UserInput input) {
        return service.recommend(input);
    }
}
