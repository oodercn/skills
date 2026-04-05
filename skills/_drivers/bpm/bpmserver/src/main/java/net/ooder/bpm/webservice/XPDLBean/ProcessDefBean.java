package net.ooder.bpm.webservice.XPDLBean;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
import net.ooder.bpm.webservice.XMLParse;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: XPDL文件中工作流信息与数据库工作流信息的存取
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
public class ProcessDefBean extends AbstractBean {

    // XPDL文件中工作流节点对象
    private Node wpNode;

    // 工作流表字段对应的变量
    private String processId;

    // 流程定义管理器接口
    private EIProcessDefManager processDefManager;

    // 获得工作流定义基本属性表对象
    EIProcessDef processDef;

    private boolean block;

    /**
     * XPDL流程Bean构造方法
     * 
     * @param wpNode
     *            工作流Dom Node对象
     */

    public ProcessDefBean(Node wpNode) {
	this(wpNode, false);
    }

    public ProcessDefBean(Node wpNode, boolean block) {
	this.wpNode = wpNode;
	this.block = block;
	// 获得流程定义管理器接口
	if (processDefManager == null) {
	    processDefManager = EIProcessDefManager.getInstance();
	}
    }

    public ProcessDefBean(String processId) {
	this(processId, false);
    }

    /**
     * XPDL流程Bean构造方法
     * 
     * @param wpNode
     *            工作流Dom Node对象
     * @param processId
     *            流程ID
     */
    public ProcessDefBean(String processId, boolean block) {
	// 流程ID
	this.processId = processId;
	if (processDefManager == null) {
	    processDefManager = EIProcessDefManager.getInstance();
	}
    }

    /**
     * 获得工作流ID
     * 
     * @return processDefId
     */
    public String getProcessDefId() {
	String processDefId = XMLParse.getAttributeValue(wpNode, "Id");
	return processDefId;
    }

    /**
     * 获得工作流名称
     * 
     * @return processDefName
     */
    public String getProcessDefName() {
	String processDefName = XMLParse.getAttributeValue(wpNode, "Name");
	return processDefName;
    }

    /**
     * 获得工作流中一般流程与子流程类别标识
     * 
     * @return AccessLevel
     */
    public String getAccessLevel() {

	String accessLevel = XMLParse.getAttributeValue(wpNode, "AccessLevel");

	return accessLevel;
    }

    /**
     * 获得工作流描述
     * 
     * @return Description
     */
    public String getDescription() {

	String description = "";
	List phNodeList = XMLParse.getChildNodesByName(wpNode, "ProcessHeader");
	if (phNodeList != null) {
	    Node phNode = (Node) phNodeList.get(0);
	    List desNodeList = XMLParse.getChildNodesByName(phNode, "Description");
	    if (desNodeList.size() != 0) {
		description = XMLParse.getNodeValue((Node) desNodeList.get(0));
	    }
	}
	return description;
    }

    /**
     * 获得工作流所属系统标识值
     * 
     * @return SystemCode
     */
    public String getSystemCode() {
	String systemCode = getExtendedAttributeElementValue(wpNode, "SystemCode");
	return systemCode;
    }

    /**
     * 获得工作流流程类型
     * 
     * @return Classification
     */
    public String getClassification() {

	String classification = getExtendedAttributeElementValue(wpNode, "Classification");
	return classification;
    }

    /**
     * 向数据库中工作流定义表插入一条记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean insertDataToDB() throws Exception {
	try {
	    // 获得工作流定义基本属性表对象
	    processDef = processDefManager.createProcessDef();
	    processDef.setProcessDefId(this.getProcessDefId());
	    if (!this.block) {
		processDef.setName(this.getProcessDefName());
		processDef.setDescription(this.getDescription());
		processDef.setSystemCode(this.getSystemCode());
		processDef.setClassification(this.getClassification());
		processDef.setAccessLevel(this.getAccessLevel());
	    } else {
		processDef.setAccessLevel("Block");
	    }
	    processDefManager.save(processDef);
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	    // return false;
	}
	return true;
    }

    /**
     * 修改数据库中工作流定义表的记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean updateDateFromDB() throws Exception {
	try {
	    // 获得工作流定义基本属性表对象
	    if (!this.processIsExist()) {
		this.insertDataToDB();
	    }
	    processDef = processDefManager.loadByKey(this.getProcessDefId());

	    if (!this.block) {
		processDef.setName(this.getProcessDefName());
		processDef.setDescription(this.getDescription());
		processDef.setSystemCode(this.getSystemCode());
		processDef.setClassification(this.getClassification());
		processDef.setAccessLevel(this.getAccessLevel());
	    } else {
		processDef.setAccessLevel("Block");
	    }

	    processDefManager.save(processDef);
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	    // return false;
	}
	return true;
    }

    /**
     * 删除数据库中工作流定义表的记录 ＝＝继承的抽象类方法＝＝
     * 
     * @return boolean
     */
    public boolean deleteDateFromDB() throws Exception {
	try {
	    // 获得工作流定义基本属性表对象
	    processDefManager.deleteByKey(this.processId);
	} catch (BPMException e) {
	    e.printStackTrace();
	    throw e;
	    // return false;
	}
	return true;
    }

    /**
     * 查询流程是否存在
     * 
     * @return boolean
     */
    public boolean processIsExist() throws Exception {
	try {
	    // 获得工作流定义基本属性表对象
	    processDef = processDefManager.loadByKey(this.getProcessDefId());
	    if (processDef != null) {
		return true;
	    } else {
		return false;
	    }
	} catch (BPMException e) {
	    e.printStackTrace();
	    // return false;
	    throw e;
	}
    }

    protected void insertListenersToDB() {
    }

    protected void insertExtendedAttributesToDB() {
    }

    public Element appendBeanToXPDL() {
	return null;
    }

}

