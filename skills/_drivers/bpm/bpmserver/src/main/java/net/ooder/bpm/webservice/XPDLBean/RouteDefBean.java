package net.ooder.bpm.webservice.XPDLBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.inter.EIRouteDefManager;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.webservice.XMLParse;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: XPDL文件中工作流路由信息与数据库工作流路由信息的存取
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
public class RouteDefBean extends AbstractBean {

    // XPDL文件中工作流节点对象
    private Node wpNode;

    // XPDL文件中工作流路由节点对象
    private Node routeNode;

    // XPDL文件中父节点对象
    private Node parentNode;

    // 工作流的路由ID
    private String routeId;

    // 活动定义管理器接口
    private EIRouteDefManager routeDefManager = EIRouteDefManager.getInstance();

    // 获得工作流路由基本属性表对象
    private EIRouteDef routeDef;

    private ProcessDefVersionBean processDefVersionBean;

    /**
     * XPDL流程Bean构造方法
     * 
     * @param node
     *            工作流Dom Node对象
     * @param routeNode
     *            路由Dom Node对象
     */
    public RouteDefBean(Node wpNode, Node routeNode) {
	this.wpNode = wpNode;
	this.routeNode = routeNode;
    }

    public RouteDefBean(Node wpNode, Node routeNode, ProcessDefVersionBean processDefVersionBean) {
	this.wpNode = wpNode;
	this.routeNode = routeNode;
	this.processDefVersionBean = processDefVersionBean;
    }

    /**
     * XPDL流程Bean构造方法
     * 
     * @param node
     *            工作流Dom Node对象
     * @param routeId
     *            工作流路由Id
     */
    public RouteDefBean(Node wpNode, String routeId) {
	this.wpNode = wpNode;
	this.routeId = routeId;
    }

    /**
     * 获得工作流路由ID
     * 
     * @return routeDefId
     */
    public String getRouteDefId() {
	String routeDefId = XMLParse.getAttributeValue(routeNode, "Id");
	return routeDefId;
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
     * 获得工作流路由名称
     * 
     * @return routeDefName
     */
    public String getRouteDefName() {
	String routeDefName = XMLParse.getAttributeValue(routeNode, "Name");
	return routeDefName;
    }

    /**
     * 获得工作流路由描述
     * 
     * @return description
     */
    public String getDescription() {
	String description = "";

	List desNodeList = XMLParse.getChildNodesByName(routeNode, "Description");
	if (desNodeList != null) {
	    Node desNode = (Node) desNodeList.get(0);

	    description = XMLParse.getNodeValue(desNode);
	}
	return description;
    }

    /**
     * 获得工作流路由出发活动ID
     * 
     * @return fromActivitydefId
     */
    public String getFromActivitydefId() {
	String fromActivitydefId = XMLParse.getAttributeValue(routeNode, "From");
	return fromActivitydefId;
    }

    /**
     * 获得工作流路由到达的活动ID
     * 
     * @return toActivitydefId
     */
    public String getToActivitydefId() {
	String toActivitydefId = XMLParse.getAttributeValue(routeNode, "To");
	return toActivitydefId;
    }

    /**
     * 获得工作流路由顺序编号
     * 
     * @return routeOrder
     */
    public int getRouteOrder() {
	int routeOrder = 0;
	String orderNum = getExtendedAttributeElementValue(routeNode, "RouteOrder");
	if (!orderNum.equals("")) {
	    routeOrder = Integer.parseInt(orderNum);
	}
	return routeOrder;
    }

    /**
     * 获得工作流路由方向
     * 
     * @return routeDirection
     */
    public RouteDirction getRouteDirection() {
	String routeDirection = getExtendedAttributeElementValue(routeNode, "RouteDirection");
	return RouteDirction.fromType(routeDirection);
    }

    /**
     * 获得工作流路由条件
     * 
     * @return routeCondition
     */
    public String getRouteCondition() {
	String routeCondition = "";
	List conNodeList = XMLParse.getChildNodesByName(routeNode, "Condition");
	if (conNodeList.size() != 0) {
	    Node conNode = (Node) conNodeList.get(0);
	    routeCondition = XMLParse.getNodeValue(conNode);
	}
	return routeCondition;
    }

    /**
     * 获得工作流路由条件
     * 
     * @return routeCondition
     */
    public String getRouteConditionType() {
	String routeCondition = "";
	List conNodeList = XMLParse.getChildNodesByName(routeNode, "Condition");
	if (conNodeList.size() != 0) {
	    Node conNode = (Node) conNodeList.get(0);
	    routeCondition = XMLParse.getAttributeValue(conNode, "Type");
	}
	return routeCondition;
    }

    /**
     * 向数据库中工作流路由定义表插入一条记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean insertDataToDB() throws Exception {
	try {
	    // 获得工作流路由定义基本属性表对象
	    routeDef = routeDefManager.createRouteDef();
	    routeDef.setRouteDefId(this.getRouteDefId());
	    routeDef.setProcessDefId(this.getProcessDefId());
	    routeDef.setProcessDefVersionId(this.getProcessDefVersionId());
	    routeDef.setName(this.getRouteDefName());
	    routeDef.setDescription(this.getDescription());
	    routeDef.setFromActivityDefId(this.getFromActivitydefId());
	    routeDef.setToActivityDefId(this.getToActivitydefId());
	    routeDef.setRouteCondition(this.getRouteCondition());
	    routeDef.setRouteDirection(this.getRouteDirection().getType());
	    routeDef.setRouteOrder(this.getRouteOrder());
	    routeDef.setRouteConditionType(getRouteConditionType());
	    // 清空该活动以前的扩展属性
	    routeDef.clearAttribute();
	    // 插入路由监听器信息
	    insertListenersToDB();
	    // 插入路由扩展属性信息
	    insertExtendedAttributesToDB();
	    routeDefManager.save(routeDef);
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	    // return false;
	}
	return true;
    }

    /**
     * 删除数据库中工作流路由定义表的记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean deleteDateFromDB() throws Exception {
	try {
	    // 获得工作流路由定义基本属性表对象
	    routeDefManager.deleteByKey(this.routeId);
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	    // return false;
	}
	return true;
    }

    /**
     * 向数据库中路由扩展属性表插入记录 ＝＝继承的抽象类方法＝＝
     */
    protected void insertExtendedAttributesToDB() throws Exception {
	try {
	    // 存在XPSL文件中，但不属于路由扩展属性的元素
	    List nonAttributeList = new ArrayList();
	    nonAttributeList.add("RouteDirection");
	    nonAttributeList.add("RouteOrder");
	    // 获得扩张属性对象
	    Map attributeMap = getExtendedAttributesList(routeNode, nonAttributeList);
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
		    routeDef.setAttribute(parentAttributeName, attributeDef);
		}
	    }
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /**
     * 向数据库中侦听器表插入记录 ＝＝继承的抽象类方法＝＝
     */

    protected void insertListenersToDB() throws Exception {
	try {
	    List listenerList = getListenersList(this.routeNode);
	    routeDef.setListeners(listenerList);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /**
     * 将路由信息转化为DOM中的Node对象
     * 
     * @return routeElement
     */
    public Element appendBeanToXPDL() throws Exception {
	try {
	    // 获得数据库中的路由定义表对象
	    routeDef = routeDefManager.loadByKey(this.routeId);
	    // 创建一个Transition路由Node对象
	    Document doc = wpNode.getOwnerDocument();
	    Element routeElement = doc.createElement("Transition");
	    // 为Transition元素添加属性
	    routeElement.setAttributeNode(XMLParse.createAttributeForElement(routeElement, "From", routeDef.getFromActivityDefId()));
	    routeElement.setAttributeNode(XMLParse.createAttributeForElement(routeElement, "Id", routeDef.getRouteDefId()));
	    routeElement.setAttributeNode(XMLParse.createAttributeForElement(routeElement, "Name", routeDef.getName()));
	    routeElement.setAttributeNode(XMLParse.createAttributeForElement(routeElement, "To", routeDef.getToActivityDefId()));
	    // 为Transition元素添加Condition子节点
	    Element conElement = XMLParse.createChildElement(routeElement, "Condition", routeDef.getRouteCondition());
	    conElement.setAttributeNode(XMLParse.createAttributeForElement(conElement, "Type", routeDef.getRouteConditionType()));
	    routeElement.appendChild((Node) conElement);
	    // 为Transition元素添加Description子节点
	    Element desElement = XMLParse.createChildElement(routeElement, "Description", routeDef.getDescription());
	    routeElement.appendChild((Node) desElement);

	    // 为Transition元素添加扩展属性ExtendedAttributes子节点
	    appendExtendedAttributesToRoute(routeElement);
	    // 返回Transition对象
	    return routeElement;
	} catch (BPMException e) {
	    e.printStackTrace();
	    // return null;
	    throw e;
	}
    }

    /**
     * 为路由节点添加扩展属性
     * 
     * @param routeElement
     *            路由元素对象
     */
    private void appendExtendedAttributesToRoute(Element routeElement) throws Exception {
	try {
	    Element EAsElement = XMLParse.createChildElement(routeElement, "ExtendedAttributes", "");
	    List topActivityAttrList = routeDef.getTopAttribute();
	    // ===============向扩展属性节点集合中添加扩展属性=============
	    for (int i = 0; i < topActivityAttrList.size(); i++) {
		EIAttributeDef attributeDef = (EIAttributeDef) topActivityAttrList.get(i);
		appendOneExtendedAttribute(EAsElement, "", attributeDef);
	    }
	    // ==========将路由顺序作为扩展属性添加==========
	    Element orderEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
	    orderEAElement.setAttributeNode(XMLParse.createAttributeForElement(orderEAElement, "Type", ""));
	    orderEAElement.setAttributeNode(XMLParse.createAttributeForElement(orderEAElement, "Name", "RouteOrder"));
	    orderEAElement.setAttributeNode(XMLParse.createAttributeForElement(orderEAElement, "Value", String.valueOf(routeDef.getRouteOrder())));
	    EAsElement.appendChild((Node) orderEAElement);
	    // ==========将路由方向作为扩展属性添加==========
	    Element directionEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");
	    directionEAElement.setAttributeNode(XMLParse.createAttributeForElement(directionEAElement, "Type", ""));
	    directionEAElement.setAttributeNode(XMLParse.createAttributeForElement(directionEAElement, "Name", "RouteDirection"));
	    directionEAElement.setAttributeNode(XMLParse.createAttributeForElement(directionEAElement, "Value", routeDef.getRouteDirection()));
	    EAsElement.appendChild((Node) directionEAElement);
	    appendListenersToRoute(EAsElement);
	    routeElement.appendChild((Node) EAsElement);
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
     * 为路由节点添加监听器
     * 
     * @param EAsElement
     *            扩展属性集合的节点对象
     */
    private void appendListenersToRoute(Element EAsElement) throws Exception {
	try {
	    // 获得数据库中活动的所有监听器
	    List listenerList = routeDef.getListeners();

	    if (listenerList != null && listenerList.size() > 0) {
		int listenerCount = listenerList.size();
		// 向扩展属性节点集合中添加表示监听器的扩展属性
		Element listenersEAElement = XMLParse.createChildElement(EAsElement, "ExtendedAttribute", "");

		listenersEAElement.setAttributeNode(XMLParse.createAttributeForElement(listenersEAElement, "Name", "Listeners"));
		// listenersEAElement.setAttributeNode(XMLParse
		// .createAttributeForElement(listenersEAElement, "Value",
		// ""));
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
     * 修改数据库中工作流路由定义表的记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean updateDateFromDB() {
	return false;
    }
}

