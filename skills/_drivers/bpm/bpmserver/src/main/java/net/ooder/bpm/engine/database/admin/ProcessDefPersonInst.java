package net.ooder.bpm.engine.database.admin;

public class ProcessDefPersonInst implements java.io.Serializable {

    private String uuid;
    private String processDefId;
    private String personId;
    private String rightCode;

    public ProcessDefPersonInst() {
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    public String getProcessDefId() {
	return processDefId;
    }

    public void setProcessDefId(String processDefId) {
	this.processDefId = processDefId;
    }

    public String getPersonId() {
	return personId;
    }

    public void setPersonId(String personId) {
	this.personId = personId;
    }

    public String getRightCode() {
	return rightCode;
    }

    public void setRightCode(String rightCode) {
	this.rightCode = rightCode;
    }

    public ProcessDefPersonInst(String uuid) {
	this.uuid = uuid;
    }

    public ProcessDefPersonInst(String uuid, String processDefId, String personId, String rightCode) {
	this.uuid = uuid;
	this.processDefId = processDefId;
	this.personId = personId;
	this.rightCode = rightCode;

    }

}
