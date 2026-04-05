/**
 * $RCSfile: EIProcessListener.java,v $
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
 * Description: 核心流程事件监听器接口
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
public interface EIProcessListener extends JDSListener {

	/**
	 * 流程实例正在被启动
	 */
	public void processStarting(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被启动
	 */
	public void processStarted(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被保存
	 */
	public void processSaving(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被保存
	 */
	public void processSaved(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被挂起
	 */
	public void processSuspending(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被挂起
	 */
	public void processSuspended(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被恢复（从挂起状态）
	 */
	public void processResuming(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被恢复（从挂起状态）
	 */
	public void processResumed(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被取消
	 */
	public void processAborting(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被取消
	 */
	public void processAborted(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被完成
	 */
	public void processCompleting(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被完成
	 */
	public void processCompleted(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被删除
	 */
	public void processDeleting(EIProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被删除
	 */
	public void processDeleted(EIProcessEvent event) throws BPMException;

}


