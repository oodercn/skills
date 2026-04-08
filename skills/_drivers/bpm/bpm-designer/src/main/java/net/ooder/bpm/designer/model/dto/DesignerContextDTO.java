package net.ooder.bpm.designer.model.dto;

import java.util.List;
import java.util.Map;

public class DesignerContextDTO {
    
    private String sessionId;
    private String userId;
    private String userName;
    private String userRole;
    
    private ProcessDefDTO currentProcess;
    private ActivityDefDTO currentActivity;
    private String selectedElementId;
    private String selectedElementType;
    
    private List<ProcessDefDTO> recentProcesses;
    private List<String> availableCapabilities;
    private Map<String, List<Map<String, String>>> enumOptions;
    
    private Map<String, Object> sceneContext;
    private String sceneGroupId;
    private String sceneType;
    
    private List<Map<String, String>> conversationHistory;
    private Map<String, Object> userPreferences;
    
    private Map<String, Object> ragContext;
    private List<KnowledgeReference> knowledgeReferences;
    
    private DesignerMode mode;
    private EditorState editorState;
    
    public static DesignerContextDTOBuilder create() {
        return new DesignerContextDTOBuilder();
    }
    
    public static class DesignerContextDTOBuilder {
        private DesignerContextDTO context = new DesignerContextDTO();
        
        public DesignerContextDTOBuilder sessionId(String sessionId) {
            context.setSessionId(sessionId);
            return this;
        }
        
        public DesignerContextDTOBuilder userId(String userId) {
            context.setUserId(userId);
            return this;
        }
        
        public DesignerContextDTOBuilder userName(String userName) {
            context.setUserName(userName);
            return this;
        }
        
        public DesignerContextDTOBuilder userRole(String userRole) {
            context.setUserRole(userRole);
            return this;
        }
        
        public DesignerContextDTOBuilder currentProcess(ProcessDefDTO currentProcess) {
            context.setCurrentProcess(currentProcess);
            return this;
        }
        
        public DesignerContextDTOBuilder currentActivity(ActivityDefDTO currentActivity) {
            context.setCurrentActivity(currentActivity);
            return this;
        }
        
        public DesignerContextDTOBuilder selectedElementId(String selectedElementId) {
            context.setSelectedElementId(selectedElementId);
            return this;
        }
        
        public DesignerContextDTOBuilder selectedElementType(String selectedElementType) {
            context.setSelectedElementType(selectedElementType);
            return this;
        }
        
        public DesignerContextDTOBuilder sceneGroupId(String sceneGroupId) {
            context.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public DesignerContextDTOBuilder sceneType(String sceneType) {
            context.setSceneType(sceneType);
            return this;
        }
        
        public DesignerContextDTOBuilder mode(DesignerMode mode) {
            context.setMode(mode);
            return this;
        }
        
        public DesignerContextDTOBuilder ragContext(Map<String, Object> ragContext) {
            context.setRagContext(ragContext);
            return this;
        }
        
        public DesignerContextDTOBuilder addKnowledgeReference(KnowledgeReference ref) {
            if (context.getKnowledgeReferences() == null) {
                context.setKnowledgeReferences(new java.util.ArrayList<>());
            }
            context.getKnowledgeReferences().add(ref);
            return this;
        }
        
        public DesignerContextDTO build() {
            return context;
        }
    }
    
    public DesignerContextDTO() {
        this.mode = DesignerMode.CREATE;
    }
    
    public static DesignerContextDTO create(String userId, String userName) {
        DesignerContextDTO context = new DesignerContextDTO();
        context.setUserId(userId);
        context.setUserName(userName);
        context.setSessionId(java.util.UUID.randomUUID().toString());
        return context;
    }
    
    public boolean hasCurrentProcess() {
        return currentProcess != null;
    }
    
    public boolean hasCurrentActivity() {
        return currentActivity != null;
    }
    
    public boolean hasSelectedElement() {
        return selectedElementId != null && !selectedElementId.isEmpty();
    }
    
    public boolean hasSceneContext() {
        return sceneContext != null && !sceneContext.isEmpty();
    }
    
    public boolean hasRagContext() {
        return ragContext != null && !ragContext.isEmpty();
    }
    
    public void addConversationMessage(String role, String content) {
        if (conversationHistory == null) {
            conversationHistory = new java.util.ArrayList<>();
        }
        Map<String, String> message = new java.util.HashMap<>();
        message.put("role", role);
        message.put("content", content);
        conversationHistory.add(message);
    }
    
    public enum DesignerMode {
        CREATE,
        EDIT,
        VIEW,
        NLP_ASSISTED
    }
    
    public static class EditorState {
        private double zoom;
        private double[] panOffset;
        private String[] expandedNodes;
        private String activeTab;
        private boolean hasUnsavedChanges;
        
        public double getZoom() { return zoom; }
        public void setZoom(double zoom) { this.zoom = zoom; }
        public double[] getPanOffset() { return panOffset; }
        public void setPanOffset(double[] panOffset) { this.panOffset = panOffset; }
        public String[] getExpandedNodes() { return expandedNodes; }
        public void setExpandedNodes(String[] expandedNodes) { this.expandedNodes = expandedNodes; }
        public String getActiveTab() { return activeTab; }
        public void setActiveTab(String activeTab) { this.activeTab = activeTab; }
        public boolean isHasUnsavedChanges() { return hasUnsavedChanges; }
        public void setHasUnsavedChanges(boolean hasUnsavedChanges) { this.hasUnsavedChanges = hasUnsavedChanges; }
    }
    
    public static class KnowledgeReference {
        private String docId;
        private String title;
        private String snippet;
        private double relevance;
        
        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSnippet() { return snippet; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        public double getRelevance() { return relevance; }
        public void setRelevance(double relevance) { this.relevance = relevance; }
    }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public ProcessDefDTO getCurrentProcess() { return currentProcess; }
    public void setCurrentProcess(ProcessDefDTO currentProcess) { this.currentProcess = currentProcess; }
    
    public ActivityDefDTO getCurrentActivity() { return currentActivity; }
    public void setCurrentActivity(ActivityDefDTO currentActivity) { this.currentActivity = currentActivity; }
    
    public String getSelectedElementId() { return selectedElementId; }
    public void setSelectedElementId(String selectedElementId) { this.selectedElementId = selectedElementId; }
    
    public String getSelectedElementType() { return selectedElementType; }
    public void setSelectedElementType(String selectedElementType) { this.selectedElementType = selectedElementType; }
    
    public List<ProcessDefDTO> getRecentProcesses() { return recentProcesses; }
    public void setRecentProcesses(List<ProcessDefDTO> recentProcesses) { this.recentProcesses = recentProcesses; }
    
    public List<String> getAvailableCapabilities() { return availableCapabilities; }
    public void setAvailableCapabilities(List<String> availableCapabilities) { this.availableCapabilities = availableCapabilities; }
    
    public Map<String, List<Map<String, String>>> getEnumOptions() { return enumOptions; }
    public void setEnumOptions(Map<String, List<Map<String, String>>> enumOptions) { this.enumOptions = enumOptions; }
    
    public Map<String, Object> getSceneContext() { return sceneContext; }
    public void setSceneContext(Map<String, Object> sceneContext) { this.sceneContext = sceneContext; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    
    public List<Map<String, String>> getConversationHistory() { return conversationHistory; }
    public void setConversationHistory(List<Map<String, String>> conversationHistory) { this.conversationHistory = conversationHistory; }
    
    public Map<String, Object> getUserPreferences() { return userPreferences; }
    public void setUserPreferences(Map<String, Object> userPreferences) { this.userPreferences = userPreferences; }
    
    public Map<String, Object> getRagContext() { return ragContext; }
    public void setRagContext(Map<String, Object> ragContext) { this.ragContext = ragContext; }
    
    public List<KnowledgeReference> getKnowledgeReferences() { return knowledgeReferences; }
    public void setKnowledgeReferences(List<KnowledgeReference> knowledgeReferences) { this.knowledgeReferences = knowledgeReferences; }
    
    public DesignerMode getMode() { return mode; }
    public void setMode(DesignerMode mode) { this.mode = mode; }
    
    public EditorState getEditorState() { return editorState; }
    public void setEditorState(EditorState editorState) { this.editorState = editorState; }
}
