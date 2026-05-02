package net.ooder.bpm.event.test;

import java.util.*;

public enum BpmSkillFlowState {
    DISCOVERED("discovered"),
    DEFINING("defining"),
    DEFINED("defined"),
    DEPLOYING("deploying"),
    DEPLOYED("deployed"),
    ACTIVATING("activating"),
    ACTIVATED("activated"),
    RUNNING("running"),
    PAUSING("pausing"),
    PAUSED("paused"),
    RESUMING("resuming"),
    RESUMED("resumed"),
    COMPLETING("completing"),
    COMPLETED("completed"),
    ABORTING("aborting"),
    ABORTED("aborted"),
    FAILING("failing"),
    FAILED("failed"),
    ESCALATING("escalating"),
    DELEGATING("delegating"),
    RETRYING("retrying"),
    UNDEPLOYING("undeploying"),
    UNDEPLOYED("undeployed"),
    ERROR("error");

    private final String code;

    BpmSkillFlowState(String code) {
        this.code = code;
    }

    public String getCode() { return code; }

    private static final Map<BpmSkillFlowState, Set<BpmSkillFlowState>> TRANSITIONS = new EnumMap<>(BpmSkillFlowState.class);

    static {
        TRANSITIONS.put(DISCOVERED, Set.of(DEFINING, UNDEPLOYED));
        TRANSITIONS.put(DEFINING, Set.of(DEFINED, ERROR));
        TRANSITIONS.put(DEFINED, Set.of(DEPLOYING, UNDEPLOYED));
        TRANSITIONS.put(DEPLOYING, Set.of(DEPLOYED, ERROR));
        TRANSITIONS.put(DEPLOYED, Set.of(ACTIVATING, UNDEPLOYING));
        TRANSITIONS.put(ACTIVATING, Set.of(ACTIVATED, ERROR));
        TRANSITIONS.put(ACTIVATED, Set.of(RUNNING, PAUSING, UNDEPLOYING));
        TRANSITIONS.put(RUNNING, Set.of(PAUSING, COMPLETING, FAILING, ABORTING, DELEGATING, ESCALATING));
        TRANSITIONS.put(PAUSING, Set.of(PAUSED, ERROR));
        TRANSITIONS.put(PAUSED, Set.of(RESUMING, ABORTING, UNDEPLOYING));
        TRANSITIONS.put(RESUMING, Set.of(RESUMED, ERROR));
        TRANSITIONS.put(RESUMED, Set.of(RUNNING, PAUSING));
        TRANSITIONS.put(COMPLETING, Set.of(COMPLETED, ERROR));
        TRANSITIONS.put(COMPLETED, Set.of(UNDEPLOYING));
        TRANSITIONS.put(ABORTING, Set.of(ABORTED, ERROR));
        TRANSITIONS.put(ABORTED, Set.of(UNDEPLOYING));
        TRANSITIONS.put(FAILING, Set.of(FAILED, ERROR));
        TRANSITIONS.put(FAILED, Set.of(RETRYING, ESCALATING, ABORTING));
        TRANSITIONS.put(ESCALATING, Set.of(RUNNING, FAILED));
        TRANSITIONS.put(DELEGATING, Set.of(RUNNING, FAILED));
        TRANSITIONS.put(RETRYING, Set.of(RUNNING, FAILED));
        TRANSITIONS.put(UNDEPLOYING, Set.of(UNDEPLOYED, ERROR));
        TRANSITIONS.put(UNDEPLOYED, Set.of(DISCOVERED));
        TRANSITIONS.put(ERROR, Set.of(DISCOVERED, DEPLOYED, ACTIVATED, UNDEPLOYED));
    }

    public boolean canTransitionTo(BpmSkillFlowState target) {
        Set<BpmSkillFlowState> allowed = TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }

    public static List<BpmSkillFlowState> validLifecyclePath() {
        return List.of(DISCOVERED, DEFINING, DEFINED, DEPLOYING, DEPLOYED,
            ACTIVATING, ACTIVATED, RUNNING, COMPLETING, COMPLETED);
    }

    public static List<BpmSkillFlowState> pauseResumePath() {
        return List.of(DISCOVERED, DEFINING, DEFINED, DEPLOYING, DEPLOYED,
            ACTIVATING, ACTIVATED, RUNNING, PAUSING, PAUSED,
            RESUMING, RESUMED, RUNNING, COMPLETING, COMPLETED);
    }

    public static List<BpmSkillFlowState> errorRecoveryPath() {
        return List.of(DISCOVERED, DEFINING, DEFINED, DEPLOYING, DEPLOYED,
            ACTIVATING, ACTIVATED, RUNNING, FAILING, FAILED,
            RETRYING, RUNNING, COMPLETING, COMPLETED);
    }
}
