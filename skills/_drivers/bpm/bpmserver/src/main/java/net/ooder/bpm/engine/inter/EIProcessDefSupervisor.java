/*
 * Created on 2004-3-5
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.ooder.bpm.engine.inter;

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
 * Copyright: Copyright (c) 2006-2004
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public interface EIProcessDefSupervisor {

	/**
	 * Getter method for processdefId
	 * 
	 * @return the value of processdefId
	 */
	public abstract String getProcessDefId();

	/**
	 * Setter method for processdefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefId
	 */
	public abstract void setProcessDefId(String newVal);

	/**
	 * Getter method for processdefVersionId
	 * 
	 * @return the value of processdefVersionId
	 */
	public abstract String getProcessDefVersionId();

	/**
	 * Setter method for processdefVersionId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefVersionId
	 */
	public abstract void setProcessDefVersionId(String newVal);

	/**
	 * Getter method for supervisorId
	 * 
	 * @return the value of supervisorId
	 */
	public abstract String getSupervisorId();

	/**
	 * Setter method for supervisorId
	 * 
	 * @param newVal
	 *            The new value to be assigned to supervisorId
	 */
	public abstract void setSupervisorId(String newVal);

	/**
	 * Getter method for supervisorName
	 * 
	 * @return the value of supervisorName
	 */
	public abstract String getSupervisorName();

	/**
	 * Setter method for supervisorName
	 * 
	 * @param newVal
	 *            The new value to be assigned to supervisorName
	 */
	public abstract void setSupervisorName(String newVal);

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public abstract void copy(EIProcessDefSupervisor bean);
}
