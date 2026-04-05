/**
 * $RCSfile: ActivityInstExpressFilterByDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.query;

import java.util.List;

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动定义对象过滤器
 * </p>
 * 此类为抽象类，在查询活动定义时使用的过滤器必须继承此类，否则将被忽略。
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public  class ActivityInstExpressFilterByDef extends AbstractFilter {

	static Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, ActivityInstExpressFilterByDef.class);
	
	
	public boolean experssPar(String expressStr) {
		JDSContext context=JDSActionContext.getActionContext()	;	
		if (expressStr == null || expressStr.equals("")) {
			return true;
		}
		Object result=true;
		try {
			result=context.Par(expressStr,boolean.class);
			
		
		} catch (Exception e) {
			log.warn("Route Condition parser error : " );
			return false;
		}

			if (result instanceof Boolean) {
				return ((Boolean) result).booleanValue();
			} else {
				return true;
			}
	}




	public boolean filterObject(Object obj,String systemCode) {
		
			EIActivityInst inst=(EIActivityInst) obj;
			List listenerList=null;
			try {
				listenerList = inst.getActivityDef().getListeners();
			} catch (BPMException e) {
				e.printStackTrace();
			}
			for(int k=0;k<listenerList.size();k++){
				 Listener listener=(Listener) listenerList.get(k);
				 if (listener.getExpressionListenerType().equals("filter")){
					return experssPar(listener.getExpressionStr());
					 
				 }
			}
				return true;
		
	}
	
	

}


