/**
 * $RCSfile: AttributeInstProxy.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.Attribute;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.engine.inter.EIAttribute;
import net.ooder.bpm.engine.inter.EIAttributeInst;
import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 扩展属性实例客户端接口的代理实现 （包括流程定义、活动定义和路由定义的扩展属性）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public class AttributeInstProxy implements AttributeInst, Serializable {
    @JSONField(serialize = false)
    
    private EIAttributeInst eiAttributeInst;
    private String systemCode;

    /**
     * @param eiAttributeInst
     */
    public AttributeInstProxy(EIAttributeInst eiAttributeInst, String systemCode) {
	super();
	this.systemCode = systemCode;
	this.eiAttributeInst = eiAttributeInst;
    }

    /*
     * @see com.ds.bpm.client.AttributeInst#getId()
     */
    public String getId() {
	return eiAttributeInst.getId();
    }

    @Override
    public void setId(String id) {

    }

    /*
     * @see com.ds.bpm.client.AttributeInst#getType()
     */
    public Attributetype getType() {
	return Attributetype.fromType(eiAttributeInst.getType());
    }

    /*
     * @see com.ds.bpm.client.AttributeInst#getInterpretClass()
     */

    public AttributeInterpretClass getInterpretClass() {
	return AttributeInterpretClass.fromType(eiAttributeInst.getInterpretClass());
    }

    /*
     * @see com.ds.bpm.client.AttributeInst#getParentId()
     */
    public String getParentId() {
	return eiAttributeInst.getParentId();
    }

    @Override
    public void setParentId(String parentId) {

        this.eiAttributeInst.setParentId(parentId);
    }

    /*
     * @see com.ds.bpm.client.Attribute#getName()
     */

    public String getName() {
	return eiAttributeInst.getName();
    }

    @Override
    public void setName(String name) {
        this.eiAttributeInst.setName(name);
    }

    /*
     * @see com.ds.bpm.client.Attribute#getInterpretedValue()
     */
    @JSONField(serialize = false)
    
    public Object getInterpretedValue() {
	return eiAttributeInst.getInterpretedValue();
    }

    /*
     * @see com.ds.bpm.client.Attribute#getValue()
     */
    public String getValue() {
	return eiAttributeInst.getValue();
    }

    @Override
    public void setValue(String value) {
        this.eiAttributeInst.setValue(value);
    }

    /*
     * @see com.ds.bpm.client.Attribute#getParent()
     */
    @JSONField(serialize = false)
    
    public Attribute getParent() {
	EIAttributeInst eiAttribute = (EIAttributeInst) eiAttributeInst.getParent();
	return new AttributeInstProxy(eiAttribute, systemCode);
    }

    /*
     * @see com.ds.bpm.client.Attribute#getChildren()
     */
    @JSONField(serialize = false)
    
    public List<Attribute> getChildren() {
	List<EIAttribute> eiChildren = eiAttributeInst.getChildren();
	return new WorkflowListProxy(eiChildren, systemCode);
    }

    @Override
    public List<String> getChildrenIds() {
        List<String> childIds=new ArrayList<String>();
        List<EIAttribute> eiChildrens= eiAttributeInst.getChildren();
        for(EIAttribute child:eiChildrens){
            if (child instanceof EIAttributeInst){
                childIds.add(((EIAttributeInst)child).getId());
            }
        }
        return childIds;
    }

    @Override
    public void setChildrenIds(List<String> ids) {

    }

    /*
     * @see com.ds.bpm.client.Attribute#getChild(java.lang.String)
     */
    @JSONField(serialize = false)
    
    public Attribute getChild(String name) {
	EIAttributeInst eiAttribute = (EIAttributeInst) eiAttributeInst.getChild(name);
	return new AttributeInstProxy(eiAttribute, systemCode);
    }

}
