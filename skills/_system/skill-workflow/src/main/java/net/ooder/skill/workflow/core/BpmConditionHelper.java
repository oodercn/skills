package net.ooder.skill.workflow.core;

import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;

public class BpmConditionHelper {

    public static BPMCondition buildActivityCondition(RightConditionEnums conditionEnum,
                                                       String processDefId,
                                                       Long startTime,
                                                       Long endTime,
                                                       String titleKeyword) {
        BPMCondition condition;
        switch (conditionEnum) {
            case CONDITION_WAITEDWORK:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.EQUALS, "notStarted");
                break;
            case CONDITION_CURRENTWORK_NOTSTART:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_RUNSTATUS,
                    Operator.EQUALS, "PROCESSNOTSTARTED");
                break;
            case CONDITION_CURRENTWORK:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.EQUALS, "running");
                break;
            case CONDITION_READ:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.EQUALS, "READ");
                break;
            case CONDITION_COMPLETEDWORK:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.EQUALS, "completed");
                break;
            case CONDITION_ENDREAD:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.EQUALS, "ENDREAD");
                break;
            default:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                    Operator.NOT_EQUAL, "aborted");
                break;
        }

        if (processDefId != null && !processDefId.isEmpty()) {
            BPMCondition c = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSDEF_ID,
                Operator.EQUALS, processDefId);
            condition.addCondition(c, JoinOperator.JOIN_AND);
        }

        if (startTime != null && startTime > 0) {
            BPMCondition c = new BPMCondition(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME,
                Operator.GREATER_THAN, startTime);
            condition.addCondition(c, JoinOperator.JOIN_AND);
        }

        if (endTime != null && endTime > 0) {
            BPMCondition c = new BPMCondition(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME,
                Operator.LESS_THAN, endTime);
            condition.addCondition(c, JoinOperator.JOIN_AND);
        }

        if (titleKeyword != null && !titleKeyword.isEmpty()) {
            BPMCondition c = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSINST_ID,
                Operator.IN, "select PROCESSINST_ID from BPM_PROCESSINSTANCE where " +
                    BPMConditionKey.PROCESSINST_NAME + " like '%" + titleKeyword + "%'");
            condition.addCondition(c, JoinOperator.JOIN_AND);
        }

        condition.addOrderBy(new Order(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME, false));
        return condition;
    }

    public static BPMCondition buildProcessCondition(String state) {
        return new BPMCondition(BPMConditionKey.PROCESSINST_STATE, Operator.EQUALS, state);
    }

    public static BPMCondition buildPublishedProcessDefCondition() {
        return new BPMCondition(BPMConditionKey.PROCESSDEF_VERSION_PUBLICATIONSTATUS,
            Operator.EQUALS, "RELEASED");
    }
}
