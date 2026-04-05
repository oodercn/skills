/**
 * $RCSfile: EIAttributeInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

/** @stereotype interface */
public interface EIAttributeInst extends EIAttribute {
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

	// public abstract String getActivityInstId();
	//
	// public abstract void setActivityInstId(String newVal);

	public abstract void setInterpretedValue(Object o);

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
}

