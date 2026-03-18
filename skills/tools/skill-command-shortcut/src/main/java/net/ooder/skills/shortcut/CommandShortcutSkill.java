package net.ooder.skills.shortcut;

import net.ooder.agent.sdk.annotation.Skill;
import net.ooder.agent.sdk.annotation.SkillMethod;
import net.ooder.agent.sdk.annotation.SkillEvent;
import net.ooder.agent.sdk.annotation.SkillConfig;
import net.ooder.agent.sdk.annotation.SkillParameter;
import net.ooder.agent.sdk.api.SkillContext;
import net.ooder.agent.sdk.api.SkillRegistry;
import net.ooder.agent.sdk.api.event.SkillEventPublisher;
import net.ooder.agent.sdk.api.event.SkillEventListener;
import net.ooder.agent.sdk.api.event.SkillEventData;
import net.ooder.agent.sdk.api.config.SkillConfiguration;
import net.ooder.agent.sdk.api.lifecycle.SkillLifecycle;
import net.ooder.agent.sdk.api.lifecycle.SkillLifecycleState;
import net.ooder.agent.sdk.api.metrics.MetricsCollector;
import net.ooder.agent.sdk.api.discovery.SkillInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Skill(
    id = "skill-command-shortcut",
    name = "Command Shortcut Skill",
    version = "1.0.0",
    description = "Manages command shortcuts, aliases, and quick actions for efficient workflow",
    category = "productivity",
    tags = {"command", "shortcut", "alias", "quick-action", "productivity"}
)
@Component
public class CommandShortcutSkill implements SkillLifecycle, SkillEventListener {

    @Autowired
    private SkillRegistry skillRegistry;

    @Autowired
    private SkillEventPublisher eventPublisher;

    @Autowired
    private SkillConfiguration configuration;

    @Autowired
    private MetricsCollector metricsCollector;

    // Storage for shortcuts - userId -> shortcutName -> Shortcut
    private final Map<String, Map<String, Shortcut>> userShortcuts = new ConcurrentHashMap<>();
    
    // Global shortcuts available to all users
    private final Map<String, Shortcut> globalShortcuts = new ConcurrentHashMap<>();
    
    // Command history for suggestions
    private final Map<String, List<CommandUsage>> commandHistory = new ConcurrentHashMap<>();
    
    // Template registry
    private final Map<String, CommandTemplate> templates = new ConcurrentHashMap<>();
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Configuration
    private int maxShortcutsPerUser = 100;
    private int maxHistorySize = 50;
    private boolean enableAutoSuggestions = true;
    private int suggestionThreshold = 3;
    
    private volatile SkillLifecycleState lifecycleState = SkillLifecycleState.CREATED;

    @PostConstruct
    public void init() {
        loadConfiguration();
        initializeDefaultShortcuts();
        initializeDefaultTemplates();
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
        this.maxShortcutsPerUser = configuration.getInt("shortcut.max.per.user", 100);
        this.maxHistorySize = configuration.getInt("shortcut.history.max.size", 50);
        this.enableAutoSuggestions = configuration.getBoolean("shortcut.suggestions.enabled", true);
        this.suggestionThreshold = configuration.getInt("shortcut.suggestion.threshold", 3);
    }

    private void initializeDefaultShortcuts() {
        // Global shortcuts for common operations
        registerGlobalShortcut(new Shortcut(
            "help",
            "Show help information",
            "/system/help",
            Collections.emptyMap(),
            true,
            "global"
        ));
        
        registerGlobalShortcut(new Shortcut(
            "agents",
            "List all available agents",
            "/agent/list",
            Collections.singletonMap("format", "summary"),
            true,
            "global"
        ));
        
        registerGlobalShortcut(new Shortcut(
            "skills",
            "List all available skills",
            "/skill/list",
            Collections.emptyMap(),
            true,
            "global"
        ));
        
        registerGlobalShortcut(new Shortcut(
            "status",
            "Show system status",
            "/system/status",
            Collections.emptyMap(),
            true,
            "global"
        ));
    }

    private void initializeDefaultTemplates() {
        templates.put("skill.invoke", new CommandTemplate(
            "skill.invoke",
            "Invoke a skill with parameters",
            "/skill/{skillId}/invoke",
            Arrays.asList("skillId", "param1", "param2"),
            "Invoke skill {skillId}"
        ));
        
        templates.put("agent.chat", new CommandTemplate(
            "agent.chat",
            "Start chat with an agent",
            "/agent/{agentId}/chat",
            Arrays.asList("agentId", "message"),
            "Chat with agent {agentId}"
        ));
        
        templates.put("search", new CommandTemplate(
            "search",
            "Search across agents and skills",
            "/search?q={query}",
            Collections.singletonList("query"),
            "Search for {query}"
        ));
    }

    private void startBackgroundTasks() {
        scheduler.scheduleAtFixedRate(this::cleanupOldHistory, 1, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(this::analyzeUsagePatterns, 5, 5, TimeUnit.MINUTES);
    }

    @SkillMethod(
        name = "createShortcut",
        description = "Create a new command shortcut",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "shortcutName", type = "string", required = true, description = "Unique name for the shortcut"),
            @SkillParameter(name = "description", type = "string", required = true),
            @SkillParameter(name = "targetCommand", type = "string", required = true, description = "The full command to execute"),
            @SkillParameter(name = "parameters", type = "map", required = false, description = "Default parameters")
        }
    )
    public ShortcutResult createShortcut(String userId, String shortcutName, String description, 
                                          String targetCommand, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate shortcut name
            if (!isValidShortcutName(shortcutName)) {
                return new ShortcutResult(false, "Invalid shortcut name. Use alphanumeric characters, hyphens, and underscores only.");
            }

            Map<String, Shortcut> userShortcutMap = userShortcuts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            
            // Check limit
            if (userShortcutMap.size() >= maxShortcutsPerUser) {
                return new ShortcutResult(false, "Maximum number of shortcuts reached (" + maxShortcutsPerUser + ")");
            }

            // Check for conflicts
            if (userShortcutMap.containsKey(shortcutName) || globalShortcuts.containsKey(shortcutName)) {
                return new ShortcutResult(false, "Shortcut name '" + shortcutName + "' already exists");
            }

            Shortcut shortcut = new Shortcut(
                shortcutName,
                description,
                targetCommand,
                parameters != null ? parameters : Collections.emptyMap(),
                false,
                userId
            );

            userShortcutMap.put(shortcutName, shortcut);
            
            eventPublisher.publishEvent("shortcut.created", 
                new ShortcutEventData(userId, shortcutName, "created"));
            
            metricsCollector.incrementCounter("shortcut.created");
            
            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordTimer("shortcut.create.duration", duration);
            
            return new ShortcutResult(true, "Shortcut created successfully", shortcut);
        } catch (Exception e) {
            metricsCollector.incrementCounter("shortcut.create.failure");
            return new ShortcutResult(false, "Error creating shortcut: " + e.getMessage());
        }
    }

    @SkillMethod(
        name = "executeShortcut",
        description = "Execute a command shortcut",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "shortcutName", type = "string", required = true),
            @SkillParameter(name = "overrideParams", type = "map", required = false, description = "Parameters to override defaults")
        }
    )
    public ExecutionResult executeShortcut(String userId, String shortcutName, Map<String, Object> overrideParams) {
        long startTime = System.currentTimeMillis();
        
        try {
            Shortcut shortcut = findShortcut(userId, shortcutName);
            if (shortcut == null) {
                return new ExecutionResult(false, "Shortcut not found: " + shortcutName, null);
            }

            // Merge parameters
            Map<String, Object> mergedParams = new HashMap<>(shortcut.getParameters());
            if (overrideParams != null) {
                mergedParams.putAll(overrideParams);
            }

            // Build final command
            String finalCommand = buildCommand(shortcut.getTargetCommand(), mergedParams);

            // Record usage
            recordCommandUsage(userId, shortcutName, finalCommand);

            // Execute (in real implementation, this would call the actual command executor)
            ExecutionResult result = executeCommand(finalCommand, mergedParams);
            
            metricsCollector.incrementCounter("shortcut.executed");
            
            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordTimer("shortcut.execute.duration", duration);

            eventPublisher.publishEvent("shortcut.executed", 
                new ShortcutEventData(userId, shortcutName, "executed"));

            return result;
        } catch (Exception e) {
            metricsCollector.incrementCounter("shortcut.execute.failure");
            return new ExecutionResult(false, "Error executing shortcut: " + e.getMessage(), null);
        }
    }

    @SkillMethod(
        name = "deleteShortcut",
        description = "Delete a user shortcut",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "shortcutName", type = "string", required = true)
        }
    )
    public ShortcutResult deleteShortcut(String userId, String shortcutName) {
        Map<String, Shortcut> userShortcutMap = userShortcuts.get(userId);
        if (userShortcutMap == null) {
            return new ShortcutResult(false, "No shortcuts found for user");
        }

        Shortcut removed = userShortcutMap.remove(shortcutName);
        if (removed == null) {
            return new ShortcutResult(false, "Shortcut not found: " + shortcutName);
        }

        eventPublisher.publishEvent("shortcut.deleted", 
            new ShortcutEventData(userId, shortcutName, "deleted"));
        metricsCollector.incrementCounter("shortcut.deleted");

        return new ShortcutResult(true, "Shortcut deleted successfully");
    }

    @SkillMethod(
        name = "listShortcuts",
        description = "List all shortcuts for a user including global ones",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "includeGlobal", type = "boolean", required = false, defaultValue = "true")
        }
    )
    public List<Shortcut> listShortcuts(String userId, boolean includeGlobal) {
        List<Shortcut> result = new ArrayList<>();
        
        if (includeGlobal) {
            result.addAll(globalShortcuts.values());
        }
        
        Map<String, Shortcut> userShortcutMap = userShortcuts.get(userId);
        if (userShortcutMap != null) {
            result.addAll(userShortcutMap.values());
        }
        
        return result.stream()
            .sorted(Comparator.comparing(Shortcut::getName))
            .collect(Collectors.toList());
    }

    @SkillMethod(
        name = "searchShortcuts",
        description = "Search shortcuts by name or description",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "query", type = "string", required = true),
            @SkillParameter(name = "includeGlobal", type = "boolean", required = false, defaultValue = "true")
        }
    )
    public List<Shortcut> searchShortcuts(String userId, String query, boolean includeGlobal) {
        String lowerQuery = query.toLowerCase();
        
        return listShortcuts(userId, includeGlobal).stream()
            .filter(s -> s.getName().toLowerCase().contains(lowerQuery) ||
                        s.getDescription().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }

    @SkillMethod(
        name = "getSuggestions",
        description = "Get command suggestions based on usage history",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "context", type = "string", required = false, description = "Current context for contextual suggestions"),
            @SkillParameter(name = "limit", type = "integer", required = false, defaultValue = "5")
        }
    )
    public List<Suggestion> getSuggestions(String userId, String context, int limit) {
        if (!enableAutoSuggestions) {
            return Collections.emptyList();
        }

        List<CommandUsage> history = commandHistory.getOrDefault(userId, Collections.emptyList());
        if (history.isEmpty()) {
            return Collections.emptyList();
        }

        // Calculate frequency scores
        Map<String, Long> frequencyMap = history.stream()
            .collect(Collectors.groupingBy(CommandUsage::getShortcutName, Collectors.counting()));

        // Filter by threshold and sort by frequency
        return frequencyMap.entrySet().stream()
            .filter(e -> e.getValue() >= suggestionThreshold)
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(e -> new Suggestion(
                e.getKey(),
                e.getValue(),
                findShortcut(userId, e.getKey())
            ))
            .collect(Collectors.toList());
    }

    @SkillMethod(
        name = "createFromTemplate",
        description = "Create a shortcut from a predefined template",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "templateId", type = "string", required = true),
            @SkillParameter(name = "shortcutName", type = "string", required = true),
            @SkillParameter(name = "templateParams", type = "map", required = true, description = "Parameters to fill in the template")
        }
    )
    public ShortcutResult createFromTemplate(String userId, String templateId, String shortcutName, 
                                              Map<String, String> templateParams) {
        CommandTemplate template = templates.get(templateId);
        if (template == null) {
            return new ShortcutResult(false, "Template not found: " + templateId);
        }

        String targetCommand = template.getCommandPattern();
        for (Map.Entry<String, String> entry : templateParams.entrySet()) {
            targetCommand = targetCommand.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return createShortcut(userId, shortcutName, template.getDescription(), targetCommand, Collections.emptyMap());
    }

    @SkillMethod(
        name = "listTemplates",
        description = "List available command templates"
    )
    public List<CommandTemplate> listTemplates() {
        return new ArrayList<>(templates.values());
    }

    @SkillMethod(
        name = "getUsageStats",
        description = "Get usage statistics for shortcuts",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true)
        }
    )
    public UsageStats getUsageStats(String userId) {
        List<CommandUsage> history = commandHistory.getOrDefault(userId, Collections.emptyList());
        
        Map<String, Long> usageCount = history.stream()
            .collect(Collectors.groupingBy(CommandUsage::getShortcutName, Collectors.counting()));
        
        long totalExecutions = history.size();
        String mostUsed = usageCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        return new UsageStats(totalExecutions, mostUsed, usageCount);
    }

    @SkillMethod(
        name = "importShortcuts",
        description = "Import shortcuts from JSON or CSV format",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "format", type = "string", required = true, description = "json or csv"),
            @SkillParameter(name = "data", type = "string", required = true, description = "Shortcut data in specified format")
        }
    )
    public ImportResult importShortcuts(String userId, String format, String data) {
        int imported = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        try {
            if ("json".equalsIgnoreCase(format)) {
                // Parse JSON format
                // Implementation would use JSON parser
                // For now, placeholder
            } else if ("csv".equalsIgnoreCase(format)) {
                // Parse CSV format
                String[] lines = data.split("\n");
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        ShortcutResult result = createShortcut(userId, parts[0].trim(), parts[1].trim(), 
                                                                  parts[2].trim(), Collections.emptyMap());
                        if (result.isSuccess()) {
                            imported++;
                        } else {
                            failed++;
                            errors.add(parts[0] + ": " + result.getMessage());
                        }
                    }
                }
            }
            
            return new ImportResult(imported, failed, errors);
        } catch (Exception e) {
            return new ImportResult(imported, failed + 1, 
                Collections.singletonList("Import error: " + e.getMessage()));
        }
    }

    @SkillMethod(
        name = "exportShortcuts",
        description = "Export shortcuts to JSON or CSV format",
        parameters = {
            @SkillParameter(name = "userId", type = "string", required = true),
            @SkillParameter(name = "format", type = "string", required = true, description = "json or csv")
        }
    )
    public String exportShortcuts(String userId, String format) {
        List<Shortcut> shortcuts = listShortcuts(userId, false);
        
        if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder("name,description,targetCommand\n");
            for (Shortcut s : shortcuts) {
                csv.append(String.format("%s,%s,%s\n", 
                    s.getName(), s.getDescription(), s.getTargetCommand()));
            }
            return csv.toString();
        } else {
            // JSON format - placeholder
            return "{\"shortcuts\": " + shortcuts.size() + "}";
        }
    }

    // Private helper methods

    private boolean isValidShortcutName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_-]+$");
    }

    private Shortcut findShortcut(String userId, String shortcutName) {
        // Check user shortcuts first
        Map<String, Shortcut> userShortcutMap = userShortcuts.get(userId);
        if (userShortcutMap != null && userShortcutMap.containsKey(shortcutName)) {
            return userShortcutMap.get(shortcutName);
        }
        // Check global shortcuts
        return globalShortcuts.get(shortcutName);
    }

    private void registerGlobalShortcut(Shortcut shortcut) {
        globalShortcuts.put(shortcut.getName(), shortcut);
    }

    private String buildCommand(String template, Map<String, Object> params) {
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", 
                entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return result;
    }

    private void recordCommandUsage(String userId, String shortcutName, String command) {
        List<CommandUsage> history = commandHistory.computeIfAbsent(userId, k -> new ArrayList<>());
        history.add(new CommandUsage(shortcutName, command, System.currentTimeMillis()));
        
        // Trim history if too large
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }

    private ExecutionResult executeCommand(String command, Map<String, Object> params) {
        // In real implementation, this would integrate with the command execution system
        // For now, return a simulated result
        return new ExecutionResult(true, "Command executed: " + command, params);
    }

    private void cleanupOldHistory() {
        long cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30);
        commandHistory.values().forEach(history ->
            history.removeIf(h -> h.getTimestamp() < cutoff)
        );
    }

    private void analyzeUsagePatterns() {
        // Analyze patterns and potentially auto-create suggestions
        // This would be expanded in a real implementation
    }

    @Override
    public void onEvent(String eventType, SkillEventData eventData) {
        // Handle relevant events
    }

    @Override
    public List<String> getSubscribedEvents() {
        return Collections.emptyList();
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

    public static class Shortcut {
        private final String name;
        private final String description;
        private final String targetCommand;
        private final Map<String, Object> parameters;
        private final boolean global;
        private final String ownerId;
        private final long createdAt;

        public Shortcut(String name, String description, String targetCommand, 
                       Map<String, Object> parameters, boolean global, String ownerId) {
            this.name = name;
            this.description = description;
            this.targetCommand = targetCommand;
            this.parameters = new HashMap<>(parameters);
            this.global = global;
            this.ownerId = ownerId;
            this.createdAt = System.currentTimeMillis();
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getTargetCommand() { return targetCommand; }
        public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
        public boolean isGlobal() { return global; }
        public String getOwnerId() { return ownerId; }
        public long getCreatedAt() { return createdAt; }
    }

    public static class ShortcutResult {
        private final boolean success;
        private final String message;
        private final Shortcut shortcut;

        public ShortcutResult(boolean success, String message) {
            this(success, message, null);
        }

        public ShortcutResult(boolean success, String message, Shortcut shortcut) {
            this.success = success;
            this.message = message;
            this.shortcut = shortcut;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Shortcut getShortcut() { return shortcut; }
    }

    public static class ExecutionResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> result;

        public ExecutionResult(boolean success, String message, Map<String, Object> result) {
            this.success = success;
            this.message = message;
            this.result = result != null ? new HashMap<>(result) : null;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Map<String, Object> getResult() { return result; }
    }

    public static class CommandTemplate {
        private final String id;
        private final String description;
        private final String commandPattern;
        private final List<String> parameters;
        private final String example;

        public CommandTemplate(String id, String description, String commandPattern, 
                              List<String> parameters, String example) {
            this.id = id;
            this.description = description;
            this.commandPattern = commandPattern;
            this.parameters = new ArrayList<>(parameters);
            this.example = example;
        }

        public String getId() { return id; }
        public String getDescription() { return description; }
        public String getCommandPattern() { return commandPattern; }
        public List<String> getParameters() { return new ArrayList<>(parameters); }
        public String getExample() { return example; }
    }

    public static class Suggestion {
        private final String shortcutName;
        private final long usageCount;
        private final Shortcut shortcut;

        public Suggestion(String shortcutName, long usageCount, Shortcut shortcut) {
            this.shortcutName = shortcutName;
            this.usageCount = usageCount;
            this.shortcut = shortcut;
        }

        public String getShortcutName() { return shortcutName; }
        public long getUsageCount() { return usageCount; }
        public Shortcut getShortcut() { return shortcut; }
    }

    public static class UsageStats {
        private final long totalExecutions;
        private final String mostUsedShortcut;
        private final Map<String, Long> usageBreakdown;

        public UsageStats(long totalExecutions, String mostUsedShortcut, Map<String, Long> usageBreakdown) {
            this.totalExecutions = totalExecutions;
            this.mostUsedShortcut = mostUsedShortcut;
            this.usageBreakdown = new HashMap<>(usageBreakdown);
        }

        public long getTotalExecutions() { return totalExecutions; }
        public String getMostUsedShortcut() { return mostUsedShortcut; }
        public Map<String, Long> getUsageBreakdown() { return new HashMap<>(usageBreakdown); }
    }

    public static class ImportResult {
        private final int imported;
        private final int failed;
        private final List<String> errors;

        public ImportResult(int imported, int failed, List<String> errors) {
            this.imported = imported;
            this.failed = failed;
            this.errors = new ArrayList<>(errors);
        }

        public int getImported() { return imported; }
        public int getFailed() { return failed; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
    }

    private static class CommandUsage {
        private final String shortcutName;
        private final String command;
        private final long timestamp;

        CommandUsage(String shortcutName, String command, long timestamp) {
            this.shortcutName = shortcutName;
            this.command = command;
            this.timestamp = timestamp;
        }

        String getShortcutName() { return shortcutName; }
        String getCommand() { return command; }
        long getTimestamp() { return timestamp; }
    }

    private static class ShortcutEventData implements SkillEventData {
        private final String userId;
        private final String shortcutName;
        private final String action;

        ShortcutEventData(String userId, String shortcutName, String action) {
            this.userId = userId;
            this.shortcutName = shortcutName;
            this.action = action;
        }

        @Override
        public String getEventType() {
            return "shortcut." + action;
        }

        @Override
        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        @Override
        public <T> T getData(String key, Class<T> type) {
            switch (key) {
                case "userId": return type.cast(userId);
                case "shortcutName": return type.cast(shortcutName);
                case "action": return type.cast(action);
                default: return null;
            }
        }

        @Override
        public Map<String, Object> getAllData() {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("shortcutName", shortcutName);
            data.put("action", action);
            return data;
        }
    }
}
