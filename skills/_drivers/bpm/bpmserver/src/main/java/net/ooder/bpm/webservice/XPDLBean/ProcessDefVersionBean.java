package net.ooder.bpm.webservice.XPDLBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ooder.bpm.client.event.BPDEvent;
import net.ooder.bpm.engine.event.BPMEventControl;
import net.ooder.bpm.engine.proxy.ProcessDefVersionProxy;
import net.ooder.bpm.enums.event.BPDEventEnums;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.right.DbProcessDefRightGrp;
import net.ooder.bpm.engine.database.right.DbProcessDefRightGrpManager;
import net.ooder.bpm.engine.database.right.DbProcessDefSupervisor;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisor;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisorManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.inter.EIRouteDefManager;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.webservice.XMLParse;
import net.ooder.common.util.DateUtility;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: XPDL文件中工作流版本信息与数据库工作流版本信息的存取
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * <p>
 * Created on 2004-2-4
 * </p>
 *
 * @author Lizhy
 * @version 1.0
 */
public class ProcessDefVersionBean extends AbstractBean {

    // XPDL文件中工作流节点对象
    private Node wpNode;

    // 参数传入的流程版本ID
    private String wpVersionId;

    // 流程定义版本管理器接口
    private EIProcessDefVersionManager processDefVersionManager = EIProcessDefVersionManager.getInstance();

    // 获得工作流定义版本的基本属性表对象
    private EIProcessDefVersion processDefVersion;

    // 活动定义管理器接口
    private EIActivityDefManager activityDefManager = EIActivityDefManager.getInstance();

    // 活动定义管理器接口
    EIRouteDefManager routeDefManager = EIRouteDefManager.getInstance();

    // 日期格式化
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mainProcessDefVersionId;

    private boolean block;

    /**
     * XPDL流程Bean构造方法
     *
     * @param wpNode 工作流Dom Node对象
     */
    public ProcessDefVersionBean(Node wpNode, boolean block) {
        this.block = block;
        this.wpNode = wpNode;

    }

    /**
     * XPDL流程Bean构造方法
     *
     * @param wpNode 工作流Dom Node对象
     */
    public ProcessDefVersionBean(Node wpNode) {
        this(wpNode, false);

    }

    public boolean getBlock() {
        return block;
    }

    public ProcessDefVersionBean(String wpVersionId) {
        this(wpVersionId, false);
    }

    /**
     * XPDL流程Bean构造方法
     *
     * @param wpVersionId 工作流版本ID
     */
    public ProcessDefVersionBean(String wpVersionId, boolean block) {
        this.wpVersionId = wpVersionId;
        this.block = block;
    }

    /**
     * 获得工作流版本ID
     *
     * @return processId
     */
    public String getProcessDefVersionId() {
        String processDefVersionId = null;
        if (wpNode != null) {

            processDefVersionId = getExtendedAttributeElementValue(wpNode, "VersionId");
            if (processDefVersionId == null || processDefVersionId.equals("")) {
                processDefVersionId = XMLParse.getAttributeValue(wpNode, "Id");
            }
        }
        if (processDefVersionId == null || processDefVersionId.equals("")) {
            if (this.processDefVersion != null) {
                processDefVersionId = this.processDefVersion.getProcessDefVersionId();
            }
        }

        return processDefVersionId;
    }

    public String getMainProcessDefVersionId() {
        String processDefVersionId = null;
        String accessLevel = XMLParse.getAttributeValue(wpNode, "AccessLevel");

        if (wpNode != null) {
            // 主流程判断
            if (accessLevel != null && accessLevel.equals(ProcessDefAccess.Public.getType())) {
                processDefVersionId = getExtendedAttributeElementValue(wpNode, "VersionId");
            } else {
                processDefVersionId = XMLParse.getAttributeValue(wpNode, "MainProcessVersionId");
            }

        } else if (this.processDefVersion != null) {
            processDefVersionId = this.processDefVersion.getProcessDefVersionId();
        }

        return processDefVersionId;
    }

    /**
     * 获得工作流ID
     *
     * @return processId
     */
    public String getProcessDefId() {
        String processId = XMLParse.getAttributeValue(wpNode, "Id");
        return processId;
    }

    /**
     * 获得工作流版本号
     *
     * @return version
     */
    public int getVersion() {
        int version = 0;
        List rhNodeList = XMLParse.getChildNodesByName(wpNode, "RedefinableHeader");
        if (rhNodeList != null && rhNodeList.size() != 0) {
            Node rhNode = (Node) rhNodeList.get(0);
            List versionNodeList = XMLParse.getChildNodesByName(rhNode, "Version");
            if (versionNodeList != null && versionNodeList.size() != 0) {
                version = Integer.parseInt(XMLParse.getNodeValue((Node) versionNodeList.get(0)));
            }
        }
        return version;
    }

    /**
     * 获得工作流版本状态
     *
     * @return publicationStatus
     */
    public ProcessDefVersionStatus getPublicationStatus() {
        String publicationStatus = "";
        List rhNodeList = XMLParse.getChildNodesByName(wpNode, "RedefinableHeader");
        if (rhNodeList != null && rhNodeList.size() != 0) {
            publicationStatus = XMLParse.getAttributeValue((Node) rhNodeList.get(0), "PublicationStatus");
        }
        return ProcessDefVersionStatus.fromType(publicationStatus);
    }

    /**
     * 获得工作流版本描述
     *
     * @return description
     */
    public String getDescription() {
        String description = getExtendedAttributeElementValue(wpNode, "Description");
        return description;
    }

    /**
     * 获得工作流版本激活时间
     *
     * @return activeTime
     */
    public Date getActiveTime() {
        // Date activeTime =
        // DateUtility.constructTimestamp(DateUtility.getCurrentTime());
        Date activeTime = null;
        String aTime = getExtendedAttributeElementValue(wpNode, "ActiveTime");
        if (!aTime.equals("")) {
            activeTime = DateUtility.constructTimestamp(aTime);
        }
        return activeTime;
    }

    /**
     * 获得工作流版本冻结时间
     *
     * @return freezeTime
     */
    public Date getFreezeTime() {
        // Date freezeTime =
        // DateUtility.constructTimestamp(DateUtility.getCurrentTime());
        Date freezeTime = null;
        String fTime = getExtendedAttributeElementValue(wpNode, "FreezeTime");
        if (!fTime.equals("")) {
            freezeTime = DateUtility.constructTimestamp(fTime);
        }
        return freezeTime;
    }

    /**
     * 获得工作流版本创建人ID
     *
     * @return creatorid
     */
    public String getCreatorid() {
        String creatorid = "";
        List redNodeList = XMLParse.getChildNodesByName(wpNode, "RedefinableHeader");
        if (redNodeList != null && redNodeList.size() != 0) {
            Node redNode = (Node) redNodeList.get(0);
            List auNodeList = XMLParse.getChildNodesByName(redNode, "Author");
            if (auNodeList != null && auNodeList.size() != 0) {
                creatorid = XMLParse.getNodeValue((Node) auNodeList.get(0));
            }
        }
        if (creatorid == null || creatorid.equals("")) {
            creatorid = this.getModifierId();
        }

        return creatorid;
    }

    /**
     * 获得工作流版本创建人姓名
     *
     * @return creatorName
     */
    public String getCreatorName() {
        String creatorName = getExtendedAttributeElementValue(wpNode, "CreatorName");
        return creatorName;
    }

    /**
     * 获得工作流版本创建时间
     *
     * @return created
     */
    public Date getCreated() {
        Date created = DateUtility.constructTimestamp(DateUtility.getCurrentTime());
        List phNodeList = XMLParse.getChildNodesByName(wpNode, "ProcessHeader");
        if (phNodeList != null && phNodeList.size() != 0) {
            Node phNode = (Node) phNodeList.get(0);
            List creNodeList = XMLParse.getChildNodesByName(phNode, "Created");
            if (creNodeList != null && creNodeList.size() != 0) {
                if (!XMLParse.getNodeValue((Node) creNodeList.get(0)).equals("")) {
                    created = DateUtility.constructTimestamp(XMLParse.getNodeValue((Node) creNodeList.get(0)));
                }
            }
        }
        return created;
    }

    /**
     * 获得工作流版本修改人ID
     *
     * @return modifierId
     */
    public String getModifierId() {
        String modifierId = getExtendedAttributeElementValue(wpNode, "ModifierId");
        return modifierId;
    }

    /**
     * 获得工作流版本修改人姓名
     *
     * @return modifierName
     */
    public String getModifierName() {
        String modifierName = getExtendedAttributeElementValue(wpNode, "ModifierName");
        return modifierName;
    }

    /**
     * 获得工作流版本修改时间
     *
     * @return modifyTime
     */
    public Date getModifyTime() {
        // Date modifyTime =
        // DateUtility.constructTimestamp(DateUtility.getCurrentTime());
        Date modifyTime = null;
        String mTime = getExtendedAttributeElementValue(wpNode, "ModifyTime");
        if (!mTime.equals("")) {
            modifyTime = DateUtility.constructTimestamp(mTime);
        }
        return modifyTime;
    }

    /**
     * 获得工作流版本时间限制
     *
     * @return limit
     */
    public int getLimit() {
        int limit = 0;
        List phNodeList = XMLParse.getChildNodesByName(wpNode, "ProcessHeader");
        if (phNodeList != null && phNodeList.size() != 0) {
            Node phNode = (Node) phNodeList.get(0);
            List limitNodeList = XMLParse.getChildNodesByName(phNode, "Limit");
            if (limitNodeList != null && limitNodeList.size() != 0) {
                if (!XMLParse.getNodeValue((Node) limitNodeList.get(0)).equals("")) {
                    limit = Integer.parseInt(XMLParse.getNodeValue((Node) limitNodeList.get(0)));
                }
            }
        }
        return limit;
    }

    /**
     * 获得工作流版本时间限制单位
     *
     * @return durationUnit
     */
    public String getDurationUnit() {
        String durationUnit = "";
        List phNodeList = XMLParse.getChildNodesByName(wpNode, "ProcessHeader");
        if (phNodeList != null && phNodeList.size() != 0) {
            Node phNode = (Node) phNodeList.get(0);
            durationUnit = XMLParse.getAttributeValue(phNode, "DurationUnit");
        }
        return durationUnit;
    }

    /**
     * 向数据库中工作流定义版本表插入一条记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean insertDataToDB() throws Exception {
        try {
            // ==============插入流程基本信息===============
            ProcessDefBean processDefBean = null;

            processDefBean = new ProcessDefBean(wpNode, this.block);

            if (!processDefBean.processIsExist()) {
                if (!processDefBean.insertDataToDB()) {
                    return false;
                }
            } else {
                if (!processDefBean.updateDateFromDB()) {
                    return false;
                }
            }
            // ==============插入流程版本信息===============
            // 获得工作流定义版本的基本属性表对象

            processDefVersion = processDefVersionManager.createProcessDefVersion();

            Map eventContext = new HashMap();

            eventContext.put(RightCtx.USERID, this.getCreatorid());



            processDefVersion.setProcessDefId(this.getProcessDefId());
            // 如果是块活动则不进行处理

            processDefVersion.setProcessDefVersionId(this.getProcessDefVersionId());
            if (!this.block) {
                processDefVersion.setPublicationStatus(this.getPublicationStatus().getType());
                processDefVersion.setCreated(this.getCreated());
                processDefVersion.setCreatorId(this.getCreatorid());
                processDefVersion.setCreatorName(this.getCreatorName());
                processDefVersion.setVersion(this.getVersion());
                processDefVersion.setActiveTime(this.getActiveTime());
                processDefVersion.setDurationUnit(this.getDurationUnit());
                processDefVersion.setFreezeTime(this.getFreezeTime());
                processDefVersion.setLimit(this.getLimit());
                processDefVersion.setModifierId(this.getModifierId());
                processDefVersion.setModifierName(this.getModifierName());
                processDefVersion.setModifyTime(this.getModifyTime());
                processDefVersion.setDescription(this.getDescription());
                // ==============插入流程扩展属性信息==============
                // 清空流程某版本以前的扩展属性
                processDefVersion.clearAttribute();
                insertExtendedAttributesToDB();
                // ==============插入流程监听器信息==============
                insertListenersToDB();
            }
            // 如果是块活动的话则不插入
            // 保存流程
            processDefVersionManager.save(processDefVersion);
            if (!this.block) {
                // ==============插入流程版本自定义权限组==============
                if (!operateRightGroupsOnDB("Insert")) {
                    return false;
                }
                // ==============插入该流程监控人信息==============
                if (!operateSuperVisorOnDB("Insert")) {
                    return false;
                }
            }

            // =============插入流程的所有路由===============
            if (!operateRoutesOnDB("Insert")) {
                return false;
            }
            // ==============插入流程的所有活动===============
            if (!operateActivitiesOnDB("Insert")) {
                return false;
            }

            BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFCREATED, eventContext);

            BPMEventControl.getInstance().dispatchEvent(event);

        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /**
     * 修改数据库中工作流定义版本表的记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean updateDateFromDB() throws Exception {
        try {
            // ==============修改流程基本信息===============
            ProcessDefBean processDefBean = new ProcessDefBean(wpNode, block);
            if (!processDefBean.updateDateFromDB()) {
                return false;
            }

            // ==============修改流程版本信息===============
            // 获得工作流定义版本的基本属性表对象
            processDefVersion = processDefVersionManager.loadByKey(this.getProcessDefVersionId());
            processDefVersion.setProcessDefId(this.getProcessDefId());


            Map eventContext = new HashMap();

            eventContext.put(RightCtx.USERID, this.getCreatorid());




            if (!this.block) {
                processDefVersion.setPublicationStatus(this.getPublicationStatus().getType());
                processDefVersion.setCreated(this.getCreated());
                processDefVersion.setCreatorId(this.getCreatorid());
                processDefVersion.setCreatorName(this.getCreatorName());
                processDefVersion.setVersion(this.getVersion());
                processDefVersion.setActiveTime(this.getActiveTime());
                processDefVersion.setDurationUnit(this.getDurationUnit());
                processDefVersion.setFreezeTime(this.getFreezeTime());
                processDefVersion.setLimit(this.getLimit());
                processDefVersion.setModifierId(this.getModifierId());
                processDefVersion.setModifierName(this.getModifierName());
                processDefVersion.setModifyTime(this.getModifyTime());
                String desc = this.getDescription();
                if (desc == null || desc.equals("")) {
                    processDefVersion.setDescription(processDefBean.getDescription());
                }

                // ==============修改流程某版本扩展属性信息==============
                // 清空流程某版本以前的扩展属性
                processDefVersion.clearAttribute();
                // 插入流程某版本扩展属性信息
                insertExtendedAttributesToDB();
                // ==============修改流程某版本监听器信息==============

                insertListenersToDB();
                // 保存流程某版本
            }
            processDefVersionManager.save(processDefVersion);
            // ==============修改流程某版本自定义权限组==============

            if (!this.block) {
                if (!operateRightGroupsOnDB("Update")) {
                    return false;
                }
                // ==============修改流程监控人信息==============
                if (!operateSuperVisorOnDB("Update")) {
                    return false;
                }
            }
            // ==============修改流程的所有路由===============
            if (!operateRoutesOnDB("Update")) {
                return false;
            }
            // ==============修改流程的所有活动===============
            if (!operateActivitiesOnDB("Update")) {
                return false;
            }

            BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFUPDATE, eventContext);

            BPMEventControl.getInstance().dispatchEvent(event);



        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
        return true;
    }

    /**
     * 删除数据库中工作流定义版本表的记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean deleteDateFromDB() throws Exception {
        try {
            // 删除流程版本下属的所有自定义权限组信息
            if (!operateRightGroupsOnDB("Delete")) {
                return false;
            }
            // 删除流程版本下属的所有流程监控人信息
            if (!operateSuperVisorOnDB("Delete")) {
                return false;
            }
            // 删除流程版本下属的所有活动信息
            if (!operateActivitiesOnDB("Delete")) {
                return false;
            }
            // 删除流程版本下属的所有路由信息
            if (!operateRoutesOnDB("Delete")) {
                return false;
            }
            // 如果流程版本信息都被删除，那么将流程基本信息也删除
            processDefVersion = processDefVersionManager.loadByKey(this.wpVersionId);
            // 获得流程ID
            String processId = processDefVersion.getProcessDefId();
            List processDefVersionList = processDefVersionManager.loadByProcessdefId(processId);

            EIProcessDef processDef = EIProcessDefManager.getInstance().loadByKey(processId);
            // 删除活动块
            if (processDef.getAccessLevel().equals("Block")) {
                ProcessDefBean processDefBean = new ProcessDefBean(processId, true);
                processDefBean.deleteDateFromDB();
            }
            if (processDefVersionList.size() == 0) {
                ProcessDefBean processDefBean = new ProcessDefBean(processId, block);
                if (!processDefBean.deleteDateFromDB()) {
                    return false;
                }
            }
            // 删除流程版本信息
            processDefVersionManager.deleteByKey(this.wpVersionId);
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
        return true;
    }

    /**
     * 向数据库中流程监听器表插入流程监听器记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    protected void insertListenersToDB() throws Exception {
        try {
            List listenerList = getListenersList(wpNode);
            processDefVersion.setListeners(listenerList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 向数据库中流程扩展属性表插入记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    protected void insertExtendedAttributesToDB() throws Exception {
        try {
            // 存在XPSL文件中，但不属于流程扩展属性的元素

            List nonAttributeList = new ArrayList();
            nonAttributeList.add("Listeners");
            nonAttributeList.add("RightGroups");
            nonAttributeList.add("Classification");
            nonAttributeList.add("SystemCode");
            nonAttributeList.add("VersionId");
            nonAttributeList.add("Description");
            nonAttributeList.add("ActiveTime");
            nonAttributeList.add("FreezeTime");
            nonAttributeList.add("CreatorName");
            nonAttributeList.add("ModifierId");
            nonAttributeList.add("ModifierName");
            nonAttributeList.add("ModifyTime");
            // 获得扩张属性对象
            Map attributeMap = getExtendedAttributesList(wpNode, nonAttributeList);
            if (attributeMap != null) {
                Iterator attributeIter = attributeMap.entrySet().iterator();
                while (attributeIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) attributeIter.next();
                    EIAttributeDef attributeDef = (EIAttributeDef) entry.getValue();
                    // 如果扩展属性是级联的，那么将属性类型与其父属性作为父关键字；否则以属性类型为父关键字
                    String attributeName = (String) entry.getKey();
                    String parentAttributeName = attributeDef.getType();
                    int index = attributeName.lastIndexOf(".");
                    if (index != -1) {
                        parentAttributeName += "." + attributeName.substring(0, index);
                    }
                    processDefVersion.setAttribute(parentAttributeName, attributeDef);
                }
            }
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 对数据库中于本流程版本的流程监控人表进行操作
     *
     * @param operation 操作名称（增加、修改、删除）
     * @return boolean
     */
    private boolean operateSuperVisorOnDB(String operation) throws Exception {
        try {
            // 流程版本监控人定义管理器接口
            EIProcessDefSupervisorManager supervisorManager = EIProcessDefSupervisorManager.getInstance();
            if (operation.equals("Delete")) {
                // 删除本版本的所有流程监控人信息
                supervisorManager.deleteByProcessDefVersion(this.wpVersionId);
            } else {
                // 如果是添加和修改操作时，重新添加所有流程监控人信息
                // 先删除本版本的所有流程监控人信息
                supervisorManager.deleteByProcessDefVersion(this.getProcessDefVersionId());
                List rhNodeList = XMLParse.getChildNodesByName(wpNode, "RedefinableHeader");
                if (rhNodeList.size() != 0) {
                    Node rhNode = (Node) rhNodeList.get(0);
                    List respsNodeList = XMLParse.getChildNodesByName(rhNode, "Responsibles");
                    if (respsNodeList.size() != 0) {
                        Node respsNode = (Node) respsNodeList.get(0);
                        List respNodeList = XMLParse.getChildNodesByName(respsNode, "Responsible");
                        for (int i = 0; i < respNodeList.size(); i++) {
                            // 向流程监控人表插入一条记录
                            Node respNode = (Node) respNodeList.get(i);
                            EIProcessDefSupervisor processDefSupervisor = supervisorManager.createProcessDefSupervisor();
                            processDefSupervisor.setProcessDefId(this.getProcessDefId());
                            processDefSupervisor.setProcessDefVersionId(this.getProcessDefVersionId());
                            processDefSupervisor.setSupervisorId(XMLParse.getAttributeValue(respNode, "Value"));
                            processDefSupervisor.setSupervisorName(XMLParse.getAttributeValue(respNode, "Name"));
                            supervisorManager.save(processDefSupervisor);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
        return true;
    }

    /**
     * 对数据库中于本流程版本的自定义权限组进行操作
     *
     * @param operation 操作名称（增加、修改、删除）
     * @return boolean
     */
    private boolean operateRightGroupsOnDB(String operation) throws Exception {
        try {
            // 流程版本权限组定义管理器接口
            DbProcessDefRightGrpManager rightGrpManager = DbProcessDefRightGrpManager.getInstance();
            if (operation.equals("Delete")) {
                // 通过传入的流程版本ID，删除流程版本所有的权限
                rightGrpManager.deleteByProcessDefVersion(this.wpVersionId);
            } else {
                // 如果是添加和修改操作时，重新添加所有自定义权限组信息
                // 通过XPDL文件中的流程版本ID，先删除流程版本所有的权限
                rightGrpManager.deleteByProcessDefVersion(this.getProcessDefVersionId());
                // XPDL文件中的扩展属性集合
                List EAsNodeList = XMLParse.getChildNodesByName(wpNode, "ExtendedAttributes");
                Node EAsNode = (Node) EAsNodeList.get(0);
                List EANodeList = XMLParse.getChildNodesByName(EAsNode, "ExtendedAttribute");
                for (int i = 0; i < EANodeList.size(); i++) {
                    Node EANode = (Node) EANodeList.get(i);
                    String attributeName = XMLParse.getAttributeValue(EANode, "Name");
                    if (attributeName.equals("RightGroups")) {
                        if (EANode.hasChildNodes()) {
                            // 获得XPDL文件中RightGroup元素集合
                            List rightGrpsNodeList = XMLParse.getChildNodesByName(EANode, "itjds:RightGroups");
                            Node rightGrpsNode = (Node) rightGrpsNodeList.get(0);
                            List rightGrpNodeList = XMLParse.getChildNodesByName(rightGrpsNode, "itjds:RightGroup");
                            for (int j = 0; j < rightGrpNodeList.size(); j++) {
                                Node rightGrpNode = (Node) rightGrpNodeList.get(j);
                                // 设置RightGroup对象的字段值
                                String id = XMLParse.getAttributeValue(rightGrpNode, "Id");
                                String name = XMLParse.getAttributeValue(rightGrpNode, "Name");
                                String code = XMLParse.getAttributeValue(rightGrpNode, "Code");
                                String defaultGroup = XMLParse.getAttributeValue(rightGrpNode, "DefaultGroup");
                                String order = XMLParse.getAttributeValue(rightGrpNode, "Order");
                                DbProcessDefRightGrp rightGrp = rightGrpManager.createProcessDefRightGrp();
                                rightGrp.setRightGroupId(id);
                                rightGrp.setGroupName(name);
                                rightGrp.setGroupCode(code);
                                rightGrp.setDefaultRight(defaultGroup);
                                if (!order.equals("")) {
                                    rightGrp.setGroupOrder(Integer.parseInt(order));
                                } else {
                                    rightGrp.setGroupOrder(0);
                                }
                                rightGrp.setProcessDefId(this.getProcessDefId());
                                rightGrp.setProcessDefVersionId(this.getProcessDefVersionId());
                                rightGrpManager.save(rightGrp);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
        return true;
    }

    /**
     * 对数据库中于本流程版本的所有活动信息进行操作
     *
     * @param operation 操作名称（增加、修改、删除）
     * @return boolean
     */
    private boolean operateActivitiesOnDB(String operation) throws Exception {
        try {
            if (operation.equals("Delete")) {
                // 获得活动节点
                processDefVersion = processDefVersionManager.loadByKey(this.wpVersionId);
                List activityDefList = processDefVersion.getAllActivityDefs();
                if (activityDefList != null && activityDefList.size() != 0) {
                    for (int i = 0; i < activityDefList.size(); i++) {
                        EIActivityDef activityDef = (EIActivityDef) activityDefList.get(i);
                        // 实例化活动对象
                        ActivityDefBean activityDefBean = new ActivityDefBean(wpNode, activityDef.getActivityDefId());
                        if (!activityDefBean.deleteDateFromDB()) {
                            return false;
                        }
                    }
                }
            } else {
                // 先删除该流程版本的所有活动再添加
                activityDefManager.deleteByProcessDefVersionId(this.getProcessDefVersionId());
                // 获得活动节点
                Node actsNode = (Node) XMLParse.getChildNodesByName(wpNode, "Activities").get(0);
                List actNodeList = XMLParse.getChildNodesByName(actsNode, "Activity");
                if (actNodeList != null) {
                    for (int i = 0; i < actNodeList.size(); i++) {
                        Node actNode = (Node) actNodeList.get(i);
                        // 实例化活动对象
                        ActivityDefBean activityDefBean = new ActivityDefBean(wpNode, actNode, this);
                        // 插入一条活动的信息
                        if (!activityDefBean.insertDataToDB()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
    }

    /**
     * 对数据库中于本流程版本的所有路由信息进行操作
     *
     * @param operation 操作名称（增加、修改、删除）
     * @return boolean
     */
    private boolean operateRoutesOnDB(String operation) throws Exception {
        try {
            if (operation.equals("Delete")) {
                // 获得路由节点
                processDefVersion = processDefVersionManager.loadByKey(this.wpVersionId);
                List routeDefList = processDefVersion.getAllRouteDefs();
                if (routeDefList != null && routeDefList.size() != 0) {
                    for (int i = 0; i < routeDefList.size(); i++) {
                        EIRouteDef routeDef = (EIRouteDef) routeDefList.get(i);
                        // 实例化路由对象
                        RouteDefBean routeDefBean = new RouteDefBean(wpNode, routeDef.getRouteDefId());
                        // 删除一条路由的信息
                        if (!routeDefBean.deleteDateFromDB()) {
                            return false;
                        }
                    }
                }
            } else {
                // 先删除所有活动再重新添加
                routeDefManager.deleteByProcessDefVersionId(this.getProcessDefVersionId());
                // 获得路由节点
                Node routesNode = (Node) XMLParse.getChildNodesByName(wpNode, "Transitions").get(0);
                List routeNodeList = XMLParse.getChildNodesByName(routesNode, "Transition");
                if (routeNodeList != null) {
                    for (int i = 0; i < routeNodeList.size(); i++) {
                        Node routeNode = (Node) routeNodeList.get(i);
                        // 实例化路由对象
                        RouteDefBean routeDefBean = new RouteDefBean(wpNode, routeNode, this);
                        // 插入一条路由的信息
                        if (!routeDefBean.insertDataToDB()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            // return false;
        }
    }

    /**
     * 将流程信息转化为DOM中的Element对象
     *
     * @return wpElement
     */
    public Element appendBeanToXPDL() throws Exception {
        try {
            // 获得数据库中的工作流定义表对象

            processDefVersion = processDefVersionManager.loadByKey(this.wpVersionId);
            // =============创建一个WorkflowProcess工作流Document对象================
            Document doc = XMLParse.newXMLDocument();
            Element wpElement = null;
            if (this.block) {
                wpElement = doc.createElement("ActivitySet");
            } else {
                wpElement = doc.createElement("WorkflowProcess");
            }
            // 为WorkflowProcess元素添加属性

            wpElement.setAttributeNode(XMLParse.createAttributeForElement(wpElement, "Id", processDefVersion.getProcessDefId()));

            wpElement.setAttributeNode(XMLParse.createAttributeForElement(wpElement, "VersionId", processDefVersion.getProcessDefVersionId()));

            if (!this.block) {
                wpElement.setAttributeNode(XMLParse.createAttributeForElement(wpElement, "Name", processDefVersion.getProcessDefName()));
                wpElement.setAttributeNode(XMLParse.createAttributeForElement(wpElement, "AccessLevel", processDefVersion.getAccessLevel()));

                // =============为WorkflowProcess元素添加ProcessHeader子节点================
                appendProcessHeaderToWP(wpElement);
                // =============为WorkflowProcess元素添加RedefinableHeader子节点================
                appendRedefinableHeaderToWP(wpElement);
                // =============为WorkflowProcess元素添加Participants子节点================
                appendParticipantsToWP(wpElement);
                // =============为WorkflowProcess元素添加Activities子节点================
                // =============为WorkflowProcess元素添加ExtendedAttributes子节点================
                appendExtendedAttributesToWP(wpElement);
            }
            appendActivitiesToWP(wpElement);
            // =============为WorkflowProcess元素添加ActivitySets子节点================
            // appendActivitiesToWP(wpElement);
            // =============为WorkflowProcess元素添加Routes子节点================
            appendRoutesToWP(wpElement);

            return wpElement;
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
            // return null;
        }
    }

    /**
     * 为WorkflowProcess元素添加ProcessHeader子节点
     *
     * @param wpElement
     */
    private void appendProcessHeaderToWP(Element wpElement) throws Exception {
        try {
            Element phElement = XMLParse.createChildElement(wpElement, "ProcessHeader", "");
            phElement.setAttributeNode(XMLParse.createAttributeForElement(phElement, "DurationUnit", processDefVersion.getDurationUnit()));
            // 为ProcessHeader元素添加Created子节点
            String createdValue = "";
            if (processDefVersion.getCreated() != null) {
                createdValue = df.format(processDefVersion.getCreated());
            }
            Element createdElement = XMLParse.createChildElement(phElement, "Created", createdValue);
            phElement.appendChild((Node) createdElement);
            // 为ProcessHeader元素添加Description子节点
            Element desElement = XMLParse.createChildElement(phElement, "Description", processDefVersion.getDefDescription());
            phElement.appendChild((Node) desElement);
            // 为ProcessHeader元素添加Limit子节点
            Element limitElement = XMLParse.createChildElement(phElement, "Limit", String.valueOf(processDefVersion.getLimit()));
            phElement.appendChild((Node) limitElement);
            // 为WorkflowProcess元素添加ProcessHeader子节点
            wpElement.appendChild((Node) phElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为WorkflowProcess元素添加RedefinableHeader子节点
     *
     * @param wpElement
     */
    private void appendRedefinableHeaderToWP(Element wpElement) throws Exception {
        try {
            // 创建RedefinableHeader节点
            Element rhElement = XMLParse.createChildElement(wpElement, "RedefinableHeader", "");
            rhElement.setAttributeNode(XMLParse.createAttributeForElement(rhElement, "PublicationStatus", processDefVersion.getPublicationStatus()));
            // 为RedefinableHeader元素添加Author子节点
            Element authorElement = XMLParse.createChildElement(rhElement, "Author", processDefVersion.getCreatorId());
            rhElement.appendChild((Node) authorElement);
            // 为RedefinableHeader元素添加Version子节点
            Element versionElement = XMLParse.createChildElement(rhElement, "Version", String.valueOf(processDefVersion.getVersion()));
            rhElement.appendChild((Node) versionElement);
            // ====为RedefinableHeader元素添加Responsibles子节点====
            // 流程版本监控人定义管理器接口
            EIProcessDefSupervisorManager supervisorManager = EIProcessDefSupervisorManager.getInstance();
            List processDefSupervisorList = supervisorManager.getSupervisorByProcessDefVersionId(processDefVersion.getProcessDefVersionId());
            if (processDefSupervisorList.size() > 0) {
                Element responsiblesElement = XMLParse.createChildElement(rhElement, "Responsibles", "");
                // 为RedefinableHeader元素添加Responsible子节点
                for (int i = 0; i < processDefSupervisorList.size(); i++) {
                    // 获得流程版本监控人定义对象
                    DbProcessDefSupervisor processDefSupervisor = (DbProcessDefSupervisor) processDefSupervisorList.get(i);
                    Element responsibleElement = XMLParse.createChildElement(rhElement, "Responsible", "");
                    responsibleElement.setAttributeNode(XMLParse.createAttributeForElement(responsibleElement, "Name", processDefSupervisor.getSupervisorName()));
                    responsibleElement.setAttributeNode(XMLParse.createAttributeForElement(responsibleElement, "Value", processDefSupervisor.getSupervisorId()));
                    responsiblesElement.appendChild((Node) responsibleElement);
                }
                rhElement.appendChild((Node) responsiblesElement);
            }
            wpElement.appendChild((Node) rhElement);
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为WorkflowProcess元素添加Participants子节点
     *
     * @param wpElement
     */
    private void appendParticipantsToWP(Element wpElement) throws Exception {
        try {
            // 创建Participants节点
            Element participantsElement = XMLParse.createChildElement(wpElement, "Participants", "");
            // 创建Participant节点
            Element participantElement = XMLParse.createChildElement(participantsElement, "Participant", "");
            // 获得活动定义基本属性表对象集合
            List activityDefList = activityDefManager.loadByProcessDefVersionId(processDefVersion.getProcessDefVersionId());
            if (activityDefList != null && activityDefList.size() != 0) {
                EIActivityDef activityDef = (EIActivityDef) activityDefList.get(0);
                participantElement.setAttributeNode(XMLParse.createAttributeForElement(participantElement, "Id", activityDef.getAttributeValue("ParticipantID")));
                participantElement.setAttributeNode(XMLParse.createAttributeForElement(participantElement, "Name", ""));
            }
            // 创建ParticipantType节点
            Element ptElement = XMLParse.createChildElement(participantElement, "ParticipantType", "");
            ptElement.setAttributeNode(XMLParse.createAttributeForElement(ptElement, "Type", "RESOURCE_SET"));
            participantElement.appendChild((Node) ptElement);
            // 创建ParticipantType节点
            participantsElement.appendChild((Node) participantElement);
            wpElement.appendChild((Node) participantsElement);
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为WorkflowProcess元素添加活动Activities子节点
     *
     * @param wpElement
     */
    private void appendActivitiesToWP(Element wpElement) throws Exception {
        try {
            // 获得活动定义基本属性表对象集合
            List activityDefList = activityDefManager.loadByProcessDefVersionId(processDefVersion.getProcessDefVersionId());
            int activityCount = activityDefList.size();
            if (activityCount > 0) {
                // 创建Activities节点
                Element activitiesElement = XMLParse.createChildElement(wpElement, "Activities", "");
                for (int i = 0; i < activityCount; i++) {
                    EIActivityDef activityDef = (EIActivityDef) activityDefList.get(i);
                    ActivityDefBean activityDefBean = new ActivityDefBean(wpElement, activityDef.getActivityDefId());
                    Element activityNode = activityDefBean.appendBeanToXPDL();
                    activitiesElement.appendChild(activityNode);
                }
                wpElement.appendChild(activitiesElement);

            }
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * private void appendActivitySetsToWP(Element wpElement) throws Exception { try { // 获得活动定义基本属性表对象集合 List
     * activityDefList = activityDefManager .loadByProcessDefVersionId(processDefVersion .getProcessDefVersionId()); int
     * activityCount = activityDefList.size(); if (activityCount > 0) { // 创建Activities节点 Element activitiesElement =
     * XMLParse.createChildElement( wpElement, "ActivitySets", ""); for (int i = 0; i < activityCount; i++) {
     * EIActivityDef activityDef = (EIActivityDef) activityDefList .get(i); ActivityDefBean activityDefBean = new
     * ActivityDefBean( wpElement, activityDef.getActivityDefId()); Element activityNode =
     * activityDefBean.appendBeanToXPDL(); activitiesElement.appendChild(activityNode); }
     * wpElement.appendChild(activitiesElement); } } catch (BPMException e) { e.printStackTrace(); throw e; } }
     */

    /**
     * 为WorkflowProcess元素添加活动Routes子节点
     *
     * @param wpElement
     */
    private void appendRoutesToWP(Element wpElement) throws Exception {
        try {
            // 获得活动定义基本属性表对象集合
            List routeDefList = routeDefManager.loadByProcessDefVersionId(processDefVersion.getProcessDefVersionId());
            if (routeDefList != null && routeDefList.size() != 0) {
                // 创建Transitions节点
                Element routesElement = XMLParse.createChildElement(wpElement, "Transitions", "");
                for (int i = 0; i < routeDefList.size(); i++) {
                    EIRouteDef routeDef = (EIRouteDef) routeDefList.get(i);
                    if (!routeDef.getToActivityDefId().equals(ActivityDefPosition.VIRTUAL_LAST_DEF)) {
                        RouteDefBean routeDefBean = new RouteDefBean(wpElement, routeDef.getRouteDefId());
                        Element routeNode = routeDefBean.appendBeanToXPDL();
                        routesElement.appendChild(routeNode);
                    }
                }
                wpElement.appendChild(routesElement);
            }
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为WorkflowProcess元素添加活动ExtendedAttributes子节点
     *
     * @param wpElement
     */
    private void appendExtendedAttributesToWP(Element wpElement) throws Exception {
        try {
            Element EAsElement = XMLParse.createChildElement(wpElement, "ExtendedAttributes", "");
            List topActivityAttrList = processDefVersion.getTopAttribute();
            // ===============向扩展属性节点集合中添加扩展属性=============
            for (int i = 0; i < topActivityAttrList.size(); i++) {
                EIAttributeDef attributeDef = (EIAttributeDef) topActivityAttrList.get(i);
                appendOneExtendedAttribute(EAsElement, "", attributeDef);
            }
            // ==============将流程类型作为扩展属性添加===========
            Element cfEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            cfEAElement.setAttributeNode(XMLParse.createAttributeForElement(cfEAElement, "Type", ""));
            cfEAElement.setAttributeNode(XMLParse.createAttributeForElement(cfEAElement, "Name", "Classification"));
            cfEAElement.setAttributeNode(XMLParse.createAttributeForElement(cfEAElement, "Value", processDefVersion.getClassification()));
            EAsElement.appendChild((Node) cfEAElement);
            // ==========将流程所属系统标识作为扩展属性添加===========
            Element systemcodeEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            systemcodeEAElement.setAttributeNode(XMLParse.createAttributeForElement(systemcodeEAElement, "Type", ""));
            systemcodeEAElement.setAttributeNode(XMLParse.createAttributeForElement(systemcodeEAElement, "Name", "SystemCode"));
            systemcodeEAElement.setAttributeNode(XMLParse.createAttributeForElement(systemcodeEAElement, "Value", processDefVersion.getSystemCode()));
            EAsElement.appendChild((Node) systemcodeEAElement);
            // ===========将流程版本ID作为扩展属性添加==========
            Element versionidEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            versionidEAElement.setAttributeNode(XMLParse.createAttributeForElement(versionidEAElement, "Type", ""));
            versionidEAElement.setAttributeNode(XMLParse.createAttributeForElement(versionidEAElement, "Name", "VersionId"));
            versionidEAElement.setAttributeNode(XMLParse.createAttributeForElement(versionidEAElement, "Value", processDefVersion.getProcessDefVersionId()));
            EAsElement.appendChild((Node) versionidEAElement);
            // ==========将流程版本描述作为扩展属性添加==========
            Element descriptionEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            descriptionEAElement.setAttributeNode(XMLParse.createAttributeForElement(descriptionEAElement, "Type", ""));
            descriptionEAElement.setAttributeNode(XMLParse.createAttributeForElement(descriptionEAElement, "Name", "Description"));
            descriptionEAElement.setAttributeNode(XMLParse.createAttributeForElement(descriptionEAElement, "Value", processDefVersion.getDescription()));
            EAsElement.appendChild((Node) descriptionEAElement);
            // ==============将流程激活时间作为扩展属性添加===========
            Element activetimeEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            activetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(activetimeEAElement, "Type", ""));
            activetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(activetimeEAElement, "Name", "ActiveTime"));
            String activetimeValue = "";
            if (processDefVersion.getActiveTime() != null) {
                activetimeValue = df.format(processDefVersion.getActiveTime());
            }
            activetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(activetimeEAElement, "Value", activetimeValue));
            EAsElement.appendChild((Node) activetimeEAElement);
            // ==============将流程冻结时间作为扩展属性添加===========
            Element freezetimeEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            freezetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(freezetimeEAElement, "Type", ""));
            freezetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(freezetimeEAElement, "Name", "FreezeTime"));
            String freezetimeValue = "";
            if (processDefVersion.getFreezeTime() != null) {
                freezetimeValue = df.format(processDefVersion.getFreezeTime());
            }
            freezetimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(freezetimeEAElement, "Value", freezetimeValue));
            EAsElement.appendChild((Node) freezetimeEAElement);
            // ==============将流程创建人姓名作为扩展属性添加===========
            Element creatornameEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            creatornameEAElement.setAttributeNode(XMLParse.createAttributeForElement(creatornameEAElement, "Type", ""));
            creatornameEAElement.setAttributeNode(XMLParse.createAttributeForElement(creatornameEAElement, "Name", "CreatorName"));
            creatornameEAElement.setAttributeNode(XMLParse.createAttributeForElement(creatornameEAElement, "Value", processDefVersion.getCreatorName()));
            EAsElement.appendChild((Node) creatornameEAElement);
            // ==============将流程创建人姓名作为扩展属性添加===========
            Element modifieridEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            modifieridEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifieridEAElement, "Type", ""));
            modifieridEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifieridEAElement, "Name", "ModifierId"));
            modifieridEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifieridEAElement, "Value", processDefVersion.getModifierId()));
            EAsElement.appendChild((Node) modifieridEAElement);
            // ==============将流程修改人姓名作为扩展属性添加===========
            Element modifiernameEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            modifiernameEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifiernameEAElement, "Type", ""));
            modifiernameEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifiernameEAElement, "Name", "ModifierName"));
            modifiernameEAElement.setAttributeNode(XMLParse.createAttributeForElement(modifiernameEAElement, "Value", processDefVersion.getModifierName()));
            EAsElement.appendChild((Node) modifiernameEAElement);
            // ==============将流程修改时间作为扩展属性添加===========
            Element modifytimeAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            modifytimeAElement.setAttributeNode(XMLParse.createAttributeForElement(modifytimeAElement, "Type", ""));
            modifytimeAElement.setAttributeNode(XMLParse.createAttributeForElement(modifytimeAElement, "Name", "ModifyTime"));
            String modifytimeValue = "";
            if (processDefVersion.getModifyTime() != null) {
                modifytimeValue = df.format(processDefVersion.getModifyTime());
            }
            modifytimeAElement.setAttributeNode(XMLParse.createAttributeForElement(modifytimeAElement, "Value", modifytimeValue));
            EAsElement.appendChild((Node) modifytimeAElement);
            // =============为流程节点添加监听器===============
            appendListenersToWP(EAsElement);
            // ===========为流程节点添加自定义权限组===============
            appendRightGroupsToWP(EAsElement);
            // ===========为流程节点添加扩展属性集合的子节点============
            wpElement.appendChild((Node) EAsElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void appendOneExtendedAttribute(Element EAsElement, String parentName, EIAttributeDef eiAttributeDef) throws Exception {
        try {
            Element EAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
            String attName = parentName;
            if (!"".equals(attName)) {
                attName = attName + "." + eiAttributeDef.getName();
            } else {
                attName = eiAttributeDef.getName();
            }
            EAElement.setAttributeNode(XMLParse.createAttributeForElement(EAElement, "Type", eiAttributeDef.getType()));
            EAElement.setAttributeNode(XMLParse.createAttributeForElement(EAElement, "Name", attName));
            EAElement.setAttributeNode(XMLParse.createAttributeForElement(EAElement, "Value", eiAttributeDef.getValue()));
            EAsElement.appendChild((Node) EAElement);
            // 加入此属性节点的子属性！
            List children = eiAttributeDef.getChildren();
            for (int i = 0; i < children.size(); i++) {
                EIAttributeDef child = (EIAttributeDef) children.get(i);
                appendOneExtendedAttribute(EAsElement, attName, child);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为流程节点添加监听器
     *
     * @param EAsElement 扩张属性集合的节点对象
     */
    private void appendListenersToWP(Element EAsElement) throws Exception {
        try {
            // 获得数据库中流程的所有监听器
            List listenerList = processDefVersion.getListeners();
            int listenerCount = listenerList.size();
            if (listenerCount > 0) {
                // 向扩展属性节点集合中添加表示监听器的扩展属性
                Element listenersEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                listenersEAElement.setAttributeNode(XMLParse.createAttributeForElement(listenersEAElement, "Name", "Listeners"));
                listenersEAElement.setAttributeNode(XMLParse.createAttributeForElement(listenersEAElement, "Value", ""));
                // 监听器元素
                Element listenersElement = XMLParse.createChildElement(listenersEAElement, "itjds:Listeners", "");
                for (int j = 0; j < listenerCount; j++) {
                    EIListener listenerDef = (EIListener) listenerList.get(j);
                    Element listenerElement = XMLParse.createChildElement(listenersElement, "itjds:Listener", "");
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "Id", listenerDef.getListenerId()));
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "Name", listenerDef.getListenerName()));
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "ListenerEvent", listenerDef.getListenerEvent()));
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "RealizeClass", listenerDef.getRealizeClass()));

                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "expressionEventType", listenerDef.getExpressionEventType()));
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "expressionListenerType", listenerDef.getExpressionListenerType()));
                    listenerElement.setAttributeNode(XMLParse.createAttributeForElement(listenerElement, "expressionStr", listenerDef.getExpressionStr()));

                    listenersElement.appendChild((Node) listenerElement);
                }
                listenersEAElement.appendChild((Node) listenersElement);
                EAsElement.appendChild((Node) listenersEAElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为流程节点添加自定义权限组
     *
     * @param EAsElement 扩张属性集合的节点对象
     */
    private void appendRightGroupsToWP(Element EAsElement) throws Exception {
        try {
            // 流程版本权限组定义管理器接口
            DbProcessDefRightGrpManager rightGrpManager = DbProcessDefRightGrpManager.getInstance();
            List rightgroupList = rightGrpManager.getRightGroupByProcessDefVersionId(processDefVersion.getProcessDefVersionId());
            int rightgroupCount = rightgroupList.size();
            if (rightgroupCount > 0) {
                // 向扩展属性节点集合中添加自定义权限组的扩展属性
                Element rightgroupsEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                rightgroupsEAElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupsEAElement, "Name", "RightGroups"));
                rightgroupsEAElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupsEAElement, "Value", ""));
                // 自定义权限组元素
                Element rightgroupsElement = XMLParse.createChildElement(rightgroupsEAElement, "itjds:RightGroups", "");
                // ============获得数据库中流程的所有自定义权限组============
                for (int j = 0; j < rightgroupCount; j++) {
                    DbProcessDefRightGrp rightgroupDef = (DbProcessDefRightGrp) rightgroupList.get(j);
                    Element rightgroupElement = XMLParse.createChildElement(rightgroupsElement, "itjds:RightGroup", "");
                    rightgroupElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupElement, "Id", rightgroupDef.getRightGroupId()));
                    rightgroupElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupElement, "Name", rightgroupDef.getGroupName()));
                    rightgroupElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupElement, "Order", String.valueOf(rightgroupDef.getGroupOrder())));
                    rightgroupElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupElement, "Code", rightgroupDef.getGroupCode()));
                    rightgroupElement.setAttributeNode(XMLParse.createAttributeForElement(rightgroupElement, "DefaultGroup", rightgroupDef.getDefaultRight()));
                    rightgroupsElement.appendChild((Node) rightgroupElement);
                }
                rightgroupsEAElement.appendChild((Node) rightgroupsElement);
                EAsElement.appendChild((Node) rightgroupsEAElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}


