package net.ooder.bpm.webservice.XPDLBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.inter.EIRouteDefManager;
import net.ooder.bpm.engine.subflow.db.ActRefPd;
import net.ooder.bpm.engine.subflow.db.DbActRefPdDAO;
import net.ooder.bpm.engine.subflow.db.DbActRefPdManager;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.webservice.XMLParse;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: XPDL文件中工作流活动信息与数据库工作流活动信息的存取
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * <p>
 * Created on 2004-2-7
 * </p>
 *
 * @author Lizhy
 * @version 1.0
 */
public class ActivityDefBean extends AbstractBean {

    // XPDL文件中工作流节点对象
    private Node wpNode;

    // XPDL文件中工作流活动节点对象
    private Node actNode;

    // 工作流的活动ID
    private String activityId;

    // 活动定义管理器接口
    private EIActivityDefManager activityDefManager = EIActivityDefManager.getInstance();

    // 活动定义基本属性表对象
    private EIActivityDef activityDef;

    private ProcessDefVersionBean processDefVersionBean;

    /**
     * XPDL流程Bean构造方法
     *
     * @param actNode 活动Dom Node对象
     */
    public ActivityDefBean(Node wpNode, Node actNode) {
        this.wpNode = wpNode;
        this.actNode = actNode;
    }

    /**
     * XPDL流程Bean构造方法
     *
     * @param actNode 活动Dom Node对象
     */
    public ActivityDefBean(Node wpNode, Node actNode, ProcessDefVersionBean pdb) {
        this.wpNode = wpNode;
        this.actNode = actNode;
        this.processDefVersionBean = pdb;
    }

    /**
     * XPDL流程Bean构造方法
     *
     * @param activityId 活动ID
     */
    public ActivityDefBean(Node wpNode, String activityId) {
        this.wpNode = wpNode;
        this.activityId = activityId;
    }

    /**
     * 获得工作流活动ID
     *
     * @return activityDefId
     */
    public String getActivityDefId() {
        String activityDefId = XMLParse.getAttributeValue(actNode, "Id");
        return activityDefId;
    }

    /**
     * 获得工作流版本ID
     *
     * @return processDefVersionId
     */
    public String getProcessDefVersionId() {
        String processDefVersionId = null;
        if (this.processDefVersionBean != null) {
            processDefVersionId = processDefVersionBean.getProcessDefVersionId();
        } else {
            processDefVersionId = getExtendedAttributeElementValue(wpNode, "VersionId");
        }

        return processDefVersionId;
    }

    /**
     * 获得工作流ID
     *
     * @return processDefId
     */
    public String getProcessDefId() {
        String processDefId = null;
        if (this.processDefVersionBean != null) {
            processDefId = processDefVersionBean.getProcessDefId();
        } else {
            processDefId = XMLParse.getAttributeValue(wpNode, "Id");

        }

        return processDefId;
    }

    /**
     * 获得工作流活动名称
     *
     * @return activityDefName
     */
    public String getActivityDefName() {
        String activityDefName = XMLParse.getAttributeValue(actNode, "Name");
        return activityDefName;
    }

    /**
     * 获得工作流活动描述
     *
     * @return description
     */
    public String getDescription() {
        String description = "";
        List desNodeList = XMLParse.getChildNodesByName(actNode, "Description");
        if (desNodeList != null && desNodeList.size() != 0) {
            Node desNode = (Node) desNodeList.get(0);
            description = XMLParse.getNodeValue(desNode);
        }
        return description;
    }

    /**
     * 获得工作流活动位置
     *
     * @return position
     */
    public String getPosition() {
        String position = getExtendedAttributeElementValue(actNode, "Position");

        // 个别情况下开始活动position会出现NULL值2010-10-16
        if (position == null || position.equals("")) {
            position = "START";
        }
        return position;
    }

    /**
     * 获得工作流活动实现类型
     *
     * @return implementation
     */
    public String getImplementation() {
        String implementation = "";
        List impNodeList = XMLParse.getChildNodesByName(actNode, "Implementation");
        if (impNodeList != null && impNodeList.size() != 0) {
            Node impNode = (Node) impNodeList.get(0);
            if (impNode.hasChildNodes()) {
                NodeList impChildNodes = impNode.getChildNodes();
                Node impChildNode = impChildNodes.item(1);
                if (impChildNode == null) {
                    impChildNode = impChildNodes.item(0);
                }
                if (impChildNode != null) {
                    impChildNode = impChildNodes.item(0);
                    implementation = impChildNode.getNodeName();
                }

            }
        }
        return implementation;
    }

    /**
     * 获得工作流自动活动执行体
     *
     * @return execClass
     */
    public String getExecClass() {
        String execClass = "";
        List impNodeList = XMLParse.getChildNodesByName(actNode, "Implementation");
        if (impNodeList != null) {
            Node impNode = (Node) impNodeList.get(0);
            if (impNode.hasChildNodes()) {
                NodeList impChildNodes = impNode.getChildNodes();
                Node impChildNode = impChildNodes.item(1);
                if (impChildNode == null) {
                    impChildNode = impChildNodes.item(0);
                }
                // String nodeName = impChildNode.getNodeName();
                if (impChildNode != null && impChildNode.getNodeName().equals("Tool")) {
                    execClass = XMLParse.getAttributeValue(impChildNode, "ExecClass");
                }
            }
        }
        return execClass;
    }

    /**
     * 获得工作流活动处理期限
     *
     * @return limit
     */
    public int getLimit() {
        int limit = 0;
        List limitNodeList = XMLParse.getChildNodesByName(actNode, "Limit");
        if (limitNodeList != null && limitNodeList.size() != 0) {
            Node limitNode = (Node) limitNodeList.get(0);
            if (!XMLParse.getNodeValue(limitNode).trim().equals("")) {
                limit = Integer.parseInt(XMLParse.getNodeValue(limitNode));
            }
        }
        return limit;
    }

    /**
     * 获得工作流活动预警时间
     *
     * @return alertTime
     */
    public int getAlertTime() {
        int alertTime = 0;
        String aTime = getExtendedAttributeElementValue(actNode, "AlertTime");
        if (!aTime.trim().equals("")) {
            alertTime = Integer.parseInt(aTime);
        }
        return alertTime;
    }

    /**
     * 获得工作流活动时间限制单位
     *
     * @return durationUnit
     */
    public String getDurationUnit() {
        String durationUnit = getExtendedAttributeElementValue(actNode, "DurationUnit");
        return durationUnit;
    }

    /**
     * 获得工作流活动到期处理办法
     *
     * @return deadLineOperation
     */
    public String getDeadLineOperation() {
        String deadLineOperation = getExtendedAttributeElementValue(actNode, "DeadLineOperation");
        if (deadLineOperation == null) {
            deadLineOperation = "DEFAULT";
        }
        ;
        return deadLineOperation;
    }

    /**
     * 获得工作流活动是否允许退回的标识
     *
     * @return canRouteBack
     */
    public String getCanRouteBack() {
        String canRouteBack = getExtendedAttributeElementValue(actNode, "CanRouteBack");
        return canRouteBack;
    }

    /**
     * 获得工作流活动退回方法
     *
     * @return routeBackMethod
     */
    public String getRouteBackMethod() {
        String routeBackMethod = getExtendedAttributeElementValue(actNode, "RouteBackMethod");
        return routeBackMethod;
    }

    /**
     * 获得工作流活动特送设定
     *
     * @return canSpecialSend
     */
    public String getCanSpecialSend() {
        String canSpecialSend = getExtendedAttributeElementValue(actNode, "CanSpecialSend");
        return canSpecialSend;
    }

    /**
     * 获得工作流活动入口条件类型
     *
     * @return join
     */
    public String getJoin() {
        String join = getTransitionRestrictionElementValue("Join");
        return join;
    }

    /**
     * 获得工作流活动出口路由类型
     *
     * @return split
     */
    public String getSplit() {
        String split = getTransitionRestrictionElementValue("Split");
        return split;
    }

    /**
     * 获得到指定流程的路由类型元素值
     *
     * @return value 路由类型值
     */
    private String getTransitionRestrictionElementValue(String name) {
        // 返回的路由类型值
        String typeValue = "";
        List trsNodeList = XMLParse.getChildNodesByName(actNode, "TransitionRestrictions");
        if (trsNodeList.size() != 0) {
            Node easNode = (Node) trsNodeList.get(0);
            // 获得路由类型节点集合
            List trNodeList = XMLParse.getChildNodesByName(easNode, "TransitionRestriction");
            for (int i = 0; i < trNodeList.size(); i++) {
                Node trNode = (Node) trNodeList.get(i);
                if (trNode.hasChildNodes()) {
                    NodeList nodeList = trNode.getChildNodes();

                    for (int k = 0; k < nodeList.getLength(); k++) {
                        Node node = nodeList.item(k);
                        if (node != null && node.getNodeName().equals(name)) {
                            typeValue = XMLParse.getAttributeValue(node, "Type");
                            break;
                        }
                    }
                }
            }
        }
        return typeValue;
    }

    /**
     * 向数据库中工作流活动定义表插入一条记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean insertDataToDB() throws Exception {
        try {
            // 获得工作流活动定义基本属性表对象
            activityDef = activityDefManager.createActivityDef();
            // 插入活动基本信息
            activityDef.setActivityDefId(this.getActivityDefId());
            activityDef.setProcessDefId(this.getProcessDefId());
            activityDef.setProcessDefVersionId(this.getProcessDefVersionId());
            activityDef.setName(this.getActivityDefName());
            activityDef.setAlertTime(this.getAlertTime());
            activityDef.setCanRouteBack(this.getCanRouteBack());
            activityDef.setCanSpecialSend(this.getCanSpecialSend());
            activityDef.setDeadlineOperation(this.getDeadLineOperation());
            activityDef.setDescription(this.getDescription());
            activityDef.setDurationUnit(this.getDurationUnit());
            activityDef.setExecClass(this.getExecClass());
            /**
             * =======如果活动类型为子流程活动或跳转流程活动， 那么插入他们之间的对应关系信息到数据库=============
             */
            String implementation = this.getImplementation();
            if (implementation.equals("SubFlow") || implementation.equals("OutFlow") || implementation.equals("Block")) {
                insertProcessMappingToDB();

            }
            /** =================================================== */
            activityDef.setImplementation(implementation);
            activityDef.setJoin(this.getJoin());
            activityDef.setLimit(this.getLimit());
            activityDef.setPosition(this.getPosition());
            activityDef.setRouteBackMethod(this.getRouteBackMethod());
            activityDef.setSplit(this.getSplit());
            // 清空该活动以前的扩展属性
            activityDef.clearAttribute();
            // 插入活动扩展属性信息
            insertExtendedAttributesToDB();
            // 插入活动监听器信息
            insertListenersToDB();
            // 保存活动
            activityDefManager.save(activityDef);
            // 如果活动是结束活动，那么自动插入一条结束路由
            if (this.getPosition().trim().toUpperCase().equals(ActivityDefPosition.POSITION_END)) {
                EIRouteDefManager routeDefManager = EIRouteDefManager.getInstance();
                EIRouteDef routeDef = routeDefManager.createRouteDef();
                routeDef.setRouteDefId(UUID.randomUUID().toString());
                routeDef.setProcessDefId(this.getProcessDefId());
                routeDef.setProcessDefVersionId(this.getProcessDefVersionId());
                routeDef.setName("结束");
                routeDef.setDescription("结束路由");
                routeDef.setFromActivityDefId(this.getActivityDefId());
                routeDef.setToActivityDefId(ActivityDefPosition.VIRTUAL_LAST_DEF.getType());
                routeDef.setRouteCondition("");
                routeDef.setRouteDirection(RouteDirction.FORWARD.getType());
                routeDefManager.save(routeDef);
            }

        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /**
     * 向数据库中插入子流程活动或跳转活动与子流程的对应关系信息
     *
     * @throws SQLException
     */
    private void insertProcessMappingToDB() throws SQLException {
        String subFlowId = "";
        String execution = "";
        String subFlowVerId = "";
        List impNodeList = XMLParse.getChildNodesByName(actNode, "Implementation");
        if (impNodeList != null) {
            Node impNode = (Node) impNodeList.get(0);
            if (impNode.hasChildNodes()) {
                NodeList impChildNodes = impNode.getChildNodes();
                Node impChildNode = impChildNodes.item(0);
                String nodeName = impChildNode.getNodeName();
                if (nodeName.equals("SubFlow") || nodeName.equals("OutFlow") || nodeName.equals("Block")) {
                    subFlowId = XMLParse.getAttributeValue(impChildNode, "Id");
                    if (subFlowId == null || subFlowId.equals("")) {
                        return;
                    }

                    execution = XMLParse.getAttributeValue(impChildNode, "Execution");

                    subFlowVerId = XMLParse.getAttributeValue(impChildNode, "VerId");
                }

                // Connection conn = DbManager.getInstance().getConnection();

                DbActRefPdManager dbActRefPdManager = new DbActRefPdManager();
                ActRefPd ap = dbActRefPdManager.getNewDbActRefPd();
                ap.setParentprocessVerId(this.getProcessDefVersionId());
                ap.setProcesstype(nodeName);
                ap.setDestprocessVerId(subFlowId);
                ap.setActivitydefId(this.getActivityDefId());
                ap.setIswaitreturn(execution);
                ap.setMainprocessVerId(this.processDefVersionBean.getMainProcessDefVersionId());
                dbActRefPdManager.saveInstance(ap);

            }
        }

    }

    /**
     * 删除数据库中工作流定义表的记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean deleteDateFromDB() throws Exception {
        try {
            // 通过活动ID删除活动
            activityDefManager.deleteByKey(this.activityId);
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /**
     * 向数据库中活动扩展属性表插入记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    protected void insertExtendedAttributesToDB() throws Exception {
        try {
            // 存在XPSL文件中，但不属于活动扩展属性的元素
            List nonAttributeList = new ArrayList();
            nonAttributeList.add("Listeners");
            nonAttributeList.add("DurationUnit");
            nonAttributeList.add("AlertTime");
            nonAttributeList.add("DeadLineOperation");
            nonAttributeList.add("CanRouteBack");
            nonAttributeList.add("RouteBackMethod");
            nonAttributeList.add("CanSpecialSend");
            nonAttributeList.add("Position");
            // 获得扩展属性对象
            Map attributeMap = getExtendedAttributesList(actNode, nonAttributeList);

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
                    activityDef.setAttribute(parentAttributeName, attributeDef);
                }
            }
        } catch (BPMException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 向数据库中活动监听器表插入活动监听器记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    protected void insertListenersToDB() throws Exception {
        try {
            List listenerList = new ArrayList();
            listenerList.addAll(getListenersList(actNode));
            activityDef.setListeners(listenerList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 将活动信息转化为DOM中的Element对象 ＝＝继承的抽象类方法＝＝
     *
     * @return activityElement
     */
    public Element appendBeanToXPDL() {
        Element activityElement = null;
        try {
            // 获得数据库中的活动定义表对象
            activityDef = activityDefManager.loadByKey(this.activityId);
            // 创建一个Activity活动Document对象
            Document doc = wpNode.getOwnerDocument();
            activityElement = doc.createElement("Activity");
            // 为Activity元素添加属性
            activityElement.setAttributeNode(XMLParse.createAttributeForElement(activityElement, "Id", activityDef.getActivityDefId()));

            activityElement.setAttributeNode(XMLParse.createAttributeForElement(activityElement, "Name", activityDef.getName()));
            // 为Activity元素添加Description子节点
            Element desElement = XMLParse.createChildElement(activityElement, "Description", activityDef.getDescription());
            activityElement.appendChild((Node) desElement);
            // 为Activity元素添加Implementation子节点
            Element implElement = XMLParse.createChildElement(activityElement, "Implementation", "");
            String implName = activityDef.getImplementation();

            Element implChildElement = XMLParse.createChildElement(implElement, implName, "");
            if (implName.equals("Tool")) {
                implChildElement.setAttributeNode(XMLParse.createAttributeForElement(implChildElement, "Name", ""));
                implChildElement.setAttributeNode(XMLParse.createAttributeForElement(implChildElement, "ExecClass", ""));
            } else if (implName.equals("SubFlow") || implName.equals("OutFlow") || implName.equals("Block")) {

                DbActRefPdDAO apDao = new DbActRefPdDAO();
                ActRefPd ap = apDao.findById(this.activityId);
                if (ap != null) {
                    String processId = ap.getDestprocessVerId();
                    String iswaitreturn = ap.getIswaitreturn();
                    String processtype = ap.getProcesstype();
                    implChildElement.setAttributeNode(XMLParse.createAttributeForElement(implChildElement, "Id", processId));
                    implChildElement.setAttributeNode(XMLParse.createAttributeForElement(implChildElement, "Execution", iswaitreturn));
                    implChildElement.setAttributeNode(XMLParse.createAttributeForElement(activityElement, "VerId", ap.getDestprocessVerId()));
                }

            }
            implElement.appendChild((Node) implChildElement);
            activityElement.appendChild((Node) implElement);
            // 为Activity元素添加扩展属性ExtendedAttributes子节点
            appendExtendedAttributesToActivity(activityElement);
            // 为Activity元素添加扩展属性TransitionRestrictions子节点

            Element trsElements = XMLParse.createChildElement(activityElement, "TransitionRestrictions", "");
            // 为Activity元素添加Split子节点
            Element trsElement = XMLParse.createChildElement(trsElements, "TransitionRestriction", "");

            //

            if (activityDef.getSplit() != null) {
                Element splitElement = XMLParse.createChildElement(trsElement, "Split", "");
                splitElement.setAttributeNode(XMLParse.createAttributeForElement(splitElement, "Type", activityDef.getSplit()));
                trsElement.appendChild(splitElement);
            }


            if (activityDef.getJoin() != null) {
                Element joinElement = XMLParse.createChildElement(trsElement, "Join", "");
                joinElement.setAttributeNode(XMLParse.createAttributeForElement(joinElement, "Type", activityDef.getJoin()));
                trsElement.appendChild(joinElement);

            }
            trsElements.appendChild(trsElement);
            activityElement.appendChild((Node) trsElements);

            // 为Activity元素添加Limit子节点
            Element limitElement = XMLParse.createChildElement(activityElement, "Limit", String.valueOf(activityDef.getLimit()));
            activityElement.appendChild((Node) limitElement);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return activityElement;
    }

    /**
     * 为活动节点添加扩展属性
     *
     * @param activityElement 活动元素对象
     */
    private void appendExtendedAttributesToActivity(Element activityElement) throws Exception {
        try {
            Element EAsElement = XMLParse.createChildElement(activityElement, "ExtendedAttributes", "");
            // 数据库中活动的所有属性
            List topActivityAttrList = activityDef.getTopAttribute();
            // ===============向扩展属性节点集合中添加扩展属性=============
            for (int i = 0; i < topActivityAttrList.size(); i++) {
                EIAttributeDef attributeDef = (EIAttributeDef) topActivityAttrList.get(i);
                appendOneExtendedAttribute(EAsElement, "", attributeDef);
            }
            // ==============将活动到期处理办法作为扩展属性添加===========
            if (activityDef.getDurationUnit() != null) {
                Element duEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                duEAElement.setAttributeNode(XMLParse.createAttributeForElement(duEAElement, "Type", ""));
                duEAElement.setAttributeNode(XMLParse.createAttributeForElement(duEAElement, "Name", "DurationUnit"));
                duEAElement.setAttributeNode(XMLParse.createAttributeForElement(duEAElement, "Value", activityDef.getDurationUnit()));
                EAsElement.appendChild((Node) duEAElement);
            }
            // ==========将活动预警时间作为扩展属性添加===========
            if (activityDef.getDurationUnit() != null) {
                Element alerttimeEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                alerttimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(alerttimeEAElement, "Type", ""));
                alerttimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(alerttimeEAElement, "Name", "AlertTime"));
                alerttimeEAElement.setAttributeNode(XMLParse.createAttributeForElement(alerttimeEAElement, "Value", String.valueOf(activityDef.getAlertTime())));
                EAsElement.appendChild((Node) alerttimeEAElement);
            }
            // ===========将活动到期处理办法作为扩展属性添加==========
            if (activityDef.getDeadlineOperation() != null) {
                Element doEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                doEAElement.setAttributeNode(XMLParse.createAttributeForElement(doEAElement, "Type", ""));
                doEAElement.setAttributeNode(XMLParse.createAttributeForElement(doEAElement, "Name", "DeadLineOperation"));
                doEAElement.setAttributeNode(XMLParse.createAttributeForElement(doEAElement, "Value", activityDef.getDeadlineOperation()));
                EAsElement.appendChild((Node) doEAElement);
            }
            // ==========将活动是否允许退回的标识作为扩展属性添加==========
            if (activityDef.getCanRouteBack() != null) {
                Element crbEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                crbEAElement.setAttributeNode(XMLParse.createAttributeForElement(crbEAElement, "Type", ""));
                crbEAElement.setAttributeNode(XMLParse.createAttributeForElement(crbEAElement, "Name", "CanRouteBack"));
                crbEAElement.setAttributeNode(XMLParse.createAttributeForElement(crbEAElement, "Value", activityDef.getCanRouteBack()));
                EAsElement.appendChild((Node) crbEAElement);
            }
            // ==============将活动退回方法作为扩展属性添加===========
            if (activityDef.getRouteBackMethod() != null) {
                Element rbmEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                rbmEAElement.setAttributeNode(XMLParse.createAttributeForElement(rbmEAElement, "Type", ""));
                rbmEAElement.setAttributeNode(XMLParse.createAttributeForElement(rbmEAElement, "Name", "RouteBackMethod"));
                rbmEAElement.setAttributeNode(XMLParse.createAttributeForElement(rbmEAElement, "Value", activityDef.getRouteBackMethod()));
                EAsElement.appendChild((Node) rbmEAElement);
            }
            // ==============将活动特送设定作为扩展属性添加===========
            if (activityDef.getCanSpecialSend() != null) {
                Element cssEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Type", ""));
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Name", "CanSpecialSend"));
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Value", activityDef.getCanSpecialSend()));
                EAsElement.appendChild((Node) cssEAElement);
            }

            // ==============将活动位置设定作为扩展属性添加===========
            if (activityDef.getPosition() != null) {
                Element cssEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Type", ""));
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Name", "Position"));
                cssEAElement.setAttributeNode(XMLParse.createAttributeForElement(cssEAElement, "Value", activityDef.getPosition()));
                EAsElement.appendChild((Node) cssEAElement);
            }
            // 为活动节点添加监听器
            appendListenersToActivity(EAsElement);
            // 为活动元素添加扩展属性集合的子节点
            activityElement.appendChild((Node) EAsElement);
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
     * 为活动节点添加监听器
     *
     * @param EAsElement 扩张属性集合的节点对象
     */
    private void appendListenersToActivity(Element EAsElement) throws Exception {
        try {
            // 获得数据库中活动的所有监听器
            List listenerList = activityDef.getListeners();
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

                    String key = "itjds:Listener";

                    Element listenerElement = XMLParse.createChildElement(listenersElement, key, "");

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
     * 修改数据库中工作流定义表的记录 ＝＝继承的抽象类方法＝＝
     *
     * @return boolean
     */
    public boolean updateDateFromDB() {
        return false;
    }
}
