/**
 * $RCSfile: DefaultActivityListenerExpressionPar.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:44 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.event.ActivityEvent;
import net.ooder.bpm.client.event.ActivityListener;
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
public class DefaultActivityListenerExpressionPar implements ActivityListener {

	
	private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, DefaultProcessListenerExpressionPar.class);
	

	public void activityActived(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityActived".toUpperCase());
		
	}


	public void activityActiving(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityActiving".toUpperCase());
		
	}


	public void activityCompleted(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityCompleted".toUpperCase());
		
	}


	public void activityCompleting(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityCompleting".toUpperCase());
		
	}


	public void activityInited(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityInited".toUpperCase());
		
	}

	public void activityFormSaveed(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityFormSaveed".toUpperCase());
		
	}


	public void activityFormSaveing(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityFormSaveing".toUpperCase());
		
	}

	public void activityJoined(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityJoined".toUpperCase());
		
	}


	public void activityJoining(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityJoining".toUpperCase());
		
	}


	public void activityOutFlowReturned(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityOutFlowReturned".toUpperCase());
		
	}


	public void activityOutFlowReturning(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityOutFlowReturning".toUpperCase());
		
	}


	public void activityOutFlowed(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityOutFlowed".toUpperCase());
		
	}


	public void activityOutFlowing(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityOutFlowing".toUpperCase());
		
	}


	public void activityResumed(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityResumed".toUpperCase());
		
	}


	public void activityResuming(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityResuming".toUpperCase());
		
	}


	public void activityRouted(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityRouted".toUpperCase());
		
	}


	public void activityRouting(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityRouting".toUpperCase());
		
	}


	public void activitySplited(ActivityEvent event) throws BPMException {
			
		this.expressionPar(event, "activitySplited".toUpperCase());
		
	}


	public void activitySpliting(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activitySpliting".toUpperCase());
		
	}


	public void activitySuspended(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activitySuspended".toUpperCase());
		
	}


	public void activitySuspending(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activitySuspending".toUpperCase());
		
	}


	public void activityTakebacked(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityTakebacked".toUpperCase());
		
	}


	public void activityTakebacking(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityTakebacking".toUpperCase());
		
	}
	public void activityDisplay(ActivityEvent event) throws BPMException {
		this.expressionPar(event, "activityDisplay".toUpperCase());
		
	}

	

	public void expressionPar(ActivityEvent event,String methodName){		
		Listener listener =(Listener) event.getListener();	
		JDSContext context=JDSActionContext.getActionContext()	;		
		if (listener.getExpressionListenerType()
				.equals(ListenerEnums.EXPRESSIONLISENTERTYPE_EXPRESSION) 
				&& listener.getExpressionStr()!=null 
				&& listener.getListenerEvent().equals(ListenerEnums.ACTIVITY_LISTENER_EVENT)
				&& !listener.getExpressionStr().equals("")
				&& listener.getExpressionEventType().getMethod().equals(methodName)){
			try {
				logger.debug("expression =" +listener.getExpressionStr()+"  start "+methodName);	
				context.Par(listener.getExpressionStr());
				logger.debug("expression =" +listener.getExpressionStr()+"  end "+methodName);	
			} catch (Exception e) {
				logger.equals("expression =" +listener.getExpressionStr()+" par err when "+methodName);				
				
			}
		}
		
		
	}


	


	

}

