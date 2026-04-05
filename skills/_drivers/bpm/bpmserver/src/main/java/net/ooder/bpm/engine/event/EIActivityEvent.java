/**
 * $RCSfile: EIActivityEvent.java,v $
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
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.enums.event.ActivityEventEnums;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心活动事件
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
public class EIActivityEvent extends BPMEvent {

    public EIActivityEvent(EIActivityInst[] inst, ActivityEventEnums eventID) {
	super(inst, null);
	id = eventID;

    }

    public EIActivityEvent(EIActivityInst inst, ActivityEventEnums eventID) {
	super(new EIActivityInst[] { inst }, null);

	id = eventID;
    }

    @Override
    public ActivityEventEnums getID() {
	return (ActivityEventEnums) id;
    }


    /**
     * 取得触发此流程事件的一个或多个活动实例
     */
    public EIActivityInst[] getActivityInsts() {
	return (EIActivityInst[]) getSource();
    }

}

