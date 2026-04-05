/**
 * $RCSfile: EIActivityListener.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:44 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.engine.BPMException;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心活动事件监听器
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
public interface EIActivityListener extends JDSListener {

	/**
	 * 活动初始化完毕，进入inactive状态
	 */
	public void activityInited(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始执行路由操作
	 */
	public void activityRouting(EIActivityEvent event) throws BPMException;

	/**
	 * 活动完成路由操作
	 */
	public void activityRouted(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始被激活(进入active状态)
	 */
	public void activityActiving(EIActivityEvent event) throws BPMException;

	/**
	 * 活动完成激活(进入active状态)
	 */
	public void activityActived(EIActivityEvent event) throws BPMException;
	
	/**
	 * 活动完成开始保存表单数据(进入保存状态)
	 */
	public void activityFormSaveing(EIActivityEvent event) throws BPMException;

	/**
	 * 活动完成保存表单数据完毕(保存完毕)
	 */
	public void activityFormSaveed(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始执行路由分裂
	 */
	public void activitySpliting(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经分裂为多个活动实例
	 */
	public void activitySplited(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始执行合并操作
	 */
	public void activityJoining(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经完成合并操作
	 */
	public void activityJoined(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始跳转到其他流程上
	 */
	public void activityOutFlowing(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经跳转到其他流程上
	 */
	public void activityOutFlowed(EIActivityEvent event) throws BPMException;

	/**
	 * 外流活动开始返回
	 */
	public void activityOutFlowReturning(EIActivityEvent event)
			throws BPMException;

	/**
	 * 外流活动完成返回
	 */
	public void activityOutFlowReturned(EIActivityEvent event)
			throws BPMException;

	/**
	 * 活动开始挂起
	 */
	public void activitySuspending(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经挂起
	 */
	public void activitySuspended(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始恢复
	 */
	public void activityResuming(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经恢复
	 */
	public void activityResumed(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始完成
	 */
	public void activityCompleting(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经完成
	 */
	public void activityCompleted(EIActivityEvent event) throws BPMException;

	/**
	 * 活动开始收回
	 */
	public void activityTakebacking(EIActivityEvent event) throws BPMException;

	/**
	 * 活动已经收回
	 */
	public void activityTakebacked(EIActivityEvent event) throws BPMException;
	/**
	 * 活动开始展示
	 */
	public void activityDisplay(EIActivityEvent event) throws BPMException;
}


