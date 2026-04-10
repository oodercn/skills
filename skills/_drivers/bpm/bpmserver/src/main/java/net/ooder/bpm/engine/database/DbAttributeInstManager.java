/**
 * $RCSfile: DbAttributeInstManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import net.ooder.bpm.engine.inter.EIAttributeInst;
import net.ooder.bpm.engine.inter.EIAttributeInstManager;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 鎵╁睍灞炴€у畾涔夌鐞嗘暟鎹簱瀹炵幇
 * </p>
 * <p>
 * 姝ょ鐞嗗櫒鎻愪緵鍒涘缓鍜屼繚瀛樻湇鍔?
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
public class DbAttributeInstManager extends EIAttributeInstManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIAttributeDefManager#createAttributeDef()
	 */
	public EIAttributeInst createAttributeInst() {
		return new DbAttributeInst();
	}

}


