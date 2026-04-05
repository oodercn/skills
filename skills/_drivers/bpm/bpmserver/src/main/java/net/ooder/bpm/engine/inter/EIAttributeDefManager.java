/**
 * $RCSfile: EIAttributeDefManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import net.ooder.bpm.engine.database.DbAttributeDefManager;

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
public abstract class EIAttributeDefManager {

	private static EIAttributeDefManager singleton = new DbAttributeDefManager();

	/**
	 * Get the BPMActivitydefManager singleton
	 * 
	 * @return BPMActivitydefManager
	 */
	synchronized public static EIAttributeDefManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own BPMActivitydefManager instance, this is optional. By default
	 * we provide it for you.
	 */
	synchronized public static void setInstance(EIAttributeDefManager instance) {
		singleton = instance;
	}

	public abstract EIAttributeDef createAttributeDef();
}

