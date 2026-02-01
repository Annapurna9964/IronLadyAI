package com.ironlady.reco.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;

@Component
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
@Primary
public class OpenAIExplanationProvider implements ExplanationProvider {

    private final boolean enabled;
    private final String model;
    private final String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public OpenAIExplanationProvider(
            @Value("${ai.enabled:false}") boolean enabled,
            @Value("${ai.model:gpt-3.5-turbo}") String model,
            @Value("${OPENAI_API_KEY:}") String apiKey
    ) {
        this.enabled = enabled;
        this.model = model;
        this.apiKey = apiKey;
    }

    @Override
    public String buildReason(String course, String careerStage, String goal, String challenge, String fallbackReason) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return fallbackReason;
        }
        try {
            String prompt = "Write a concise, friendly 1-2 sentence explanation (no marketing fluff) for recommending '" + course +
                    "' to a woman professional who is at '" + careerStage + "' stage, whose primary goal is '" + goal +
                    "' and key challenge is '" + challenge + "'. Keep it under 40 words.";

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful, concise career advisor."),
                    Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 120);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
            Map<?, ?> resp = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", req, Map.class);
            if (resp == null) return fallbackReason;
            Object choices = resp.get("choices");
            if (!(choices instanceof List<?> list) || list.isEmpty()) return fallbackReason;
            Object message = ((Map<?, ?>) list.get(0)).get("message");
            if (!(message instanceof Map<?, ?> msg)) return fallbackReason;
            Object content = msg.get("content");
            if (content == null) return fallbackReason;
            return content.toString().trim();
        } catch (Exception e) {
            return fallbackReason;
        }
    }
}
