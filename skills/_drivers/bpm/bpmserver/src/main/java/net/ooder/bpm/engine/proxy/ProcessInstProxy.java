/**
 * $RCSfile: ProcessInstProxy.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSActionContext;
import net.ooder.annotation.Attributetype;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例客户端接口的代理实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class ProcessInstProxy implements ProcessInst, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JSONField(serialize = false)

    private EIProcessInst eiProcessInst;
    private String systemCode;
    //private WorkflowClientService client;

    /**
     * @param eiProcessInst
     */
    public ProcessInstProxy(EIProcessInst eiProcessInst, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiProcessInst = eiProcessInst;
    }

    @JSONField(serialize = false)

    private WorkflowClientService getClient() {
        JDSSessionFactory bpmSessionFactory = new JDSSessionFactory(JDSActionContext.getActionContext());
        WorkflowClientService client = null;
        try {
            JDSClientService jdsclient = bpmSessionFactory.getClientService(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());
            client = BPMServer.getInstance().getWorkflowService(jdsclient);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return client;
    }


    /*
     * @see com.ds.bpm.client.ProcessInst#getProcessInstId()
     */
    public String getProcessInstId() {
        return eiProcessInst.getProcessInstId();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getProcessDefId()
     */
    public String getProcessDefId() {
        return eiProcessInst.getProcessDefId();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getProcessDefVersionId()
     */
    public String getProcessDefVersionId() {
        return eiProcessInst.getProcessDefVersionId();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getName()
     */
    public String getName() {
        return eiProcessInst.getName();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getUrgency()
     */
    public String getUrgency() {
        return eiProcessInst.getUrgency();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getState()
     */
    public ProcessInstStatus getState() {
        return ProcessInstStatus.fromType(eiProcessInst.getState());
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getCopyNumber()
     */
    public int getCopyNumber() {
        return eiProcessInst.getCopyNumber();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getStartTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return eiProcessInst.getStartTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getEndTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return eiProcessInst.getEndTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getTimeLimit()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLimitTime() {
        return eiProcessInst.getLimitTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getRunState()
     */
    public ProcessInstStatus getRunStatus() {
        return ProcessInstStatus.fromType(eiProcessInst.getRunStatus());
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getProcessDefVersion()
     */
    @JSONField(serialize = false)

    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiProcessInst
                .getProcessDefVersion();
        ProcessDefVersion processDefVersion = new ProcessDefVersionProxy(
                eiProcessDefVersion, systemCode);
        return processDefVersion;
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getProcessDef()
     */
    @JSONField(serialize = false)

    public ProcessDef getProcessDef() throws BPMException {
        EIProcessDef eiProcessDef = eiProcessInst.getProcessDef();
        return new ProcessDefProxy(eiProcessDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getActivityInstList()
     */
    @JSONField(serialize = false)

    public List<ActivityInst> getActivityInstList() throws BPMException {
        List<EIActivityInst> eiActivityInstList = eiProcessInst.getActivityInstList();
        return new WorkflowListProxy(eiActivityInstList, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getWorkflowAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getWorkflowAttribute(ProcessInstAtt name) {
        return eiProcessInst
                .getAttributeInterpretedValue(Attributetype.ADVANCE + "."
                        + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getRightAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getRightAttribute(ProcessInstAtt name) {
        return eiProcessInst.getAttributeInterpretedValue(Attributetype.RIGHT
                + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#getAppAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getAppAttribute(ProcessInstAtt name) {
        return eiProcessInst
                .getAttributeInterpretedValue(Attributetype.APPLICATION + "."
                        + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#setAppAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void setAttribute(String name, String value) throws BPMException {
        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
                .createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());

        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);
        eiProcessInst.setAttribute(Attributetype.CUSTOMIZE.getType(), eiAttributeInst);

    }


    /*
     * @see com.ds.bpm.client.ProcessInst#setAppAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
                .createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());

        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);

        eiProcessInst.setAttribute(personId, eiAttributeInst);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ProcessInst#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getPersonAttribute(String personId, String name) {
        return eiProcessInst.getAttributeValue(personId + "."
                + name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ProcessInst#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getAttribute(String name) {
        return eiProcessInst.getAttributeValue(Attributetype.CUSTOMIZE + "."
                + name);
    }

    @JSONField(serialize = false)

    public List<AttributeInst> getAllAttribute() {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        List<EIAttributeInst> eIAttributeInsts = eiProcessInst.getAllAttribute();
        for (EIAttributeInst eiInst : eIAttributeInsts) {
            if (eiInst != null) {
                AttributeInst inst = new AttributeInstProxy(eiInst, this.systemCode);
                attributeInsts.add(inst);
            }

        }
        return attributeInsts;
    }

    @JSONField(serialize = false)

    public ReturnType abortProcessInst() throws BPMException {

        return this.getClient().abortProcessInst(this.getProcessInstId(), null);
    }

    @JSONField(serialize = false)

    public ReturnType completeProcessInst() throws BPMException {
        return getClient().completeProcessInst(this.getProcessInstId(), null);
    }

    @JSONField(serialize = false)

    public ReturnType deleteProcessInst() throws BPMException {

        return getClient().deleteProcessInst(this.getProcessInstId(), null);
    }

    @Override
    @JSONField(serialize = false)
    public DataMap getFormValues() throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    @JSONField(serialize = false)
    public void updateFormValues(DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }


    @JSONField(serialize = false)

    public List<ActivityInstHistory> getActivityInstHistoryListByProcessInst() throws BPMException {
        List<ActivityInstHistory> histories = new ArrayList<ActivityInstHistory>();
        try {
            histories = getClient().getActivityInstHistoryListByProcessInst(this.getProcessInstId(), null).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return histories;

    }

    @JSONField(serialize = false)

    public ReturnType resumeProcessInst() throws BPMException {
        return getClient().resumeProcessInst(this.getProcessInstId(), null);
    }

    @JSONField(serialize = false)

    public ReturnType suspendProcessInst() throws BPMException {
        return getClient().suspendProcessInst(this.getProcessInstId(), null);
    }

    @JSONField(serialize = false)

    public ReturnType updateProcessInstName(String name) throws BPMException {
        return getClient().updateProcessInstName(this.getProcessInstId(), name);
    }

    @JSONField(serialize = false)

    public ReturnType updateProcessInstUrgency(String urgency) throws BPMException {

        return getClient().updateProcessInstUrgency(this.getProcessInstId(), urgency);
    }


}
