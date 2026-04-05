/**
 * $RCSfile: ActivityInstProxy.java,v $
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
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightPermission;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.Attributetype;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.*;


/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例客户端接口的代理实现
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
public class ActivityInstProxy implements ActivityInst, Serializable {
    @JSONField(serialize = false)

    private EIActivityInst eiActivityInst;
    private String systemCode;


    /**
     * @param eiActivityInst
     */
    public ActivityInstProxy(EIActivityInst eiActivityInst, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiActivityInst = eiActivityInst;
    }


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


    /*
     * @see com.ds.bpm.client.ActivityInst#getActivityDefUUID()
     */
    public String getActivityDefId() {
        return eiActivityInst.getActivityDefId();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getActivityInstId()
     */
    public String getActivityInstId() {
        return eiActivityInst.getActivityInstId();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getProcessInstId()
     */
    public String getProcessInstId() {
        return eiActivityInst.getProcessInstId();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getProcessDefId()
     */
    public String getProcessDefId() {
        return eiActivityInst.getProcessDefId();
    }

    @JSONField(serialize = false)

    public ProcessDef getProcessDef() throws BPMException {
        return new ProcessDefProxy(eiActivityInst.getProcessDef(), systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getState()
     */
    public ActivityInstStatus getState() {

        return ActivityInstStatus.fromType(eiActivityInst.getState());
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getUrgency()
     */
    public String getUrgency() {

        return eiActivityInst.getUrgency();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getArrivedTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getArrivedTime() {
        return eiActivityInst.getArrivedTime();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getLimitTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLimitTime() {
        return eiActivityInst.getLimitTime();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getAlertTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getAlertTime() {
        return eiActivityInst.getAlertTime();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getStartTime()
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return eiActivityInst.getStartTime();
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getRecieveState()
     */
    public ActivityInstReceiveMethod getReceiveMethod() {
        return ActivityInstReceiveMethod.fromType(eiActivityInst.getReceiveMethod());
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getDealState()
     */
    public ActivityInstDealMethod getDealMethod() {

        return ActivityInstDealMethod.fromType(eiActivityInst.getDealMethod());
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getRunState()
     */
    public ActivityInstRunStatus getRunStatus() {

        return ActivityInstRunStatus.fromType(eiActivityInst.getRunStatus());
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getCanTakeBack()
     */
    public CommonYesNoEnum getCanTakeBack() {

        return CommonYesNoEnum.fromType(eiActivityInst.getCanTakeBack());
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
     * @see com.ds.bpm.client.ActivityInst#getAllAttribute()
     */
    @JSONField(serialize = false)

    public List getAllAttribute() {
        List eiAttributeList = eiActivityInst.getAllAttribute();
        return new WorkflowListProxy(eiAttributeList, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getProcessInst()
     */
    @JSONField(serialize = false)

    public ProcessInst getProcessInst() throws BPMException {
        EIProcessInst eiProcessInst = eiActivityInst.getProcessInst();
        return new ProcessInstProxy(eiProcessInst, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getProcessDef()
     */
    @JSONField(serialize = false)

    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        EIProcessDefVersion eiProcessDef = eiActivityInst
                .getProcessDefVersion();
        return new ProcessDefVersionProxy(eiProcessDef, systemCode);
    }

    @Override
    public String getProcessDefVersionId() {
        String processDefVersionId = null;
        try {
            processDefVersionId = this.getProcessDefVersion().getProcessDefVersionId();
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return processDefVersionId;
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getActivityDef()
     */
    @JSONField(serialize = false)

    public ActivityDef getActivityDef() throws BPMException {
        EIActivityDef eiActivityDef = eiActivityInst.getActivityDef();
        return new ActivityDefProxy(eiActivityDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getWorkflowAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getWorkflowAttribute(String name) {
        return eiActivityInst
                .getAttributeInterpretedValue(Attributetype.ADVANCE + "."
                        + name);
    }

    @Override
    public List<AttributeInst> loadAllAttribute() {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        List<EIAttributeInst> eIAttributeInsts = eiActivityInst.getAllAttribute();
        for (EIAttributeInst eiInst : eIAttributeInsts) {
            if (eiInst != null) {
                AttributeInst inst = new AttributeInstProxy(eiInst, this.systemCode);
                attributeInsts.add(inst);
            }

        }
        return attributeInsts;
    }

    @JSONField(serialize = false)

    public List<Person> getRightAttribute(ActivityInstRightAtt group) {

        List<Person> persons = new ArrayList<Person>();
        try {
            persons = getClient().getActivityInstPersons(this.getActivityInstId(), group);
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return persons;
    }

//	/*
//	 * @see com.ds.bpm.client.ActivityInst#getRightAttribute(java.lang.String)
//	 */
//	@JSONField(serialize = false)
//	
//	public Object getRightAttribute(String name) {
//			return eiActivityInst.getAttributeInterpretedValue(Attributetype.RIGHT
//					+ "." + name);
//	}

    /*
     * @see com.ds.bpm.client.ActivityInst#getAttributeValue(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getAppAttribute(String name) {
        return eiActivityInst
                .getAttributeInterpretedValue(Attributetype.APPLICATION + "."
                        + name);
    }

    /*
     * @see com.ds.bpm.client.ActivityInst#setAttribute(java.lang.String,
     *      java.lang.String)
     */
    @JSONField(serialize = false)

    public void setAttribute(String name, String value) throws BPMException {
        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
                .createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());
        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);
        eiActivityInst.setAttribute(Attributetype.CUSTOMIZE.getType(), eiAttributeInst);

    }


    public void setPersonAttribute(String personId, String name, String value)
            throws BPMException {

        EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
                .createAttributeInst();
        eiAttributeInst.setId(UUID.randomUUID().toString());
        eiAttributeInst.setName(name);
        eiAttributeInst.setInterpretedValue(value);

        eiActivityInst.setAttribute(personId, eiAttributeInst);

    }

    /*
     * @see com.ds.bpm.client.ActivityInst#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getAttribute(String name) {
        return eiActivityInst.getAttributeValue(Attributetype.CUSTOMIZE + "."
                + name);
    }


    @Override

    @JSONField(serialize = false)
    public Boolean isCanRouteBack() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_ANY);
        return getClient().canRouteBack(this.getActivityInstId(), ctx);
    }

    @Override

    @JSONField(serialize = false)
    public Boolean isCanSignReceive() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_ANY);
        return getClient().canSignReceive(this.getActivityInstId(), ctx);
    }

    @Override
    public Boolean isCanCompleteProcessInst() throws BPMException {
        List<RouteDef> routes = this.getNextRoutes();
        for (RouteDef route : routes) {
            if (route.getToActivityDefId().equals("LAST")) {
                return true;
            }
        }
        return false;
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanReSend() throws BPMException {
        return this.getActivityDef().getCanReSend().equals(CommonYesNoEnum.YES);
    }

    @Override

    @JSONField(serialize = false)
    public Boolean isCanTakeBack() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_ANY);
        return getClient().canTakeBack(this.getActivityInstId(), ctx);
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanSpecialSend() throws BPMException {
        return this.getActivityDef().getCanSpecialSend().getType().equals(CommonYesNoEnum.YES) ? true : false;
    }

    @Override

    @JSONField(serialize = false)
    public Boolean isCanPerform() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_PERFORM);
        boolean isCanPerform = getClient().queryPermissionToActivityInst(this.getActivityInstId()
                , ctx);
        return isCanPerform;
    }

    @Override

    @JSONField(serialize = false)
    public Boolean isCanEndRead() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_READ);
        boolean isEndRead = getClient().canEndRead(this.getActivityInstId()
                , ctx);
        return isEndRead;
    }

    @JSONField(serialize = false)
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst() throws BPMException {
        return getClient().getActivityInstHistoryListByActvityInst(this.getActivityInstId(), null);
    }

    @JSONField(serialize = false)
    public List<RouteDef> getNextRoutes() throws BPMException {

        return getClient().getNextRoutes(this.getActivityInstId(), null, null, null);
    }

    @JSONField(serialize = false)
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList() throws BPMException {

        return getClient().getRouteBackActivityHistoryInstList(this.getActivityInstId(), null, null);
    }

    @JSONField(serialize = false)
    public ReturnType resumeActivityInst() throws BPMException {

        return getClient().resumeActivityInst(this.getActivityInstId(), null);
    }

    @JSONField(serialize = false)
    public ReturnType routeBack(String toActivityInstHistoryID) throws BPMException {
        return getClient().routeBack(null, toActivityInstHistoryID, null);
    }

    @JSONField(serialize = false)
    public ReturnType routeTo(List<String> nextActivityDefIDs) throws BPMException {
        return getClient().routeTo(this.getActivityInstId(), nextActivityDefIDs, null);
    }

    @JSONField(serialize = false)
    public ReturnType signReceive() throws BPMException {
        return getClient().signReceive(this.getActivityInstId(), null);
    }

    @JSONField(serialize = false)
    public ReturnType suspendActivityInst() throws BPMException {
        return getClient().suspendActivityInst(this.getActivityInstId(), null);
    }

    @JSONField(serialize = false)
    public ReturnType takeBack() throws BPMException {
        return getClient().takeBack(this.getActivityInstId(), null);
    }

    @JSONField(serialize = false)
    public ReturnType endRead() throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_READ.getType());
        ReturnType isEndRead = getClient().endRead(this.getActivityInstId()
                , ctx);
        return isEndRead;
    }


}
