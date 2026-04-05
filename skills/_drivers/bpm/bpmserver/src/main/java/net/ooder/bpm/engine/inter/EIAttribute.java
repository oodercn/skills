/**
 * $RCSfile: EIAttribute.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
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
public interface EIAttribute {

	/**
	 * Getter method for propname
	 * 
	 * @return the value of propname
	 */
	public abstract String  getName();

	/**
	 * Setter method for propname
	 * 
	 * @param newVal
	 *            The new value to be assigned to propname
	 */
	public abstract void setName(String  newVal);

	/**
	 * Getter method for propvalue
	 * 
	 * @return the value of propvalue
	 */
	public abstract Object getInterpretedValue();

	public abstract String getValue();

	/**
	 * Setter method for propvalue
	 * 
	 * @param newVal
	 *            The new value to be assigned to propvalue
	 */
	public abstract void setValue(String newVal);

	public abstract EIAttribute getParent();

	public abstract void setParent(EIAttribute parent);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.process.EIAttributeDef#getChildren()
	 */
	public abstract List<EIAttribute> getChildren();

	public abstract EIAttribute getChild(String name);

	public abstract void addChild(EIAttribute child);

	// public abstract void removeChild(EIAttribute child);
	public abstract void clearChild();
}


