/**
 * $RCSfile: EIRightEvent.java,v $
 * $Revision: 1.1 $
 * $Date: 2016/01/23 16:29:55 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.client.event.BPMEvent;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.enums.event.RightEventEnums;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心权限事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class EIRightEvent extends BPMEvent {

    public EIRightEvent(EIActivityInst[] inst, RightEventEnums event) {
	super(inst, null);

	id = event;
    }

    @Override
    public RightEventEnums getID() {
	return (RightEventEnums) id;
    }

    public EIRightEvent(EIActivityInst inst, RightEventEnums event) {
	super(new EIActivityInst[] { inst }, null);

	id = event;
    }

    /**
     * 取得触发此流程事件的一个或多个活动实例
     */
    public EIActivityInst[] getActivityInsts() {
	return (EIActivityInst[]) getSource();
    }

}

