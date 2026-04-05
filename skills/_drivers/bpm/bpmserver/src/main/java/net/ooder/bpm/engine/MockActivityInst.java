package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.ReturnType;
import net.ooder.org.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockActivityInst implements ActivityInst {

    private String activityInstId;
    private String activityDefId;
    private String processInstId;
    private String processDefId;
    private String processDefVersionId;
    private String name;
    private ActivityInstStatus state;
    private String urgency;
    private Date arrivedTime;
    private Date limitTime;
    private Date alertTime;
    private Date startTime;
    private ActivityInstReceiveMethod receiveMethod;
    private ActivityInstDealMethod dealMethod;
    private ActivityInstRunStatus runStatus;

    @Override
    public String getActivityInstId() {
        return activityInstId;
    }

    public void setActivityInstId(String activityInstId) {
        this.activityInstId = activityInstId;
    }

    @Override
    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

    @Override
    public String getActivityDefId() {
        return activityDefId;
    }

    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }

    @Override
    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    @Override
    public ProcessDef getProcessDef() throws BPMException {
        return null;
    }

    @Override
    public ActivityInstStatus getState() {
        return state != null ? state : ActivityInstStatus.running;
    }

    public void setState(ActivityInstStatus state) {
        this.state = state;
    }

    @Override
    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public Date getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(Date arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    @Override
    public Date getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
    }

    @Override
    public Date getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Date alertTime) {
        this.alertTime = alertTime;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public ActivityInstReceiveMethod getReceiveMethod() {
        return receiveMethod != null ? receiveMethod : ActivityInstReceiveMethod.SEND;
    }

    public void setReceiveMethod(ActivityInstReceiveMethod receiveMethod) {
        this.receiveMethod = receiveMethod;
    }

    @Override
    public ActivityInstDealMethod getDealMethod() {
        return dealMethod != null ? dealMethod : ActivityInstDealMethod.DEALMETHOD_NORMAL;
    }

    public void setDealMethod(ActivityInstDealMethod dealMethod) {
        this.dealMethod = dealMethod;
    }

    @Override
    public ActivityInstRunStatus getRunStatus() {
        return runStatus != null ? runStatus : ActivityInstRunStatus.NORMAL;
    }

    public void setRunStatus(ActivityInstRunStatus runStatus) {
        this.runStatus = runStatus;
    }

    @Override
    public CommonYesNoEnum getCanTakeBack() {
        return CommonYesNoEnum.NO;
    }

    @Override
    public DataMap getFormValues() throws BPMException {
        return null;
    }

    @Override
    public void updateFormValues(DataMap dataMap) throws BPMException {
    }

    @Override
    public Object getWorkflowAttribute(String name) {
        return null;
    }

    @Override
    public List<AttributeInst> loadAllAttribute() {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getRightAttribute(ActivityInstRightAtt group) {
        return new ArrayList<>();
    }

    @Override
    public Object getAppAttribute(String name) {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, String value) throws BPMException {
    }

    @Override
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
    }

    @Override
    public ProcessInst getProcessInst() throws BPMException {
        return null;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        return null;
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    public void setProcessDefVersionId(String processDefVersionId) {
        this.processDefVersionId = processDefVersionId;
    }

    @Override
    public ActivityDef getActivityDef() throws BPMException {
        return null;
    }

    @Override
    public List<RouteDef> getNextRoutes() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public Boolean isCanTakeBack() throws BPMException {
        return false;
    }

    @Override
    public Boolean isCanSpecialSend() throws BPMException {
        return false;
    }

    @Override
    public Boolean isCanEndRead() throws BPMException {
        return false;
    }

    @Override
    public ReturnType endRead() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public Boolean isCanPerform() throws BPMException {
        return true;
    }

    @Override
    public ReturnType takeBack() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public Boolean isCanRouteBack() throws BPMException {
        return false;
    }

    @Override
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ReturnType routeBack(String toActivityInstHistoryID) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public Boolean isCanSignReceive() throws BPMException {
        return false;
    }

    @Override
    public Boolean isCanCompleteProcessInst() throws BPMException {
        return false;
    }

    @Override
    public Boolean isCanReSend() throws BPMException {
        return false;
    }

    @Override
    public ReturnType signReceive() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType suspendActivityInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType resumeActivityInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst() throws BPMException {
        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
