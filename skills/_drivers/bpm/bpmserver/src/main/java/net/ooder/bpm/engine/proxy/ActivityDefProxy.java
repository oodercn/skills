
package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.subflow.ActRefPdClient;
import net.ooder.bpm.engine.subflow.ActRefPdClientImpl;
import net.ooder.bpm.engine.subflow.db.ActRefPd;
import net.ooder.bpm.enums.activitydef.*;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.config.ActivityDefImpl;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.DurationUnit;
import net.ooder.annotation.Attributetype;
import net.ooder.org.Org;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动定义客户端接口的代理实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
public class ActivityDefProxy implements ActivityDef, Serializable {
    @JSONField(serialize = false)

    private EIActivityDef eiActivityDef;

    private String systemCode;

    public ActivityDefProxy(EIActivityDef eiActivityDef, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiActivityDef = eiActivityDef;
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
     * @see com.ds.bpm.client.ActivityDef#getProcessDefId()
     */
    public String getProcessDefId() {
        return eiActivityDef.getProcessDefId();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getProcessDefVersionId()
     */
    public String getProcessDefVersionId() {
        return eiActivityDef.getProcessDefVersionId();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getActivityDefId()
     */
    public String getActivityDefId() {
        return eiActivityDef.getActivityDefId();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getName()
     */
    public String getName() {
        return eiActivityDef.getName();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getDescription()
     */
    public String getDescription() {
        return eiActivityDef.getDescription();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getPosition()
     */
    public ActivityDefPosition getPosition() {
        return ActivityDefPosition.fromType(eiActivityDef.getPosition());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getImplementation()
     */
    public ActivityDefImpl getImplementation() {

        return ActivityDefImpl.fromType(eiActivityDef.getImplementation());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getExecClass()
     */
    public String getExecClass() {
        return eiActivityDef.getExecClass();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getLimit()
     */
    public int getLimit() {
        return eiActivityDef.getLimit();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getAlertTime()
     */
    public int getAlertTime() {
        return eiActivityDef.getAlertTime();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getDurationUnit()
     */
    public DurationUnit getDurationUnit() {

        return DurationUnit.fromType(eiActivityDef.getDurationUnit());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getDeadlineOperation()
     */
    public ActivityDefDeadLineOperation getDeadlineOperation() {
        return ActivityDefDeadLineOperation.fromType(eiActivityDef.getDeadlineOperation());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getCanRouteBack()
     */
    public CommonYesNoEnum getCanRouteBack() {
        return CommonYesNoEnum.fromType(eiActivityDef.getCanRouteBack());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getRouteBackMethod()
     */
    public ActivityDefRouteBackMethod getRouteBackMethod() {
        return ActivityDefRouteBackMethod.fromType(eiActivityDef.getRouteBackMethod());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getCanSpecialSend()
     */
    public CommonYesNoEnum getCanSpecialSend() {
        return CommonYesNoEnum.fromType(eiActivityDef.getCanSpecialSend());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getJoin()
     */
    public ActivityDefJoin getJoin() {
        return ActivityDefJoin.fromType(eiActivityDef.getJoin());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getSplit()
     */

    public ActivityDefSplit getSplit() {
        return ActivityDefSplit.fromType(eiActivityDef.getSplit());
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getOutRouteIds()
     */
    public List<String> getOutRouteIds() throws BPMException {
        return eiActivityDef.getOutRouteIds();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getInRouteIds()
     */
    public List<String> getInRouteIds() throws BPMException {
        return eiActivityDef.getInRouteIds();
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getAllAttribute()
     */
    @JSONField(serialize = false)
    public List getAllAttribute() {
        List eiAttributeList = eiActivityDef.getAllAttribute();
        return new WorkflowListProxy(eiAttributeList, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getListeners()
     */
//    @JSONField(serialize = false)
//
    public List<Listener> getListeners() {
        List listeners = new ArrayList();
        List eiListenerList = eiActivityDef.getListeners();
        for (int k = 0; k < eiListenerList.size(); k++) {
            EIListener eiListener = (EIListener) eiListenerList.get(k);
            if (eiListener != null && eiListener.getExpressionListenerType() != null && !eiListener.getExpressionListenerType().equals("filter")) {
                listeners.add(eiListenerList.get(k));
            }
        }

        return new WorkflowListProxy(listeners, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getParentProcessDef()
     */
    @JSONField(serialize = false)
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = EIProcessDefVersionManager.getInstance().loadByKey(getProcessDefVersionId());
        return new ProcessDefVersionProxy(eiProcessDefVersion, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getProcessDef()
     */
    @JSONField(serialize = false)
    public ProcessDef getProcessDef() throws BPMException {
        EIProcessDef eiProcessDef = eiActivityDef.getProcessDef();
        return new ProcessDefProxy(eiProcessDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getWorkflowAttribute(java.lang.String)
     */
    @JSONField(serialize = false)
    public String getWorkflowAttribute(String name) {
        return eiActivityDef.getAttributeInterpretedValue(Attributetype.ADVANCE + "." + name).toString();
    }

    @JSONField(serialize = false)
    public ActivityDefRight getRightAttribute() {
        try {
            return this.getClient().getActivityDefRightAttribute(this.getActivityDefId());
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return null;

    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getRightAttribute(java.lang.String)
     */
    @JSONField(serialize = false)
    public Object getRightAttribute(String name) {
        return eiActivityDef.getAttributeInterpretedValue(Attributetype.RIGHT + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ActivityDef#getAppAttribute(java.lang.String)
     */
    @JSONField(serialize = false)
    @Override
    public Object getAppAttribute(String name) {
        return eiActivityDef.getAttributeInterpretedValue(Attributetype.APPLICATION + "." + name);
    }

    @Override
    @JSONField(serialize = false)
    public Object getAttribute(Attributetype attributetype, String name) {
        return eiActivityDef.getAttributeInterpretedValue(attributetype + "." + name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ActivityDef#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)
    public String getAttribute(String name) {
        return eiActivityDef.getAttributeValue(Attributetype.CUSTOMIZE + "." + name);
    }

    @JSONField(serialize = false)
    public ProcessDefVersion getSubFlow() throws BPMException {
        ProcessDefVersion processDefVersionProxy = null;
        if (this.getImplementation().equals(ActivityDefImpl.SubFlow.getType()) || this.getImplementation().equals(ActivityDefImpl.OutFlow)) {
            ActRefPdClient actRefPdClient = new ActRefPdClientImpl();
            if (actRefPdClient != null) {
                String subProcessDefVersionId = actRefPdClient.getSubProcessDefVersionId(this.getActivityDefId());
                EIProcessDefVersion eiProcessDefVersion = EIProcessDefVersionManager.getInstance().loadByKey(subProcessDefVersionId);
                processDefVersionProxy = new ProcessDefVersionProxy(eiProcessDefVersion, systemCode);
            }

        }
        return processDefVersionProxy;
    }

    @JSONField(serialize = false)
    public String getSubFlowId() {
        try {
            if (getSubFlow() != null) {
                return getSubFlow().getProcessDefId();
            }
        } catch (BPMException e) {
            return null;
        }
        return null;
    }

    @JSONField(serialize = false)
    public CommonYesNoEnum getIswaitreturn() {
        ActRefPdClient actRefPdClient = new ActRefPdClientImpl();
        ActRefPd eiIactRefPd = actRefPdClient.getActRefPdbyActivityId(this.getActivityDefId());
        if (eiIactRefPd != null) {
            return CommonYesNoEnum.fromType(eiIactRefPd.getIswaitreturn());
        } else {
            return CommonYesNoEnum.NO;
        }

    }

    @JSONField(serialize = false)
    public List<Org> getAllOrgPerForm(Map<RightCtx, Object> ctx) {
        return null;
    }

    public CommonYesNoEnum getCanReSend() {
        if (this.getImplementation().equals(ActivityDefImpl.No)) {
            Boolean isCan = this.getRightAttribute().isCanReSend();
            return isCan ? CommonYesNoEnum.YES : CommonYesNoEnum.NO;
        }
        return CommonYesNoEnum.NO;
    }

    ;

}
