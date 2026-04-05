/**
 * $RCSfile: DbListenerManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.engine.inter.EIListenerManager;

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
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class DbListenerManager extends EIListenerManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIListenerManager#createListener()
	 */
	public EIListener createListener() {
		return new DbListener();
	}

}


