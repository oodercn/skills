/**
 * $RCSfile: EIProcessEvent.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.client.event.BPMEvent;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.enums.event.ProcessEventEnums;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心流程事件
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
public class EIProcessEvent extends BPMEvent {

    public EIProcessEvent(EIProcessInst inst, ProcessEventEnums eventID) {
	super(inst, null);

	id = eventID;
    }

    public ProcessEventEnums getID() {
	return (ProcessEventEnums) id;
    }

    /**
     * 取得触发此流程事件的流程实例
     */
    public EIProcessInst getProcessInst() {
	return (EIProcessInst) getSource();
    }
}

