/**
 * $RCSfile: DbProcessDefVersionRight.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.right;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认权限模型使用的流程权限定义数据封装类
 * </p>
 * <p>
 * 此类数据来自于流程监控人表和流程权限组表
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
public class DbProcessDefVersionRight implements Serializable {

	private String processDefVersionId = null;

	private List rightGroups = null;

	private List supervisors = null;

	public DbProcessDefVersionRight() {

	}

	/**
	 * @return Returns the processDefVersionId.
	 */
	public String getProcessDefVersionId() {
		return processDefVersionId;
	}

	/**
	 * @param processDefVersionId
	 *            The processDefVersionId to set.
	 */
	public void setProcessDefVersionId(String processDefVersionId) {
		this.processDefVersionId = processDefVersionId;
	}

	/**
	 * @return Returns the rightGroup.
	 */
	public List getRightGroups() {
		return rightGroups;
	}

	/**
	 * @param rightGroup
	 *            The rightGroup to set.
	 */
	public void setRightGroups(List rightGroup) {
		this.rightGroups = rightGroup;
	}

	/**
	 * @return Returns the supervisors.
	 */
	public List getSupervisors() {
		return supervisors;
	}

	/**
	 * @param supervisors
	 *            The supervisors to set.
	 */
	public void setSupervisors(List supervisors) {
		this.supervisors = supervisors;
	}

}

