/**
 * $RCSfile: AbstractFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.query;

import net.ooder.common.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 查询结果过滤器实现，用于对引擎内部的查询结果 进行对象级的再次过滤。
 * </p>
 * 此抽象类实现了一个过滤器链，每个继承此类的实现类都可以具有过滤器链的功能。
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public abstract class AbstractFilter implements Filter {

	protected List childFilterList = new ArrayList();

	/**
	 * 添加下级过滤器， 实现类需要在FilterObject方法中调用 <code>processChildFilter()</code>方法才能使下级过滤器正常运行
	 * 
	 * @param filter
	 */
	public void addFilter(Filter filter) {
		if (filter != null) {
			childFilterList.add(filter);
		}
	}

	/**
	 * 调用下级过滤器，实现类不需要覆盖此方法， 在实现filterObject()方法时调用此方法即可。
	 * 
	 * @param obj
	 * @return
	 */
	protected boolean processChildFilter(Object obj,String systemCode) {
		boolean result = true;
		for (int i = 0; i < childFilterList.size(); i++) {
			Filter filter = (Filter) childFilterList.get(i);
			if (!filter.filterObject(obj,systemCode)) {
				result = false;
				break;
			}
		}
		return result;
	}

}


