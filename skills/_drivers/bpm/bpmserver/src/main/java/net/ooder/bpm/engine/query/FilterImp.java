/**
 * $RCSfile: FilterImp.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.query;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.common.Filter;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 查询结果过滤器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public class FilterImp implements Filter {

	/**
	 * 应用应该实现的过滤方法。
	 * 
	 * @param obj
	 *            需要过滤的对象
	 * @return
	 */
	public  boolean filterObject(Object obj,String systemCode){
		EIActivityInst eac=(EIActivityInst) obj;
		String activityDefName=null;
		try {
			activityDefName = eac.getActivityDef().getName();
		} catch (BPMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			return activityDefName.equals("不同意");
		
		
		
		
	}
}


