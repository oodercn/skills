/**
 * $RCSfile: EIAttributeDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
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
public interface EIAttributeDef<T> extends EIAttribute {

	/**
	 * Getter method for propertyId
	 * 
	 * @return the value of propertyId
	 */
	public abstract String getId();

	/**
	 * Setter method for propertyId
	 * 
	 * @param newVal
	 *            The new value to be assigned to propertyId
	 */
	public abstract void setId(String newVal);

	/**
	 * Getter method for propclass
	 * 
	 * @return the value of propclass
	 */
	public abstract String getInterpretClass();

	/**
	 * Setter method for propclass
	 * 
	 * @param newVal
	 *            The new value to be assigned to propclass
	 */
	public abstract void setInterpretClass(String newVal);

	/**
	 * Getter method for proptype
	 * 
	 * @return the value of proptype
	 */
	public abstract String getType();

	/**
	 * Setter method for proptype
	 * 
	 * @param newVal
	 *            The new value to be assigned to proptype
	 */
	public abstract void setType(String newVal);

	/**
	 * Getter method for parentpropId
	 * 
	 * @return the value of parentpropId
	 */
	public abstract String getParentId();

	/**
	 * Setter method for parentpropId
	 * 
	 * @param newVal
	 *            The new value to be assigned to parentpropId
	 */
	public abstract void setParentId(String newVal);

	/**
	 * Getter method for isextension
	 * 
	 * @return the value of isextension
	 */
	public abstract Integer getIsExtension();

	/**
	 * Setter method for isextension
	 * 
	 * @param newVal
	 *            The new value to be assigned to isextension
	 */
	public abstract void setIsExtension(Integer newVal);

	/**
	 * Getter method for caninstantiate
	 * 
	 * @return the value of caninstantiate
	 */
	public abstract String getCanInstantiate();

	/**
	 * Setter method for caninstantiate
	 * 
	 * @param newVal
	 *            The new value to be assigned to caninstantiate
	 */
	public abstract void setCanInstantiate(String newVal);

}
