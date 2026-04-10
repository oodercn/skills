package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * 场景配置DTO
 */
public class SceneConfigDTO {

    @JSONField(name = "sceneId")
    private String sceneId;

    @JSONField(name = "sceneName")
    private String sceneName;

    @JSONField(name = "sceneType")
    private String sceneType;

    @JSONField(name = "sceneCategory")
    private String sceneCategory;

    @JSONField(name = "triggerEvents")
    private List<String> triggerEvents;

    @JSONField(name = "entryConditions")
    private List<String> entryConditions;

    @JSONField(name = "exitConditions")
    private List<String> exitConditions;

    @JSONField(name = "sceneData")
    private Map<String, Object> sceneData;

    @JSONField(name = "sceneRules")
    private List<Map<String, Object>> sceneRules;

    @JSONField(name = "sceneState")
    private String sceneState;

    @JSONField(name = "priority")
    private Integer priority;

    @JSONField(name = "validityPeriod")
    private Map<String, String> validityPeriod;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getSceneCategory() {
        return sceneCategory;
    }

    public void setSceneCategory(String sceneCategory) {
        this.sceneCategory = sceneCategory;
    }

    public List<String> getTriggerEvents() {
        return triggerEvents;
    }

    public void setTriggerEvents(List<String> triggerEvents) {
        this.triggerEvents = triggerEvents;
    }

    public List<String> getEntryConditions() {
        return entryConditions;
    }

    public void setEntryConditions(List<String> entryConditions) {
        this.entryConditions = entryConditions;
    }

    public List<String> getExitConditions() {
        return exitConditions;
    }

    public void setExitConditions(List<String> exitConditions) {
        this.exitConditions = exitConditions;
    }

    public Map<String, Object> getSceneData() {
        return sceneData;
    }

    public void setSceneData(Map<String, Object> sceneData) {
        this.sceneData = sceneData;
    }

    public List<Map<String, Object>> getSceneRules() {
        return sceneRules;
    }

    public void setSceneRules(List<Map<String, Object>> sceneRules) {
        this.sceneRules = sceneRules;
    }

    public String getSceneState() {
        return sceneState;
    }

    public void setSceneState(String sceneState) {
        this.sceneState = sceneState;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Map<String, String> getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Map<String, String> validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}
