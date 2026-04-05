package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessDefAccess;

import java.util.ArrayList;
import java.util.List;

public class MockProcessDef implements ProcessDef {

    private String processDefId;
    private String name;
    private String description;
    private String classification;
    private String systemCode;
    private ProcessDefAccess accessLevel;

    @Override
    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
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
    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Override
    public ProcessDefAccess getAccessLevel() {
        return accessLevel != null ? accessLevel : ProcessDefAccess.Public;
    }

    public void setAccessLevel(ProcessDefAccess accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion(int version) throws BPMException {
        return null;
    }

    @Override
    public List<String> getAllProcessDefVersionIds() {
        return new ArrayList<>();
    }

    @Override
    public List<ProcessDefVersion> getAllProcessDefVersions() throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ProcessDefVersion getActiveProcessDefVersion() throws BPMException {
        return null;
    }
}
