/**
 * $RCSfile: dayTimeControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:54 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import java.util.Iterator;
import java.util.List;

import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIActivityInstManager;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation;
import net.ooder.bpm.enums.activityinst.ActivityInstRunStatus;
import net.ooder.bpm.enums.activityinst.ActivityInstStatus;
import net.ooder.bpm.enums.process.ProcessInstRunStatus;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.common.database.DBBeanBase;
import net.ooder.common.database.DBResult;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 时间控制类，用于执行控制流程，活动超过 指定时限时执行自动操作。
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 1.0
 */
public class DayTimeControl implements Runnable {

	private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, DayTimeControl.class);

	public void run() {
		try {
			updateProcessInsts();
			updateActivityInsts();
		} catch (BPMException e) {
			logger.error("", e);
		}
	}

	private synchronized void updateProcessInsts() throws BPMException {
		DBBeanBase dbBean = new DBBeanBase(BPMConstants.CONFIG_KEY);
		DBResult dbResult = null;
		String where = "  select LIMITTIME where PROCESSINST_STATE in ('"
				+ ProcessInstStatus.notStarted.getType() + "','"
				+ ProcessInstStatus.running.getType() + "','"
				+ ProcessInstStatus.suspended.getType() + "') and RUNSTATUS='"
				+ ProcessInstRunStatus.NORMAL.getType() + "'" + " and LIMITTIME<"
				+ System.currentTimeMillis();
		try {
			int nResult;
			dbBean.prepareSql(where);
			nResult = dbBean.executePreparedQuery();
			if (nResult == -1) {
				System.err.println("error !");
				return;
			}
			dbResult = dbBean.getSelectDBResult();

		} catch (Exception e) {
			System.err.println("error !");
			return;
		} finally {
			dbBean.close();
		}

		// if (DateUtility.simpleCompareDate(endTime,currentTime)==1);
		List procInsts = EIProcessInstManager.getInstance().loadByWhere(where);
		for (Iterator it = procInsts.iterator(); it.hasNext();) {
			EIProcessInst inst = (EIProcessInst) it.next();
			// 设置为延期
			inst.setRunStatus(ProcessInstRunStatus.DELAY.getType());
		}
	}

	private synchronized void updateActivityInsts() throws BPMException {
		String where = " where ACTIVITYINST_STATE in ('"
				+ ActivityInstStatus.notStarted.getType() + "','"
				+ ActivityInstStatus.running.getType()  + "','"
				+ ActivityInstStatus.suspended.getType() + "') and RUNSTATUS='"
				+ ActivityInstRunStatus.NORMAL.getType() + "'" + " and LIMITTIME<"
				+ System.currentTimeMillis();
		List insts = EIActivityInstManager.getInstance().loadByWhere(where);
		// System.out.print(where);
		for (Iterator it = insts.iterator(); it.hasNext();) {
			EIActivityInst inst = (EIActivityInst) it.next();
			String deadlineOper = ActivityDefDeadLineOperation.DELAY.getType();
			/*
			 * if ((inst.getActivityDef().getDeadlineOperation())!=null){
			 * System.out.print("aaaaaaaaaaaaaaaaaaaaaaaaa"); deadlineOper =
			 * inst.getActivityDef().getDeadlineOperation(); }
			 */
			// 过期处理
			if (deadlineOper != null) {
				// 默认办理
				if (deadlineOper.equals(ActivityDefDeadLineOperation.DEFAULT.getType())) {
				}
				// 延期办理
				if (deadlineOper.equals(ActivityDefDeadLineOperation.DELAY.getType())) {
					// 设置为延期
					inst.setRunStatus(ActivityInstRunStatus.DELAY.getType());
				}
//				// 如果能自动收回则收回
//				WorkflowEngine engine = WorkflowEngine.getEngine();
//				if (deadlineOper.equals(ActivityDef.DEADLINEOPERATION_TAKEBACK)
//						&& engine.canTakeBack(inst.getActivityInstId())) {
//					engine.tackBack(inst.getActivityInstId());
//				}
				// 代办人自动办理
				if (deadlineOper
						.equals(ActivityDefDeadLineOperation.SURROGATE.getType())) {
				}
			}
		}
	}
}


