
package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ActivityDef;
import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.annotation.DurationUnit;
import net.ooder.annotation.Attributetype;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义版本客户端接口的代理实现
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
public class ProcessDefVersionProxy implements ProcessDefVersion, Serializable {

    @JSONField(serialize = false)
    private EIProcessDefVersion eiProcessDefVersion;
    private String systemCode;

    public ProcessDefVersionProxy(EIProcessDefVersion eiProcessDefVersion, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiProcessDefVersion = eiProcessDefVersion;
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
     * @see com.ds.bpm.client.ProcessDefVersion#getProcessDefId()
     */
    @Override
    public String getProcessDefId() {
        return eiProcessDefVersion.getProcessDefId();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getProcessDefVersionId()
     */
    @Override
    public String getProcessDefVersionId() {
        return eiProcessDefVersion.getProcessDefVersionId();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getVersion()
     */
    @Override
    public int getVersion() {
        return eiProcessDefVersion.getVersion();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getPublicationStatus()
     */
    @Override
    public ProcessDefVersionStatus getPublicationStatus() {
        return ProcessDefVersionStatus.fromType(eiProcessDefVersion.getPublicationStatus());
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getProcessDefName()
     */
    @Override
    public String getProcessDefName() {
        return eiProcessDefVersion.getProcessDefName();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getDescription()
     */
    @Override
    public String getDescription() {
        return eiProcessDefVersion.getDescription();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getClassification()
     */
    @Override
    public String getClassification() {
        return eiProcessDefVersion.getClassification();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getSystemCode()
     */
    @Override
    public String getSystemCode() {
        return eiProcessDefVersion.getSystemCode();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getAccessLevel()
     */
    @Override
    public ProcessDefAccess getAccessLevel() {
        return ProcessDefAccess.fromType(eiProcessDefVersion.getAccessLevel());
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getActiveTime()
     */
    @Override
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getActiveTime() {
        return eiProcessDefVersion.getActiveTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getFreezeTime()
     */
    @Override
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getFreezeTime() {
        return eiProcessDefVersion.getFreezeTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getCreatorId()
     */
    @Override
    public String getCreatorId() {
        return eiProcessDefVersion.getCreatorId();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getCreatorName()
     */
    @Override
    public String getCreatorName() {
        return eiProcessDefVersion.getCreatorName();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getCreated()
     */
    @Override
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreated() {
        return eiProcessDefVersion.getCreated();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getModifierId()
     */
    @Override
    public String getModifierId() {
        return eiProcessDefVersion.getModifierId();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getModifierName()
     */
    @Override
    public String getModifierName() {
        return eiProcessDefVersion.getModifierName();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getModifyTime()
     */
    @Override
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getModifyTime() {
        return eiProcessDefVersion.getModifyTime();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getLimit()
     */
    @Override
    public int getLimit() {
        return eiProcessDefVersion.getLimit();
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getDurationUnit()
     */
    @Override
    public DurationUnit getDurationUnit() {
        return DurationUnit.fromType(eiProcessDefVersion.getDurationUnit());
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getAllActivityDefs()
     */
    @Override
    @JSONField(serialize = false)
    public List<ActivityDef> getAllActivityDefs() {
        List<EIActivityDef> eiAllActivityDefs = eiProcessDefVersion.getAllActivityDefs();
        return new WorkflowListProxy(eiAllActivityDefs, systemCode);
    }

    /**
     * 取得当前版本中包含所有路由的对象
     *
     * @return 返回的List是只读
     */
    @Override
    @JSONField(serialize = false)
    public List getAllRouteDefs() throws BPMException {
        List eiAllRouteDefs = eiProcessDefVersion.getAllRouteDefs();
        return new WorkflowListProxy(eiAllRouteDefs, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getWorkflowAttribute(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    public Object getWorkflowAttribute(String name) {
        return eiProcessDefVersion.getAttributeInterpretedValue(Attributetype.ADVANCE + "." + name);
    }


    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getWorkflowAttribute(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    public Object getAttribute(Attributetype attributetype,String name) {
        return eiProcessDefVersion.getAttributeInterpretedValue(attributetype+ "." + name);
    }


    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getRightAttribute(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    public Object getRightAttribute(String name) {
        return eiProcessDefVersion.getAttributeInterpretedValue(Attributetype.RIGHT + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getAppAttribute(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    public Object getAppAttribute(String name) {
        return eiProcessDefVersion.getAttributeInterpretedValue(Attributetype.APPLICATION + "." + name);
    }

    /*
     * @see com.ds.bpm.client.ProcessDefVersion#getAllAttribute()
     */
    @Override
    @JSONField(serialize = false)
    public List getAllAttribute() {
        List eiAllAttribute = eiProcessDefVersion.getAllAttribute();
        return new WorkflowListProxy(eiAllAttribute, systemCode);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ProcessDefVersion#getDefDescription()
     */
    public String getDefDescription() {
        return eiProcessDefVersion.getDefDescription();
    }

    @Override
    @JSONField(serialize = false)
    public List<Listener> getListeners() {
        return new WorkflowListProxy(eiProcessDefVersion.getListeners(), systemCode);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.client.ProcessDefVersion#getAttribute(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    public String getAttribute(String name) {
        return eiProcessDefVersion.getAttributeValue(Attributetype.CUSTOMIZE + "." + name);
    }

    @Override
    public List<String> getActivityDefIds() {
        List<String> activityDefIds = new ArrayList<String>();
        List<EIActivityDef> eiAllActivityDefs = eiProcessDefVersion.getAllActivityDefs();
        for (EIActivityDef eiActivityDef : eiAllActivityDefs) {
            activityDefIds.add(eiActivityDef.getActivityDefId());
        }
        return activityDefIds;
    }

    @Override
    public List<String> getRouteDefIds() {
        List<String> routeDefIds = new ArrayList<String>();
        List<EIRouteDef> allRouteDefs = eiProcessDefVersion.getAllRouteDefs();
        for (EIRouteDef eiActivityDef : allRouteDefs) {
            routeDefIds.add(eiActivityDef.getRouteDefId());
        }
        return routeDefIds;
    }


    @Override
    @JSONField(serialize = false)
    public ProcessDefForm getFormDef() throws BPMException {

        return getClient().getMapDAODataEngine().getProcessDefForm(this.getProcessDefVersionId(), null);
    }

}