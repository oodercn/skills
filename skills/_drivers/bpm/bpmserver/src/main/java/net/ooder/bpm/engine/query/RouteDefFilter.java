/**
 * $RCSfile: RouteDefFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.query;

import net.ooder.bpm.client.RouteDef;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.proxy.RouteDefProxy;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由定义对象过滤器
 * </p>
 * 此类为抽象类，在查询路由定义时使用的过滤器必须继承此类，否则将被忽略。
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
public abstract class RouteDefFilter extends AbstractFilter {

	public boolean filterObject(Object obj,String systemCode) {
		if (obj instanceof EIRouteDef) {
			return filterRouteDef(new RouteDefProxy((EIRouteDef) obj,systemCode));
		} else {
			return true;
		}
	}

	/**
	 * 抽象方法，继承类必须实现此方法来过滤活动定义！
	 * 
	 * @see net.ooder.bpm.engine.query.Filter#filterObject(java.lang.Object)
	 */
	public abstract boolean filterRouteDef(RouteDef obj);

}


