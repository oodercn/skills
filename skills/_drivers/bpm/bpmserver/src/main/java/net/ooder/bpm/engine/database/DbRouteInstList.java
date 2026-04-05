/**
 * $RCSfile: DbRouteInstList.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIRouteInst;
import net.ooder.bpm.engine.inter.EIRouteInstManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义数据List
 * </p>
 * <p>
 * 可以根据需要动态从数据库中读取流程定义对象
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public class DbRouteInstList extends DbWorkflowList {

	private static int PREFETCH_SIZE = 30;

	int prefetchIndex = 0;

	/**
	 * use default constructor
	 */
	public DbRouteInstList() {
		super();
	}

	/**
	 * use default constructor
	 * 
	 * @param c
	 */
	public DbRouteInstList(Collection c) {
		super(c);
	}

	public DbRouteInstList(DbRouteInst[] routes) {
		size = routes.length;

		elementData = new Object[size];
		System.arraycopy(routes, 0, elementData, 0, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.database.DbWorkflowList#getWorkflowObject(java.lang.Object)
	 */
	protected Object getWorkflowObject(Object obj) {
		DbRouteInst routeInst = (DbRouteInst) obj;
		try {
			EIRouteInst result = EIRouteInstManager.getInstance().loadByKey(
					routeInst.getRouteInstId());
			return result;
		} catch (BPMException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 这里假设读取操作都是从前向后进行的，所以预读方向都是向后的
	 */
	protected void prepareGet(int index) {
		if (index >= prefetchIndex) {
			// the prefetched objects used up , need load more
			prefetch();
		}
	}

	/**
	 * prefetch the object into cache
	 */
	private void prefetch() {
		int length = prefetchIndex + PREFETCH_SIZE;
		List v = new ArrayList();
		for (; prefetchIndex < length; prefetchIndex++) {
			if (prefetchIndex >= size()) {
				break;
			}
			EIRouteInst routeInst = (EIRouteInst) elementData[prefetchIndex];
			String uuid = routeInst.getRouteInstId();
			v.add(uuid);
		}
		DbRouteInstManager defManager = (DbRouteInstManager) EIRouteInstManager
				.getInstance();
		defManager.prepareCache(v);
	}

}


