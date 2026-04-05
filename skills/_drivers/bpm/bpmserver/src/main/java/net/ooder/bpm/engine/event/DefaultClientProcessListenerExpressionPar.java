/**
 * $RCSfile: DefaultClientProcessListenerExpressionPar.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.event.ProcessEvent;
import net.ooder.bpm.client.event.ProcessListener;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.event.ListenerEnums;
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
 * Description: 流程事件监听器表达式解析
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
public class DefaultClientProcessListenerExpressionPar implements ProcessListener {

	private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, DefaultClientProcessListenerExpressionPar.class);

	public void processAborted(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processAborted".toUpperCase());

	}

	public void processAborting(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processAborting".toUpperCase());

	}

	public void processCompleted(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processCompleted".toUpperCase());

	}

	public void processCompleting(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processCompleting".toUpperCase());

	}

	public void processDeleted(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processDeleted".toUpperCase());

	}

	public void processDeleting(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processDeleting".toUpperCase());

	}

	public void processResumed(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processResumed".toUpperCase());

	}

	public void processResuming(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "expressionPar".toUpperCase());

	}

	public void processSaved(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processSaved".toUpperCase());

	}

	public void processSaving(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processSaving".toUpperCase());

	}

	public void processStarted(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processStarted".toUpperCase());

	}

	public void processStarting(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processStarting".toUpperCase());

	}

	public void processSuspended(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processSuspended".toUpperCase());

	}

	public void processSuspending(ProcessEvent event) throws BPMException {
		this.expressionPar(event, "processSuspending".toUpperCase());

	}

	public void expressionPar(ProcessEvent event, String methodName) {

		Listener listener = (Listener) event.getListener();

		JDSContext context=JDSActionContext.getActionContext()	;			
		
//		Map<Object ,Object> contextRoot=ESBPar.getContextRoot();
//		contextRoot.put("$currProcessInst", event.getSource());

		if (listener.getExpressionListenerType().equals(
			ListenerEnums.EXPRESSIONLISENTERTYPE_EXPRESSION)
				&& listener.getExpressionStr() != null
				&& listener.getListenerEvent().equals(
					ListenerEnums.PROCESS_LISTENER_EVENT)
				&& !listener.getExpressionStr().equals("")
				&& listener.getExpressionEventType().getMethod().equals(
						methodName)) {
			try {
				context.Par(listener.getExpressionStr());
			} catch (Exception e) {
				logger.debug("expression =" + listener.getExpressionStr()
						+ " par err when " + methodName);

			}
		}
	}

}

