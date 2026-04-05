/**
 * $RCSfile: FilterChain.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.query;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 查询结果过滤器的链调用实现。
 * </p>
 * 此实现类本身不做任何判断和过滤，仅仅提供一个过滤器链的载体。
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
public class FilterChain extends AbstractFilter {

	/**
	 * 进行链过滤操作
	 * 
	 * @param obj
	 * @return
	 */
	public boolean filterObject(Object obj,String systemCode) {
		return processChildFilter(obj,systemCode);
	}
}

