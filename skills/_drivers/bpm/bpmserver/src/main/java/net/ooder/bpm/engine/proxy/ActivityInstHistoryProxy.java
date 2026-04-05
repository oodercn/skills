/**
 * $RCSfile: ActivityInstHistoryProxy.java,v $
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
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.ActivityDef;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.enums.activityinst.ActivityInstDealMethod;
import net.ooder.bpm.enums.activityinst.ActivityInstReceiveMethod;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryStatus;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例历史客户端接口的代理实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.1
 */
public class ActivityInstHistoryProxy implements ActivityInstHistory, Serializable {
    @JSONField(serialize = false)

    private EIActivityInstHistory eiActivityInstHistory;
    private String systemCode;

    @JSONField(serialize = false)

    private WorkflowClientService getClient() {
        JDSSessionFactory bpmSessionFactory = new JDSSessionFactory(null);
        WorkflowClientService client = null;
        try {
            JDSClientService jdsclient = bpmSessionFactory.getClientService(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());
            client = BPMServer.getInstance().getWorkflowService(jdsclient);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return client;
    }

    public ActivityInstHistoryProxy(EIActivityInstHistory eiActivityInst, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiActivityInstHistory = eiActivityInst;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getActivityHistoryId()
     */
    public String getActivityHistoryId() {
        return eiActivityInstHistory.getActivityHistoryId();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getProcessInstId()
     */
    public String getProcessInstId() {
        return eiActivityInstHistory.getProcessInstId();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getActivityDefId()
     */
    public String getActivityDefId() {
        return eiActivityInstHistory.getActivityDefId();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getActivityInstId()
     */
    public String getActivityInstId() {
        return eiActivityInstHistory.getActivityInstId();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getUrgencyType()
     */
    public String getUrgency() {
        return eiActivityInstHistory.getUrgency();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getArrivedTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getArrivedTime() {
        return eiActivityInstHistory.getArrivedTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getLimitTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLimitTime() {
        return eiActivityInstHistory.getLimitTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getStartTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return eiActivityInstHistory.getStartTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getEndTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return eiActivityInstHistory.getEndTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getReceiveStatus()
     */
    public ActivityInstReceiveMethod getReceiveMethod() {
        return ActivityInstReceiveMethod.fromType(eiActivityInstHistory.getReceiveMethod());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getDealMethod()
     */
    public ActivityInstDealMethod getDealMethod() {
        return ActivityInstDealMethod.fromType(eiActivityInstHistory.getDealMethod());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getRunStatus()
     */
    public ActivityInstHistoryStatus getRunStatus() {
        return ActivityInstHistoryStatus.fromType(eiActivityInstHistory.getRunStatus());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getProcessInst()
     */
    @JSONField(serialize = false)

    public ProcessInst getProcessInst() throws BPMException {
        if (eiActivityInstHistory == null) {
            return null;
        }
        EIProcessInst eiProcessInst = eiActivityInstHistory.getProcessInst();
        if (eiProcessInst == null) {
            return null;
        }
        return new ProcessInstProxy(eiProcessInst, systemCode);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getActivityInst()
     */
    @JSONField(serialize = false)

    public ActivityInst getActivityInst() throws BPMException {
        if (eiActivityInstHistory == null) {
            return null;
        }
        EIActivityInst eiActivityInst = eiActivityInstHistory.getActivityInst();
        if (eiActivityInst == null) {
            return null;
        }
        return new ActivityInstProxy(eiActivityInst, systemCode);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityInstHistory#getActivityDef()
     */
    @JSONField(serialize = false)

    public ActivityDef getActivityDef() throws BPMException {
        if (eiActivityInstHistory == null) {
            return null;
        }
        EIActivityDef eiActivityDef = eiActivityInstHistory.getActivityDef();
        if (eiActivityDef == null) {
            return null;
        }
        return new ActivityDefProxy(eiActivityDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getWorkflowAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getWorkflowAttribute(String name) {
        return eiActivityInstHistory.getAttributeInterpretedValue(Attributetype.ADVANCE + "." + name).toString();
    }


    /*
     * @see com.ds.bpm.client.ActivityInst#getRightAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public List<Person> getRightAttribute(ActivityInstHistoryAtt name) {
        return (List<Person>) eiActivityInstHistory.getAttributeInterpretedValue(Attributetype.RIGHT + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getAttributeValue(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getAppAttribute(String name) {
        return eiActivityInstHistory.getAttributeInterpretedValue(Attributetype.APPLICATION + "." + name).toString();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#setAttribute(java.lang.String, java.lang.String)
     */

    public void setAttribute(String name, String value) throws BPMException {
        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance().createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());
        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);
        eiActivityInstHistory.setAttribute(Attributetype.CUSTOMIZE.getType(), eiAttributeInst);

    }

    /*
     * @see com.ds.bpm.client.ActivityInstHistory#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getAttribute(String name) {
        return eiActivityInstHistory.getAttributeValue(Attributetype.CUSTOMIZE + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessInst#setAppAttribute(java.lang.String, java.lang.String)
     */
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance().createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());

        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);

        eiActivityInstHistory.setAttribute(personId, eiAttributeInst);

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

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ProcessInst#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getPersonAttribute(String personId, String name) {
        return eiActivityInstHistory.getAttributeValue(personId + "." + name);
    }


}
