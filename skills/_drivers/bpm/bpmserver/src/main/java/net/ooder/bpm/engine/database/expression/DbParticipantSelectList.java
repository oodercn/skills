/**
 * $RCSfile: DbParticipantSelectList.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.expression;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ooder.bpm.engine.database.DbWorkflowList;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2003-2004
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lxl
 * @version 1.0
 */
public class DbParticipantSelectList extends DbWorkflowList {

	private static int PREFETCH_SIZE = 30;

	int prefetchIndex = 0;

	/**
	 * use default constructor
	 */
	public DbParticipantSelectList() {
		super();
	}

	/**
	 * use default constructor
	 * 
	 * @param c
	 */
	public DbParticipantSelectList(Collection c) {
		super(c);
	}

	public DbParticipantSelectList(DbParticipantSelect[] acts) {
		size = acts.length;

		elementData = new Object[size];
		System.arraycopy(acts, 0, elementData, 0, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.database.DbWorkflowList#getWorkflowObject(java.lang.Object)
	 */
	protected Object getWorkflowObject(Object obj) {
		if (obj instanceof DbParticipantSelect) {
			DbParticipantSelect participant = (DbParticipantSelect) obj;
			try {
				return DbParticipantSelectManager.getInstance().loadByKey(
						participant.getParticipantSelectId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
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
			DbParticipantSelect participant = (DbParticipantSelect) elementData[prefetchIndex];
			String uuid = participant.getParticipantSelectId();
			v.add(uuid);
		}
		DbParticipantSelectManager participantManager = (DbParticipantSelectManager) DbParticipantSelectManager
				.getInstance();
		participantManager.prepareCache(v);
	}

}


