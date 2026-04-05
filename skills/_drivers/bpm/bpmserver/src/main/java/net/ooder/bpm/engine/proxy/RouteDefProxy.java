/**
 * $RCSfile: RouteDefProxy.java,v $
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
import net.ooder.bpm.client.ActivityDef;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.RouteDef;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.route.RouteCondition;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由定义客户端接口的代理实现
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
public class RouteDefProxy implements RouteDef, Serializable {
    @JSONField(serialize = false)

    private EIRouteDef eiRouteDef;
    private String systemCode;

    /**
     * @param eiRouteDef
     */
    public RouteDefProxy(EIRouteDef eiRouteDef, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiRouteDef = eiRouteDef;
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getRouteDefId()
     */
    public String getRouteDefId() {
        return eiRouteDef.getRouteDefId();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getProcessDefId()
     */
    public String getProcessDefId() {
        return eiRouteDef.getProcessDefId();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getProcessDefVersionId()
     */
    public String getProcessDefVersionId() {
        return eiRouteDef.getProcessDefVersionId();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getName()
     */
    public String getName() {
        return eiRouteDef.getName();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getDescription()
     */
    public String getDescription() {
        return eiRouteDef.getDescription();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getFromActivityDefId()
     */
    public String getFromActivityDefId() {
        return eiRouteDef.getFromActivityDefId();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getToActivityDefId()
     */
    public String getToActivityDefId() {
        return eiRouteDef.getToActivityDefId();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getRouteOrder()
     */
    public int getRouteOrder() {
        return eiRouteDef.getRouteOrder();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getRouteDirection()
     */
    public RouteDirction getRouteDirection() {
        return RouteDirction.fromType(eiRouteDef.getRouteDirection());
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getRouteCondition()
     */
    public String getRouteCondition() {
        return eiRouteDef.getRouteCondition();
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getWorkflowAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getWorkflowAttribute(String name) {
        return eiRouteDef.getAttributeInterpretedValue(Attributetype.ADVANCE + "." + name);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getRightAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getRightAttribute(String name) {
        return eiRouteDef.getAttributeInterpretedValue(Attributetype.RIGHT + "." + name);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getAppAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public Object getAppAttribute(String name) {
        return eiRouteDef.getAttributeInterpretedValue(Attributetype.APPLICATION + "." + name);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getAllAttribute()
     */
    @JSONField(serialize = false)

    public List getAllAttribute() {
        List eiAllAttribute = eiRouteDef.getAllAttribute();
        return new WorkflowListProxy(eiAllAttribute, systemCode);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getListeners()
     */
    @JSONField(serialize = false)

    public List getListeners() {

        List listeners = new ArrayList();
        List eiListenerList = eiRouteDef.getListeners();
        for (int k = 0; k < eiListenerList.size(); k++) {
            EIListener eiListener = (EIListener) eiListenerList.get(k);
            if (!eiListener.getExpressionListenerType().equals("filter")) {
                listeners.add(eiListenerList.get(k));
            }
        }

        return new WorkflowListProxy(listeners, systemCode);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getFromActivityDef()
     */
    @JSONField(serialize = false)

    public ActivityDef getFromActivityDef() throws BPMException {
        EIActivityDef eiActivityDef = eiRouteDef.getFromActivityDef();
        return new ActivityDefProxy(eiActivityDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getToActivityDef()
     */
    @JSONField(serialize = false)

    public ActivityDef getToActivityDef() throws BPMException {
        EIActivityDef eiActivityDef = eiRouteDef.getToActivityDef();
        return new ActivityDefProxy(eiActivityDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getProcessDefVersion()
     */
    @JSONField(serialize = false)

    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiRouteDef.getProcessDefVersion();
        return new ProcessDefVersionProxy(eiProcessDefVersion, systemCode);
    }

    /*
     * @see com.ds.bpm.client.RouteDef#getProcessDef()
     */
    @JSONField(serialize = false)

    public ProcessDef getProcessDef() throws BPMException {
        EIProcessDef eiProcessDef = eiRouteDef.getProcessDef();
        return new ProcessDefProxy(eiProcessDef, systemCode);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.RouteDef#getAttribute(java.lang.String)
     */
    @JSONField(serialize = false)

    public String getAttribute(String name) {
        return eiRouteDef.getAttributeValue(Attributetype.CUSTOMIZE + "." + name);
    }

    @Override
    @JSONField(serialize = false)
    public Object getAttribute(Attributetype attributetype, String name) {
        return eiRouteDef.getAttributeValue(attributetype + "." + name);
    }

    @JSONField(serialize = false)

    public boolean isToEnd() {
        if (ActivityDefPosition.VIRTUAL_LAST_DEF.getType().equals(this.getToActivityDefId())) {
            return true;
        }
        return false;
    }

    @Override
    public RouteCondition getRouteConditionType() {
        return RouteCondition.fromType(eiRouteDef.getRouteConditionType());
    }
}
