package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.common.ReturnType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockProcessInst implements ProcessInst {

    private String processInstId;
    private String processDefId;
    private String processDefVersionId;
    private String name;
    private String urgency;
    private ProcessInstStatus state;
    private int copyNumber;
    private Date startTime;
    private Date endTime;
    private Date limitTime;
    private ProcessInstStatus runStatus;

    @Override
    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public ProcessInstStatus getState() {
        return state != null ? state : ProcessInstStatus.running;
    }

    public void setState(ProcessInstStatus state) {
        this.state = state;
    }

    @Override
    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public Date getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
    }

    @Override
    public ProcessInstStatus getRunStatus() {
        return runStatus != null ? runStatus : ProcessInstStatus.running;
    }

    public void setRunStatus(ProcessInstStatus runStatus) {
        this.runStatus = runStatus;
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
    public List<ActivityInst> getActivityInstList() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public Object getWorkflowAttribute(ProcessInstAtt name) {
        return null;
    }

    @Override
    public Object getRightAttribute(ProcessInstAtt name) {
        return null;
    }

    @Override
    public Object getAppAttribute(ProcessInstAtt name) {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public List<AttributeInst> getAllAttribute() {
        return new ArrayList<>();
    }

    @Override
    public String getPersonAttribute(String personId, String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, String value) throws BPMException {
    }

    @Override
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
    }

    @Override
    public ReturnType updateProcessInstName(String name) throws BPMException {
        this.name = name;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType updateProcessInstUrgency(String urgency) throws BPMException {
        this.urgency = urgency;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType suspendProcessInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType resumeProcessInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByProcessInst() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ReturnType abortProcessInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType completeProcessInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType deleteProcessInst() throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public DataMap getFormValues() throws BPMException {
        return null;
    }

    @Override
    public void updateFormValues(DataMap dataMap) throws BPMException {
    }
}
