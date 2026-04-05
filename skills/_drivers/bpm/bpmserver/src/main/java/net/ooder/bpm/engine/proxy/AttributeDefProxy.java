/**
 * $RCSfile: AttributeDefProxy.java,v $
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
import net.ooder.bpm.client.Attribute;
import net.ooder.bpm.client.AttributeDef;
import net.ooder.bpm.engine.inter.EIAttribute;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.common.CommonYesNoEnum;
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
 * Description: 扩展属性定义客户端接口的代理实现 （包括流程定义、活动定义和路由定义的扩展属性）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * <
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class AttributeDefProxy implements AttributeDef, Serializable {

    @JSONField(serialize = false)
    
    private EIAttributeDef eiAttributeDef;
    private String systemCode;

    public AttributeDefProxy(EIAttributeDef eiAttributeDef, String systemCode) {
        super();
        this.systemCode = systemCode;

        this.eiAttributeDef = eiAttributeDef;
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getId()
     */
    public String getId() {
        return eiAttributeDef.getId();
    }

    @Override
    public void setId(String id) {
        this.eiAttributeDef.setId(id);
    }


    /*
     * @see com.ds.bpm.client.AttributeDef#getName()
     */
    public String getName() {

        return eiAttributeDef.getName();
    }

    @Override
    public void setName(String name) {
        this.eiAttributeDef.setName(name);
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getInterpretedValue()
     */
    @Override
    @JSONField(serialize = false)
    
    public Object getInterpretedValue() {
        return eiAttributeDef.getInterpretedValue();
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getValue()
     */
    public String getValue() {
        return eiAttributeDef.getValue();
    }

    @Override
    public void setValue(String value) {
        this.eiAttributeDef.setValue(value);
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getInterpretClass()
     */
    @Override
    public AttributeInterpretClass getInterpretClass() {
        return AttributeInterpretClass.fromType(eiAttributeDef.getInterpretClass());
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getType()
     */
    public Attributetype getType() {
        return Attributetype.fromType(eiAttributeDef.getType());
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getParentId()
     */
    public String getParentId() {
        return eiAttributeDef.getParentId();
    }

    @Override
    public void setParentId(String parentId) {
        this.eiAttributeDef.setParentId(parentId);
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getIsExtension()
     */
    public Integer getIsExtension() {
        return eiAttributeDef.getIsExtension();
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getCanInstantiate()
     */
    public CommonYesNoEnum getCanInstantiate() {
        return CommonYesNoEnum.fromType(eiAttributeDef.getCanInstantiate());
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getParent()
     */
    @Override
    @JSONField(serialize = false)
    
    public Attribute getParent() {
        EIAttributeDef parentEIAttributeDef = (EIAttributeDef) eiAttributeDef.getParent();
        return new AttributeDefProxy(parentEIAttributeDef, systemCode);
    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getChildren()
     */
    @Override
    @JSONField(serialize = false)
    
    public List<Attribute> getChildren() {
        List<EIAttribute> childrenList = eiAttributeDef.getChildren();
        return new WorkflowListProxy(childrenList, systemCode);
    }

    @Override
    public List<String> getChildrenIds() {
        List<String> childIds = new ArrayList<String>();
        List<EIAttribute> eiChildrens = eiAttributeDef.getChildren();
        for (EIAttribute child : eiChildrens) {
            if (child instanceof EIAttributeDef) {
                childIds.add(((EIAttributeDef) child).getId());
            }
        }
        return childIds;
    }

    @Override
    public void setChildrenIds(List<String> ids) {

    }

    /*
     * @see com.ds.bpm.client.AttributeDef#getChild(java.lang.String)
     */
    @Override
    @JSONField(serialize = false)
    
    public Attribute getChild(String name) {
        EIAttributeDef childEIAttributeDef = (EIAttributeDef) eiAttributeDef.getChild(name);
        return new AttributeDefProxy(childEIAttributeDef, systemCode);
    }


}
