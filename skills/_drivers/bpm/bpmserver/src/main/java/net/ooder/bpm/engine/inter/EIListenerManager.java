/**
 * $RCSfile: EIListenerManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import net.ooder.bpm.engine.database.DbListenerManager;

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
 * @author wenzhangli
 * @version 2.0
 */
public abstract class EIListenerManager {
	private static EIListenerManager singleton = new DbListenerManager();

	synchronized public static EIListenerManager getInstance() {
		return singleton;
	}

	synchronized public static void setInstance(EIListenerManager instance) {
		singleton = instance;
	}

	public abstract EIListener createListener();
}

