package net.ooder.bpm.webservice.XPDLBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.engine.inter.EIListenerManager;
import net.ooder.bpm.enums.event.ListenerTypeEnums;
import net.ooder.bpm.webservice.XMLParse;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: XPDL文件中工作流信息与数据库工作流信息的存取Bean的抽象基类
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
public abstract class AbstractBean {

	// 活动扩展属性定义管理器接口
	private EIAttributeDefManager attributeDefManager = EIAttributeDefManager
			.getInstance();

	/**
	 * 获得指定流程的扩展属性元素值
	 * 
	 * @param node
	 *            元素节点对象
	 * @param Name
	 *            扩展属性名称
	 * @return value 扩展属性值
	 */
	public String getExtendedAttributeElementValue(Node node, String Name) {
		// 返回的扩展属性值
		String value = "";
		List EAsNodeList = XMLParse.getChildNodesByName(node,
				"ExtendedAttributes");
		if (EAsNodeList != null) {
			
			for(int k=0;EAsNodeList.size()>k;k++){
				Node EAsNode = (Node) EAsNodeList.get(k);
				// 获得扩展属性节点集合
				List EANodeList = XMLParse.getChildNodesByName(EAsNode,
						"ExtendedAttribute");
				for (int i = 0; i < EANodeList.size(); i++) {
					Node EANode = (Node) EANodeList.get(i);
					String attributeName = XMLParse.getAttributeValue(EANode,
							"Name");
					if (attributeName.equals(Name)) {
						value = XMLParse.getAttributeValue(EANode, "Value");
					}
				}
			}
			
		}
		return value;
	}

	/**
	 * 获得元素扩张属性得集合
	 * 
	 * @param node
	 *            元素节点对象
	 * @param nonAttributeList
	 *            非扩展属性集合
	 * @return attributeList 扩展属性集合
	 */
	public Map getExtendedAttributesList(Node node, List nonAttributeList) {
		TreeMap attributeMap = new TreeMap();
		List EAsNodeList = XMLParse.getChildNodesByName(node,
				"ExtendedAttributes");
		if (EAsNodeList.size() != 0) {
			for(int k=0;EAsNodeList.size()>k;k++){
				Node EAsNode = (Node) EAsNodeList.get(k);
			// 获得XPDL文件中元素扩展属性节点集合
			List EANodeList = XMLParse.getChildNodesByName(EAsNode,
					"ExtendedAttribute");
			if (EANodeList != null && EANodeList.size() != 0) {
				for (int i = 0; i < EANodeList.size(); i++) {
					Node EANode = (Node) EANodeList.get(i);
					String attributeName = XMLParse.getAttributeValue(EANode,
							"Name");
					if (nonAttributeList != null) {
						// 排除元素基本信息和监听器以外的非扩展属性
						if (!nonAttributeList.contains(attributeName)) {
							EIAttributeDef attributeDef = attributeDefManager
									.createAttributeDef();
							// 插入扩展属性信息
							String type = XMLParse.getAttributeValue(EANode,
									"Type");
							if (type == null || type.equals("")) {
								type = "BPD";
							}
							String value = XMLParse.getAttributeValue(EANode,
									"Value");
							String childAttributeName = attributeName;
							int index = childAttributeName.lastIndexOf(".");
							if (index != -1) {
								childAttributeName = childAttributeName
										.substring(index + 1);
							}
							attributeDef.setId(UUID.randomUUID().toString());
							attributeDef.setType(type );
							attributeDef.setName(childAttributeName);
							attributeDef.setValue(value);
							attributeMap.put(attributeName, attributeDef);
						}
					}
				}
			}
			}
		}
		return attributeMap;
	}

	/**
	 * 获得对象监听器集合
	 * 
	 * @param node
	 *            元素节点对象
	 * @return listenerList
	 */
	public List getListenersList(Node node) {
		List listenerList = new ArrayList();
		// 监听器定义管理器接口
		EIListenerManager listenerDefManager = EIListenerManager.getInstance();
		// XPDL文件中的扩展属性集合
		List EAsNodeList = XMLParse.getChildNodesByName(node,
				"ExtendedAttributes");
		for(int k=0;EAsNodeList.size()>k;k++){
			Node EAsNode = (Node) EAsNodeList.get(k);
		List EANodeList = XMLParse.getChildNodesByName(EAsNode,
				"ExtendedAttribute");
		for (int i = 0; i < EANodeList.size(); i++) {
			Node EANode = (Node) EANodeList.get(i);
			String attributeName = XMLParse.getAttributeValue(EANode, "Name");
			if (attributeName.equals("Listeners")||attributeName.equals("ClientListeners")) {
				if (EANode.hasChildNodes()) {
					// 获得XPDL文件中Listener元素集合
					List listenersNodeList = XMLParse.getChildNodesByName(
							EANode, "itjds:Listeners");
					Node listenersNode = (Node) listenersNodeList.get(0);
					List listenerNodeList = XMLParse.getChildNodesByName(
							listenersNode, "itjds:Listener");
					for (int j = 0; j < listenerNodeList.size(); j++) {
						Node listenerNode = (Node) listenerNodeList.get(j);
						// 设置Listener对象的字段值
						String id = XMLParse.getAttributeValue(listenerNode,
								"Id");
						String name = XMLParse.getAttributeValue(listenerNode,
								"Name");
						// "ListenerEvent"
						String listenerEvent = XMLParse.getAttributeValue(
								listenerNode,ListenerTypeEnums.ListenerEvent.getType());
						String realizeClass = XMLParse.getAttributeValue(
								listenerNode, "RealizeClass");
						String  expressionEventType=XMLParse.getAttributeValue(
								listenerNode, ListenerTypeEnums.ExpressionEventType.getType());
						String expressionListenerType = XMLParse.getAttributeValue(
								listenerNode, ListenerTypeEnums.ExpressionListenerType.getType());
						                      
						String  expressionStr=XMLParse.getAttributeValue(
								listenerNode, "expressionStr");
						
						EIListener listenerDef = listenerDefManager
								.createListener();
						listenerDef.setListenerId(id);
						listenerDef.setListenerName(name);
						listenerDef.setListenerEvent(listenerEvent);
						listenerDef.setRealizeClass(realizeClass);
						
						listenerDef.setExpressionEventType(expressionEventType);
						listenerDef.setExpressionListenerType(expressionListenerType);
						listenerDef.setExpressionStr(expressionStr);
						
						// 加入Listener对象到List中o
						listenerList.add(listenerDef);
					}
				}
			}
		}
		}
		return listenerList;
	}
	
	

	/**
	 * 向数据库表中保存一条记录
	 * 
	 * @return boolean
	 */
	abstract public boolean insertDataToDB() throws Exception;

	/**
	 * 修改数据库表中的记录
	 * 
	 * @return boolean
	 */
	abstract public boolean updateDateFromDB() throws Exception;

	/**
	 * 删除数据库表中的记录
	 * 
	 * @return boolean
	 */
	abstract public boolean deleteDateFromDB() throws Exception;

	/**
	 * 向数据库中插入监听器记录
	 * 
	 * @return boolean
	 */
	abstract protected void insertListenersToDB() throws Exception;

	/**
	 * 向数据库中插入扩展属性记录
	 * 
	 * @return boolean
	 */
	abstract protected void insertExtendedAttributesToDB() throws Exception;

	/**
	 * 将Bean生成对应的XPDL文件的元素
	 * 
	 * @return boolean
	 */
	abstract public Element appendBeanToXPDL() throws Exception;
}

