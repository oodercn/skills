/**
 * $RCSfile: DbAttributeDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */

package net.ooder.bpm.engine.database;

import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.bpm.engine.attribute.InterpreterManager;
import net.ooder.bpm.engine.inter.EIAttribute;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_PROCESSDEF_PROPERTY
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lxl
 * @version 1.0
 */
public class DbAttributeDef<T> implements EIAttributeDef, Cacheable, Serializable {
	private String id;

	private String name;
	

	String value;

	private String interpretClass;

	private String type=Attributetype.CUSTOMIZE.getType();

	private String parentId;

	private Integer isExtension=0;

	private String canInstantiate;

	private boolean _isNew = true;

	private EIAttributeDef parent = null;

	private Map<String,EIAttribute> children = new HashMap<String,EIAttribute>();

	/**
	 */
	DbAttributeDef() {
	}

	/**
	 * Getter method for propertyId
	 * 
	 * @return the value of propertyId
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter method for propertyId
	 * 
	 * @param newVal
	 *            The new value to be assigned to propertyId
	 */
	public void setId(String newVal) {
		if ((newVal != null && newVal.equals(this.id) == true)
				|| (newVal == null && this.id == null))
			return;
		this.id = newVal;
	}

	/**
	 * Getter method for propname
	 * 
	 * @return the value of propname
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for propname
	 * 
	 * @param newVal
	 *            The new value to be assigned to propname
	 */
	public void setName(String newVal) {
	 
		if ((newVal != null && newVal.equals(this.name) == true)
				|| (newVal == null && this.name == null))
			return;
		if (newVal != null) {
			//this.name = newVal.toUpperCase();
			this.name = newVal;
		} else {
			this.name = null;
		}
	}

	/**
	 * Getter method for propvalue
	 * 
	 * @return the value of propvalue
	 */
	public T getInterpretedValue() {
		AttributeInterpreter<T> interpreter = InterpreterManager.getInstance()
				.getInterpreter(interpretClass);
		return interpreter.interpret(value);
	}

	public String getValue() {
		return value;
	}

	/**
	 * Setter method for propvalue
	 * 
	 * @param newVal
	 *            The new value to be assigned to propvalue
	 */
	public void setValue(String newVal) {
		if ((newVal != null && newVal.equals(this.value) == true)
				|| (newVal == null && this.value == null))
			return;
		this.value = newVal;
	}

	/**
	 * Getter method for propclass
	 * 
	 * @return the value of propclass
	 */
	public String getInterpretClass() {
		return interpretClass;
	}

	/**
	 * Setter method for propclass
	 * 
	 * @param newVal
	 *            The new value to be assigned to propclass
	 */
	public void setInterpretClass(String newVal) 
	
	{
	
		if ((newVal != null && newVal.equals(this.interpretClass) == true)
				|| (newVal == null && this.interpretClass == null))
			return;
		this.interpretClass = newVal;
	}

	/**
	 * Getter method for proptype
	 * 
	 * @return the value of proptype
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter method for proptype
	 * 
	 * @param newVal
	 *            The new value to be assigned to proptype
	 */
	public void setType(String newVal) {
	
		if ((newVal != null && newVal.equals(this.type) == true)
				|| (newVal == null && this.type == null))
			return;
		if (newVal != null) {
			this.type = newVal.toUpperCase();
		} else {
			this.type = null;
		}

	}

	/**
	 * Getter method for parentpropId
	 * 
	 * @return the value of parentpropId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Setter method for parentpropId
	 * 
	 * @param newVal
	 *            The new value to be assigned to parentpropId
	 */
	public void setParentId(String newVal) {
		if ((newVal != null && newVal.equals(this.parentId) == true)
				|| (newVal == null && this.parentId == null))
			return;
		this.parentId = newVal;
	}

	/**
	 * Getter method for isextension
	 * 
	 * @return the value of isextension
	 */
	public Integer getIsExtension() {
		return isExtension;
	}

	/**
	 * Setter method for isextension
	 * 
	 * @param newVal
	 *            The new value to be assigned to isextension
	 */
	public void setIsExtension(Integer newVal) {
		if (newVal == this.isExtension)
			return;
		this.isExtension = newVal;
	}

	/**
	 * Getter method for caninstantiate
	 * 
	 * @return the value of caninstantiate
	 */
	public String getCanInstantiate() {
		return canInstantiate;
	}

	/**
	 * Setter method for caninstantiate
	 * 
	 * @param newVal
	 *            The new value to be assigned to caninstantiate
	 */
	public void setCanInstantiate(String newVal) {
	  
		if ((newVal != null && newVal.equals(this.canInstantiate) == true)
				|| (newVal == null && this.canInstantiate == null))
			return;
		this.canInstantiate = newVal;
	}

	public EIAttribute getParent() {
		return parent;
	}

	public void setParent(EIAttribute parent) {
		this.parent = (EIAttributeDef) parent;
		this.setParentId(this.parent.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.process.EIAttributeDef#getChildren()
	 */
	public List getChildren() {
		List list = new ArrayList(children.values());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIAttributeDef#getChild(java.lang.String)
	 */
	public EIAttribute getChild(String name) {
		return children.get(name);
	}

	public void addChild(EIAttribute child) {

		children.put(child.getName(), child);
		child.setParent(this);
	}

	public void clearChild() {
		children.clear();
	}

	/**
	 * Determine if the current object is new or not
	 * 
	 * @return true if the current object is new, false if the object is not new
	 */
	public boolean isNew() {
		return _isNew;
	}

	/**
	 * Specify to the object if he has to been set as new or not
	 * 
	 * @param isNew
	 *            the boolean value to be assigned to the isNew field
	 */
	public void setIsNew(boolean isNew) {
		this._isNew = isNew;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbAttributeDef bean) {
		setId(bean.getId());
		setName(bean.getName());
		setValue(bean.getValue());
		setInterpretClass(bean.getInterpretClass());
		setType(bean.getType());
		setParentId(bean.getParentId());
		setIsExtension(bean.getIsExtension());
		setCanInstantiate(bean.getCanInstantiate());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_PROCESSDEF_PROPERTY] "
				+ "\n - BPM_PROCESSDEF_PROPERTY.PROPERTY_ID = "
				+ (id != null ? ("[" + id.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.PROPNAME = "
				+ (name != null ? ("[" + name.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.PROPVALUE = "
				+ (value != null ? ("[" + value.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.PROPCLASS = "
				+ (interpretClass != null ? ("[" + interpretClass.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.PROPTYPE = "
				+ (type != null ? ("[" + type.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.PARENTPROP_ID = "
				+ (parentId != null ? ("[" + parentId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.ISEXTENSION = "
				+ (true ? ("[" + isExtension + "]") : "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_PROPERTY.CANINSTANTIATE = "
				+ (canInstantiate != null ? ("[" + canInstantiate.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {

		int size = 0;
		size += CacheSizes.sizeOfString(id);
		size += CacheSizes.sizeOfString(name);
		size += CacheSizes.sizeOfString(value);
		size += CacheSizes.sizeOfString(interpretClass);
		size += CacheSizes.sizeOfString(type);
		
		size += CacheSizes.sizeOfString(parentId);
		size += CacheSizes.sizeOfInt();
		size += CacheSizes.sizeOfString(parentId);

		return size;
	}

	

}


