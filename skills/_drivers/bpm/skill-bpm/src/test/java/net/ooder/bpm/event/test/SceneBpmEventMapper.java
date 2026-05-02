package net.ooder.bpm.event.test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SceneBpmEventMapper {

    private static final Map<String, String> SCENE_TO_BPM = new ConcurrentHashMap<>();
    private static final Map<String, String> BPM_TO_SCENE = new ConcurrentHashMap<>();

    static {
        SCENE_TO_BPM.put("workflow.registered", "STARTED");
        SCENE_TO_BPM.put("workflow.executed", "SAVING");
        SCENE_TO_BPM.put("workflow.completed", "COMPLETED");
        SCENE_TO_BPM.put("workflow.failed", "ABORTED");
        SCENE_TO_BPM.put("workflow.paused", "SUSPENDED");
        SCENE_TO_BPM.put("workflow.resumed", "RESUMED");
        SCENE_TO_BPM.put("workflow.cancelled", "ABORTED");
        SCENE_TO_BPM.put("skill.installed", "DEPLOYED");
        SCENE_TO_BPM.put("skill.started", "ACTIVATED");
        SCENE_TO_BPM.put("skill.stopped", "PAUSED");
        SCENE_TO_BPM.put("skill.uninstalled", "UNDEPLOYED");
        SCENE_TO_BPM.put("skill.execution.error", "FAILED");
        SCENE_TO_BPM.put("agent.registered", "DEPLOYED");
        SCENE_TO_BPM.put("agent.online", "ACTIVATED");
        SCENE_TO_BPM.put("agent.offline", "PAUSED");
        SCENE_TO_BPM.put("agent.status.changed", "RUNNING");
        SCENE_TO_BPM.put("scene.activated", "ACTIVATED");
        SCENE_TO_BPM.put("scene.deactivated", "PAUSED");

        BPM_TO_SCENE.put("STARTED", "workflow.registered");
        BPM_TO_SCENE.put("COMPLETED", "workflow.completed");
        BPM_TO_SCENE.put("ABORTED", "workflow.failed");
        BPM_TO_SCENE.put("SUSPENDED", "workflow.paused");
        BPM_TO_SCENE.put("RESUMED", "workflow.resumed");
        BPM_TO_SCENE.put("DEPLOYED", "skill.installed");
        BPM_TO_SCENE.put("ACTIVATED", "skill.started");
        BPM_TO_SCENE.put("PAUSED", "skill.stopped");
        BPM_TO_SCENE.put("UNDEPLOYED", "skill.uninstalled");
        BPM_TO_SCENE.put("FAILED", "skill.execution.error");
        BPM_TO_SCENE.put("RUNNING", "agent.status.changed");
    }

    public static String sceneToBpmState(String sceneEventCode) {
        return SCENE_TO_BPM.getOrDefault(sceneEventCode, "UNKNOWN");
    }

    public static String bpmToSceneEvent(String bpmState) {
        return BPM_TO_SCENE.getOrDefault(bpmState, "unknown.event");
    }

    public static BpmTestEvent bridgeSceneEvent(String source, String sceneEventCode, Map<String, String> scenePayload) {
        String bpmState = sceneToBpmState(sceneEventCode);
        BpmTestEvent event = BpmTestEvent.sceneBridgeEvent(source, sceneEventCode, scenePayload);
        event.getMetadata().put("mappedBpmState", bpmState);
        return event;
    }

    public static BpmTestEvent bridgeBpmEvent(String source, String bpmState, Map<String, String> bpmPayload) {
        String sceneCode = bpmToSceneEvent(bpmState);
        BpmTestEvent event = BpmTestEvent.sceneBridgeEvent(source, sceneCode, bpmPayload);
        event.getMetadata().put("mappedBpmState", bpmState);
        return event;
    }

    public static boolean isWorkflowEvent(String sceneEventCode) {
        return sceneEventCode != null && sceneEventCode.startsWith("workflow.");
    }

    public static boolean isSkillEvent(String sceneEventCode) {
        return sceneEventCode != null && sceneEventCode.startsWith("skill.");
    }

    public static boolean isAgentEvent(String sceneEventCode) {
        return sceneEventCode != null && sceneEventCode.startsWith("agent.");
    }

    public static Set<String> getAllSceneEventCodes() {
        return Collections.unmodifiableSet(SCENE_TO_BPM.keySet());
    }

    public static Set<String> getAllBpmStates() {
        return Collections.unmodifiableSet(BPM_TO_SCENE.keySet());
    }
}
