package net.ooder.bpm.engine.subflow.db;

public class ActRefPd implements java.io.Serializable {

    private String activitydefId;
    private String processtype;
    private String iswaitreturn;
    private String mainprocessVerId;
    private String parentprocessVerId;
    private String destprocessVerId;

    public ActRefPd() {
    }

    public String getParentprocessVerId() {
	return parentprocessVerId;
    }

    public void setParentprocessVerId(String parentprocessVerId) {
	this.parentprocessVerId = parentprocessVerId;
    }

    public ActRefPd(String activitydefId) {
	this.activitydefId = activitydefId;
    }

    public ActRefPd(String activitydefId, String destprocessId, String processtype, String iswaitreturn, String mainprocessId, String mainprocessVerId, String destprocessVerId, String parentprocessVerId) {
	this.activitydefId = activitydefId;
	this.processtype = processtype;
	this.iswaitreturn = iswaitreturn;
	this.mainprocessVerId = mainprocessVerId;
	this.destprocessVerId = destprocessVerId;
	this.parentprocessVerId = parentprocessVerId;

    }

    public String getActivitydefId() {
	return activitydefId;
    }

    public void setActivitydefId(String activitydefId) {
	this.activitydefId = activitydefId;
    }


    public String getProcesstype() {
	return processtype;
    }

    public void setProcesstype(String processtype) {
	this.processtype = processtype;
    }

    public String getIswaitreturn() {
	return iswaitreturn;
    }

    public void setIswaitreturn(String iswaitreturn) {
	this.iswaitreturn = iswaitreturn;
    }

    public String getMainprocessVerId() {
	return mainprocessVerId;
    }

    public void setMainprocessVerId(String mainprocessVerId) {
	this.mainprocessVerId = mainprocessVerId;
    }

    public String getDestprocessVerId() {
	return destprocessVerId;
    }

    public void setDestprocessVerId(String destprocessVerId) {
	this.destprocessVerId = destprocessVerId;
    }

}
