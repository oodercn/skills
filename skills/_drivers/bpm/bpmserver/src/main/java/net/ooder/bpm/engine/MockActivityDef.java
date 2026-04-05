package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.*;
import net.ooder.annotation.Attributetype;
import net.ooder.annotation.DurationUnit;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.config.ActivityDefImpl;

import java.util.ArrayList;
import java.util.List;

public class MockActivityDef implements ActivityDef {

    private String activityDefId;
    private String processDefId;
    private String processDefVersionId;
    private String name;
    private String description;
    private ActivityDefPosition position;
    private ActivityDefImpl implementation;
    private String execClass;
    private CommonYesNoEnum iswaitreturn;
    private String subFlowId;
    private int limit;
    private int alertTime;
    private DurationUnit durationUnit;
    private ActivityDefDeadLineOperation deadlineOperation;
    private CommonYesNoEnum canRouteBack;
    private ActivityDefRouteBackMethod routeBackMethod;
    private CommonYesNoEnum canSpecialSend;
    private CommonYesNoEnum canReSend;
    private ActivityDefJoin join;
    private ActivityDefSplit split;

    @Override
    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    public void setProcessDefVersionId(String processDefVersionId) {
        this.processDefVersionId = processDefVersionId;
    }

    @Override
    public String getActivityDefId() {
        return activityDefId;
    }

    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ActivityDefPosition getPosition() {
        return position != null ? position : ActivityDefPosition.POSITION_NORMAL;
    }

    public void setPosition(ActivityDefPosition position) {
        this.position = position;
    }

    @Override
    public ActivityDefImpl getImplementation() {
        return implementation != null ? implementation : ActivityDefImpl.No;
    }

    public void setImplementation(ActivityDefImpl implementation) {
        this.implementation = implementation;
    }

    @Override
    public String getExecClass() {
        return execClass;
    }

    public void setExecClass(String execClass) {
        this.execClass = execClass;
    }

    @Override
    public CommonYesNoEnum getIswaitreturn() throws BPMException {
        return iswaitreturn != null ? iswaitreturn : CommonYesNoEnum.NO;
    }

    public void setIswaitreturn(CommonYesNoEnum iswaitreturn) {
        this.iswaitreturn = iswaitreturn;
    }

    @Override
    public ProcessDefVersion getSubFlow() throws BPMException {
        return null;
    }

    @Override
    public String getSubFlowId() {
        return subFlowId;
    }

    public void setSubFlowId(String subFlowId) {
        this.subFlowId = subFlowId;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(int alertTime) {
        this.alertTime = alertTime;
    }

    @Override
    public DurationUnit getDurationUnit() {
        return durationUnit != null ? durationUnit : DurationUnit.D;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    @Override
    public ActivityDefDeadLineOperation getDeadlineOperation() {
        return deadlineOperation != null ? deadlineOperation : ActivityDefDeadLineOperation.DEFAULT;
    }

    public void setDeadlineOperation(ActivityDefDeadLineOperation deadlineOperation) {
        this.deadlineOperation = deadlineOperation;
    }

    @Override
    public CommonYesNoEnum getCanRouteBack() {
        return canRouteBack != null ? canRouteBack : CommonYesNoEnum.YES;
    }

    public void setCanRouteBack(CommonYesNoEnum canRouteBack) {
        this.canRouteBack = canRouteBack;
    }

    @Override
    public ActivityDefRouteBackMethod getRouteBackMethod() {
        return routeBackMethod != null ? routeBackMethod : ActivityDefRouteBackMethod.LAST;
    }

    public void setRouteBackMethod(ActivityDefRouteBackMethod routeBackMethod) {
        this.routeBackMethod = routeBackMethod;
    }

    @Override
    public CommonYesNoEnum getCanSpecialSend() {
        return canSpecialSend != null ? canSpecialSend : CommonYesNoEnum.NO;
    }

    public void setCanSpecialSend(CommonYesNoEnum canSpecialSend) {
        this.canSpecialSend = canSpecialSend;
    }

    @Override
    public CommonYesNoEnum getCanReSend() {
        return canReSend != null ? canReSend : CommonYesNoEnum.NO;
    }

    public void setCanReSend(CommonYesNoEnum canReSend) {
        this.canReSend = canReSend;
    }

    @Override
    public ActivityDefJoin getJoin() {
        return join != null ? join : ActivityDefJoin.JOIN_AND;
    }

    public void setJoin(ActivityDefJoin join) {
        this.join = join;
    }

    @Override
    public ActivityDefSplit getSplit() {
        return split != null ? split : ActivityDefSplit.SPLIT_AND;
    }

    public void setSplit(ActivityDefSplit split) {
        this.split = split;
    }

    @Override
    public List<String> getOutRouteIds() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<String> getInRouteIds() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<AttributeDef> getAllAttribute() {
        return new ArrayList<>();
    }

    @Override
    public String getWorkflowAttribute(String name) {
        return null;
    }

    @Override
    public ActivityDefRight getRightAttribute() {
        return null;
    }

    @Override
    public Object getAppAttribute(String name) {
        return null;
    }

    @Override
    public Object getAttribute(Attributetype attributetype, String name) {
        return null;
    }

    @Override
    public List<Listener> getListeners() {
        return new ArrayList<>();
    }

    @Override
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        return null;
    }

    @Override
    public ProcessDef getProcessDef() throws BPMException {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }
}
