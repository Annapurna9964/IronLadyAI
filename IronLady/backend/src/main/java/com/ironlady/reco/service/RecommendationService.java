package com.ironlady.reco.service;

import org.springframework.stereotype.Service;
import com.ironlady.reco.dto.RecommendationResponse;
import com.ironlady.reco.dto.UserInput;
import com.ironlady.reco.ai.ExplanationProvider;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecommendationService {

    private final ExplanationProvider explanationProvider;
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    public RecommendationService(ExplanationProvider explanationProvider) {
        this.explanationProvider = explanationProvider;
    }

    public RecommendationResponse recommend(UserInput input) {
        String cs = input.getCareerStage();
        String goal = input.getGoal();
        String challenge = input.getChallenge();

        String course;
        // Rule-based mapping (priority order matters)
        if ("working".equals(cs) && "leadership".equals(goal)) {
            course = "Leadership Essentials Program";
        } else if ("mid".equals(cs)) {
            course = "Master of Business Warfare";
        } else if ("income".equals(goal)) {
            course = "1-Crore Club";
        } else if ("senior".equals(cs)) {
            course = "100 Board Members";
        } else {
            course = "Leadership Essentials Program";
        }

        if (log.isInfoEnabled()) {
            log.info("Recommendation input: careerStage={}, goal={}, challenge={} -> course={}", cs, goal, challenge, course);
        }

        String fallbackReason = buildReason(course, cs, goal, challenge);
        String reason = explanationProvider.buildReason(course, cs, goal, challenge, fallbackReason);

        RecommendationResponse resp = new RecommendationResponse(course, reason);
        resp.setTips(buildTips(cs, goal, challenge));
        return resp;
    }

    private String buildReason(String course, String cs, String goal, String challenge) {
        String stageText = switch (cs) {
            case "starting" -> "starting your career";
            case "working" -> "a working professional";
            case "mid" -> "a mid-career professional";
            case "senior" -> "a senior leader/entrepreneur";
            default -> "a professional";
        };
        String goalText = switch (goal) {
            case "leadership" -> "build strong leadership skills";
            case "growth" -> "accelerate career growth";
            case "strategic" -> "break barriers and think strategically";
            case "income" -> "increase income and executive influence";
            default -> "advance your career";
        };
        String challengeText = switch (challenge) {
            case "confidence" -> "address confidence gaps";
            case "politics" -> "navigate office politics";
            case "stagnation" -> "overcome career stagnation";
            case "direction" -> "gain strategic direction and network";
            default -> "reach your goals";
        };
        return String.format("As %s aiming to %s, this program helps you %s. We recommend %s.",
                stageText, goalText, challengeText, course);
    }

    private List<String> buildTips(String cs, String goal, String challenge) {
        List<String> tips = new ArrayList<>();
        // simple heuristics for tips
        if ("confidence".equals(challenge)) tips.add("Join weekly peer circles to practice executive presence.");
        if ("politics".equals(challenge)) tips.add("Map stakeholders and prepare influence narratives.");
        if ("stagnation".equals(challenge)) tips.add("Set 30-60-90 day outcomes and a sponsorship plan.");
        if ("direction".equals(challenge)) tips.add("Book two network conversations per week in your target domain.");

        if ("leadership".equals(goal)) tips.add("Block 2 hrs/week for leadership case practice.");
        if ("income".equals(goal)) tips.add("Track impact metrics to anchor comp negotiations.");

        if (tips.isEmpty()) tips.add("Start with a quick win this week aligned to your goal.");
        return tips;
    }
}
