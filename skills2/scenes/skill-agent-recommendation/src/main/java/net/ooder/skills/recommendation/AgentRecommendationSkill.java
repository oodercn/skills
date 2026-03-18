package net.ooder.skills.recommendation;

import net.ooder.agent.sdk.annotation.Skill;
import net.ooder.agent.sdk.annotation.SkillMethod;
import net.ooder.agent.sdk.annotation.SkillEvent;
import net.ooder.agent.sdk.annotation.SkillConfig;
import net.ooder.agent.sdk.annotation.SkillParameter;
import net.ooder.agent.sdk.api.AgentContext;
import net.ooder.agent.sdk.api.AgentRegistry;
import net.ooder.agent.sdk.api.SkillContext;
import net.ooder.agent.sdk.api.SkillRegistry;
import net.ooder.agent.sdk.api.event.SkillEventPublisher;
import net.ooder.agent.sdk.api.event.SkillEventListener;
import net.ooder.agent.sdk.api.event.SkillEventData;
import net.ooder.agent.sdk.api.config.SkillConfiguration;
import net.ooder.agent.sdk.api.lifecycle.SkillLifecycle;
import net.ooder.agent.sdk.api.lifecycle.SkillLifecycleState;
import net.ooder.agent.sdk.api.discovery.AgentInfo;
import net.ooder.agent.sdk.api.discovery.SkillInfo;
import net.ooder.agent.sdk.api.capability.CapabilityInfo;
import net.ooder.agent.sdk.api.capability.CapabilityRegistry;
import net.ooder.agent.sdk.api.capability.CapabilityMatcher;
import net.ooder.agent.sdk.api.metrics.SkillMetrics;
import net.ooder.agent.sdk.api.metrics.MetricsCollector;
import net.ooder.agent.sdk.api.metrics.MetricsSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Skill(
    id = "skill-agent-recommendation",
    name = "Agent Recommendation Skill",
    version = "1.0.0",
    description = "Intelligent agent recommendation based on context, history, and user preferences",
    category = "recommendation",
    tags = {"agent", "recommendation", "intelligent", "context-aware"}
)
@Component
public class AgentRecommendationSkill implements SkillLifecycle, SkillEventListener {

    @Autowired
    private AgentRegistry agentRegistry;

    @Autowired
    private SkillRegistry skillRegistry;

    @Autowired
    private CapabilityRegistry capabilityRegistry;

    @Autowired
    private SkillEventPublisher eventPublisher;

    @Autowired
    private SkillConfiguration configuration;

    @Autowired
    private MetricsCollector metricsCollector;

    private final Map<String, UserPreference> userPreferences = new ConcurrentHashMap<>();
    private final Map<String, List<InteractionHistory>> interactionHistory = new ConcurrentHashMap<>();
    private final Map<String, AgentScore> agentScores = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // Configuration parameters
    private int historyWindowSize = 100;
    private double contextWeight = 0.4;
    private double historyWeight = 0.3;
    private double popularityWeight = 0.2;
    private double capabilityWeight = 0.1;
    private int recommendationCacheMinutes = 5;

    private volatile SkillLifecycleState lifecycleState = SkillLifecycleState.CREATED;

    @PostConstruct
    public void init() {
        loadConfiguration();
        startBackgroundTasks();
        lifecycleState = SkillLifecycleState.INITIALIZED;
    }

    @PreDestroy
    public void destroy() {
        lifecycleState = SkillLifecycleState.DESTROYING;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        lifecycleState = SkillLifecycleState.DESTROYED;
    }

    private void loadConfiguration() {
        this.historyWindowSize = configuration.getInt("recommendation.history.window.size", 100);
        this.contextWeight = configuration.getDouble("recommendation.weight.context", 0.4);
        this.historyWeight = configuration.getDouble("recommendation.weight.history", 0.3);
        this.popularityWeight = configuration.getDouble("recommendation.weight.popularity", 0.2);
        this.capabilityWeight = configuration.getDouble("recommendation.weight.capability", 0.1);
        this.recommendationCacheMinutes = configuration.getInt("recommendation.cache.minutes", 5);
    }

    private void startBackgroundTasks() {
        scheduler.scheduleAtFixedRate(this::updateAgentScores, 1, 5, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(this::cleanupOldHistory, 1, 1, TimeUnit.HOURS);
    }

    @SkillMethod(
        name = "recommendAgents",
        description = "Recommend agents based on context and user preferences",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true, description = "User identifier"),
            @SkillParameter(name = "context", type = "map", required = false, description = "Current context information"),
            @SkillParameter(name = "limit", type = "integer", required = false, description = "Maximum number of recommendations", defaultValue = "5")
        }
    )
    public RecommendationResult recommendAgents(String userId, Map<String, Object> context, int limit) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<AgentInfo> allAgents = agentRegistry.getAllAgents();
            if (allAgents.isEmpty()) {
                return new RecommendationResult(Collections.emptyList(), "No agents available");
            }

            List<ScoredAgent> scoredAgents = allAgents.stream()
                .map(agent -> new ScoredAgent(agent, calculateScore(agent, userId, context)))
                .sorted(Comparator.comparing(ScoredAgent::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

            List<AgentRecommendation> recommendations = scoredAgents.stream()
                .map(sa -> new AgentRecommendation(
                    sa.getAgent(),
                    sa.getScore(),
                    generateReason(sa, userId, context)
                ))
                .collect(Collectors.toList());

            recordRecommendation(userId, recommendations);
            
            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordTimer("recommendation.duration", duration);
            metricsCollector.incrementCounter("recommendation.success");

            return new RecommendationResult(recommendations, "Success");
        } catch (Exception e) {
            metricsCollector.incrementCounter("recommendation.failure");
            return new RecommendationResult(Collections.emptyList(), "Error: " + e.getMessage());
        }
    }

    @SkillMethod(
        name = "recommendByCapability",
        description = "Recommend agents by required capabilities",
        parameters = {
            @SkillParameter(name = "requiredCapabilities", type = "list", required = true, description = "List of required capability IDs"),
            @SkillParameter(name = "userId", type = "string", required = false, description = "User identifier for personalization"),
            @SkillParameter(name = "limit", type = "integer", required = false, description = "Maximum number of recommendations", defaultValue = "5")
        }
    )
    public RecommendationResult recommendByCapability(List<String> requiredCapabilities, String userId, int limit) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<AgentInfo> matchingAgents = agentRegistry.getAllAgents().stream()
                .filter(agent -> hasRequiredCapabilities(agent, requiredCapabilities))
                .collect(Collectors.toList());

            if (matchingAgents.isEmpty()) {
                return new RecommendationResult(Collections.emptyList(), "No agents match required capabilities");
            }

            Map<String, Object> context = new HashMap<>();
            context.put("requiredCapabilities", requiredCapabilities);

            List<ScoredAgent> scoredAgents = matchingAgents.stream()
                .map(agent -> new ScoredAgent(agent, calculateScore(agent, userId, context)))
                .sorted(Comparator.comparing(ScoredAgent::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

            List<AgentRecommendation> recommendations = scoredAgents.stream()
                .map(sa -> new AgentRecommendation(
                    sa.getAgent(),
                    sa.getScore(),
                    "Matches required capabilities: " + String.join(", ", requiredCapabilities)
                ))
                .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordTimer("recommendation.capability.duration", duration);
            metricsCollector.incrementCounter("recommendation.capability.success");

            return new RecommendationResult(recommendations, "Success");
        } catch (Exception e) {
            metricsCollector.incrementCounter("recommendation.capability.failure");
            return new RecommendationResult(Collections.emptyList(), "Error: " + e.getMessage());
        }
    }

    @SkillMethod(
        name = "recordInteraction",
        description = "Record user interaction with an agent for future recommendations",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "agentId", type = "string", required = true),
            @SkillParameter(name = "interactionType", type = "string", required = true, description = "Type: view, use, rate"),
            @SkillParameter(name = "rating", type = "double", required = false, description = "User rating if applicable")
        }
    )
    public void recordInteraction(String userId, String agentId, String interactionType, Double rating) {
        InteractionHistory history = new InteractionHistory(
            userId, agentId, interactionType, rating, System.currentTimeMillis()
        );
        
        interactionHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(history);
        
        // Update agent popularity score
        agentScores.compute(agentId, (id, score) -> {
            if (score == null) {
                score = new AgentScore(agentId);
            }
            score.recordInteraction(interactionType, rating);
            return score;
        });

        // Update user preferences
        UserPreference preference = userPreferences.computeIfAbsent(userId, UserPreference::new);
        preference.recordInteraction(agentId, interactionType, rating);

        metricsCollector.incrementCounter("recommendation.interaction.recorded");
        
        // Publish event for real-time updates
        eventPublisher.publishEvent("agent.interaction.recorded", 
            new InteractionEventData(userId, agentId, interactionType, rating));
    }

    @SkillMethod(
        name = "getUserPreferences",
        description = "Get user preference profile"
    )
    public UserPreference getUserPreferences(String userId) {
        return userPreferences.getOrDefault(userId, new UserPreference(userId));
    }

    @SkillMethod(
        name = "getPopularAgents",
        description = "Get most popular agents across all users",
        parameters = {
            @SkillParameter(name = "limit", type = "integer", required = false, defaultValue = "10")
        }
    )
    public List<AgentRecommendation> getPopularAgents(int limit) {
        return agentScores.values().stream()
            .sorted(Comparator.comparing(AgentScore::getPopularityScore).reversed())
            .limit(limit)
            .map(score -> {
                AgentInfo agent = agentRegistry.getAgent(score.getAgentId());
                return new AgentRecommendation(agent, score.getPopularityScore(), "Popular choice");
            })
            .filter(r -> r.getAgent() != null)
            .collect(Collectors.toList());
    }

    @SkillMethod(
        name = "getSimilarUsers",
        description = "Find users with similar preferences",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "limit", type = "integer", required = false, defaultValue = "5")
        }
    )
    public List<SimilarUser> getSimilarUsers(String userId, int limit) {
        UserPreference targetUser = userPreferences.get(userId);
        if (targetUser == null) {
            return Collections.emptyList();
        }

        return userPreferences.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(userId))
            .map(entry -> new SimilarUser(entry.getKey(), calculateSimilarity(targetUser, entry.getValue())))
            .sorted(Comparator.comparing(SimilarUser::getSimilarityScore).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    private double calculateScore(AgentInfo agent, String userId, Map<String, Object> context) {
        double contextScore = calculateContextScore(agent, context);
        double historyScore = calculateHistoryScore(agent, userId);
        double popularityScore = calculatePopularityScore(agent);
        double capabilityScore = calculateCapabilityScore(agent, context);

        return contextWeight * contextScore +
               historyWeight * historyScore +
               popularityWeight * popularityScore +
               capabilityWeight * capabilityScore;
    }

    private double calculateContextScore(AgentInfo agent, Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return 0.5;
        }

        double score = 0.0;
        int matchCount = 0;

        // Match agent tags with context keywords
        List<String> agentTags = agent.getTags();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue() != null ? entry.getValue().toString().toLowerCase() : "";
            
            for (String tag : agentTags) {
                if (tag.toLowerCase().contains(key) || tag.toLowerCase().contains(value)) {
                    score += 1.0;
                    matchCount++;
                }
            }
        }

        return matchCount > 0 ? Math.min(1.0, score / matchCount) : 0.5;
    }

    private double calculateHistoryScore(AgentInfo agent, String userId) {
        List<InteractionHistory> history = interactionHistory.get(userId);
        if (history == null || history.isEmpty()) {
            return 0.5;
        }

        long recentInteractions = history.stream()
            .filter(h -> h.getAgentId().equals(agent.getId()))
            .filter(h -> System.currentTimeMillis() - h.getTimestamp() < TimeUnit.DAYS.toMillis(30))
            .count();

        double ratingSum = history.stream()
            .filter(h -> h.getAgentId().equals(agent.getId()))
            .filter(h -> h.getRating() != null)
            .mapToDouble(InteractionHistory::getRating)
            .sum();

        long ratingCount = history.stream()
            .filter(h -> h.getAgentId().equals(agent.getId()))
            .filter(h -> h.getRating() != null)
            .count();

        double ratingScore = ratingCount > 0 ? ratingSum / ratingCount / 5.0 : 0.5;
        double frequencyScore = Math.min(1.0, recentInteractions / 10.0);

        return 0.6 * ratingScore + 0.4 * frequencyScore;
    }

    private double calculatePopularityScore(AgentInfo agent) {
        AgentScore score = agentScores.get(agent.getId());
        if (score == null) {
            return 0.5;
        }
        return score.getPopularityScore();
    }

    private double calculateCapabilityScore(AgentInfo agent, Map<String, Object> context) {
        if (context == null || !context.containsKey("requiredCapabilities")) {
            return 0.5;
        }

        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) context.get("requiredCapabilities");
        if (required == null || required.isEmpty()) {
            return 0.5;
        }

        List<String> agentCapabilities = agent.getCapabilities();
        long matchCount = required.stream()
            .filter(agentCapabilities::contains)
            .count();

        return (double) matchCount / required.size();
    }

    private boolean hasRequiredCapabilities(AgentInfo agent, List<String> requiredCapabilities) {
        List<String> agentCapabilities = agent.getCapabilities();
        return requiredCapabilities.stream().allMatch(agentCapabilities::contains);
    }

    private String generateReason(ScoredAgent scoredAgent, String userId, Map<String, Object> context) {
        List<String> reasons = new ArrayList<>();
        
        if (scoredAgent.getScore() > 0.8) {
            reasons.add("Highly relevant to your needs");
        }
        
        UserPreference preference = userPreferences.get(userId);
        if (preference != null && preference.hasUsedAgent(scoredAgent.getAgent().getId())) {
            reasons.add("You've used this agent before");
        }

        AgentScore score = agentScores.get(scoredAgent.getAgent().getId());
        if (score != null && score.getAverageRating() > 4.0) {
            reasons.add("Highly rated by other users");
        }

        return reasons.isEmpty() ? "Recommended based on your context" : String.join("; ", reasons);
    }

    private void recordRecommendation(String userId, List<AgentRecommendation> recommendations) {
        // Store for analytics and feedback loop
    }

    private void updateAgentScores() {
        // Recalculate popularity scores based on recent activity
        agentScores.values().forEach(AgentScore::recalculate);
    }

    private void cleanupOldHistory() {
        long cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(90);
        interactionHistory.values().forEach(history -> 
            history.removeIf(h -> h.getTimestamp() < cutoff)
        );
    }

    private double calculateSimilarity(UserPreference user1, UserPreference user2) {
        Set<String> agents1 = user1.getUsedAgents();
        Set<String> agents2 = user2.getUsedAgents();
        
        if (agents1.isEmpty() || agents2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(agents1);
        intersection.retainAll(agents2);

        Set<String> union = new HashSet<>(agents1);
        union.addAll(agents2);

        return (double) intersection.size() / union.size();
    }

    @Override
    public void onEvent(String eventType, SkillEventData eventData) {
        switch (eventType) {
            case "agent.registered":
                updateAgentScores();
                break;
            case "agent.unregistered":
                String agentId = eventData.getData("agentId", String.class);
                agentScores.remove(agentId);
                break;
            case "user.preference.updated":
                // Refresh recommendations
                break;
        }
    }

    @Override
    public List<String> getSubscribedEvents() {
        return Arrays.asList("agent.registered", "agent.unregistered", "user.preference.updated");
    }

    @Override
    public void initialize(SkillContext context) {
        lifecycleState = SkillLifecycleState.INITIALIZING;
    }

    @Override
    public void start() {
        lifecycleState = SkillLifecycleState.ACTIVE;
    }

    @Override
    public void stop() {
        lifecycleState = SkillLifecycleState.STOPPED;
    }

    @Override
    public SkillLifecycleState getState() {
        return lifecycleState;
    }

    // Data classes

    public static class RecommendationResult {
        private final List<AgentRecommendation> recommendations;
        private final String message;
        private final long timestamp;

        public RecommendationResult(List<AgentRecommendation> recommendations, String message) {
            this.recommendations = recommendations;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public List<AgentRecommendation> getRecommendations() { return recommendations; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class AgentRecommendation {
        private final AgentInfo agent;
        private final double score;
        private final String reason;

        public AgentRecommendation(AgentInfo agent, double score, String reason) {
            this.agent = agent;
            this.score = score;
            this.reason = reason;
        }

        public AgentInfo getAgent() { return agent; }
        public double getScore() { return score; }
        public String getReason() { return reason; }
    }

    private static class ScoredAgent {
        private final AgentInfo agent;
        private final double score;

        ScoredAgent(AgentInfo agent, double score) {
            this.agent = agent;
            this.score = score;
        }

        AgentInfo getAgent() { return agent; }
        double getScore() { return score; }
    }

    private static class InteractionHistory {
        private final String userId;
        private final String agentId;
        private final String interactionType;
        private final Double rating;
        private final long timestamp;

        InteractionHistory(String userId, String agentId, String interactionType, Double rating, long timestamp) {
            this.userId = userId;
            this.agentId = agentId;
            this.interactionType = interactionType;
            this.rating = rating;
            this.timestamp = timestamp;
        }

        String getAgentId() { return agentId; }
        Double getRating() { return rating; }
        long getTimestamp() { return timestamp; }
    }

    public static class UserPreference {
        private final String userId;
        private final Set<String> usedAgents = ConcurrentHashMap.newKeySet();
        private final Map<String, Double> agentRatings = new ConcurrentHashMap<>();
        private final List<String> preferredTags = new CopyOnWriteArrayList<>();

        UserPreference(String userId) {
            this.userId = userId;
        }

        void recordInteraction(String agentId, String interactionType, Double rating) {
            usedAgents.add(agentId);
            if (rating != null) {
                agentRatings.put(agentId, rating);
            }
        }

        boolean hasUsedAgent(String agentId) {
            return usedAgents.contains(agentId);
        }

        Set<String> getUsedAgents() {
            return new HashSet<>(usedAgents);
        }

        public String getUserId() { return userId; }
        public Map<String, Double> getAgentRatings() { return new HashMap<>(agentRatings); }
        public List<String> getPreferredTags() { return new ArrayList<>(preferredTags); }
    }

    private static class AgentScore {
        private final String agentId;
        private final AtomicLong viewCount = new AtomicLong(0);
        private final AtomicLong useCount = new AtomicLong(0);
        private final List<Double> ratings = new CopyOnWriteArrayList<>();
        private volatile double popularityScore = 0.5;

        AgentScore(String agentId) {
            this.agentId = agentId;
        }

        void recordInteraction(String interactionType, Double rating) {
            switch (interactionType.toLowerCase()) {
                case "view":
                    viewCount.incrementAndGet();
                    break;
                case "use":
                    useCount.incrementAndGet();
                    break;
            }
            if (rating != null) {
                ratings.add(rating);
            }
            recalculate();
        }

        void recalculate() {
            double ratingScore = ratings.isEmpty() ? 0.5 : 
                ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.5) / 5.0;
            double usageScore = Math.min(1.0, useCount.get() / 100.0);
            double viewScore = Math.min(1.0, viewCount.get() / 1000.0);
            
            popularityScore = 0.5 * ratingScore + 0.3 * usageScore + 0.2 * viewScore;
        }

        double getPopularityScore() {
            return popularityScore;
        }

        double getAverageRating() {
            return ratings.isEmpty() ? 0.0 :
                ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }

        String getAgentId() { return agentId; }
    }

    public static class SimilarUser {
        private final String userId;
        private final double similarityScore;

        SimilarUser(String userId, double similarityScore) {
            this.userId = userId;
            this.similarityScore = similarityScore;
        }

        public String getUserId() { return userId; }
        public double getSimilarityScore() { return similarityScore; }
    }

    private static class InteractionEventData implements SkillEventData {
        private final String userId;
        private final String agentId;
        private final String interactionType;
        private final Double rating;

        InteractionEventData(String userId, String agentId, String interactionType, Double rating) {
            this.userId = userId;
            this.agentId = agentId;
            this.interactionType = interactionType;
            this.rating = rating;
        }

        @Override
        public String getEventType() {
            return "agent.interaction.recorded";
        }

        @Override
        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        @Override
        public <T> T getData(String key, Class<T> type) {
            switch (key) {
                case "userId": return type.cast(userId);
                case "agentId": return type.cast(agentId);
                case "interactionType": return type.cast(interactionType);
                case "rating": return type.cast(rating);
                default: return null;
            }
        }

        @Override
        public Map<String, Object> getAllData() {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("agentId", agentId);
            data.put("interactionType", interactionType);
            data.put("rating", rating);
            return data;
        }
    }
}
