/**
 * $RCSfile: ListenerProxy.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.proxy;

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.bpm.enums.event.*;
import net.ooder.annotation.EventEnums;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 客户端监听器接口的代理实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class ListenerProxy implements Listener, Serializable {

    private EIListener eiListener;
    private String systemCode;

    public ListenerProxy(EIListener eiListener, String systemCode) {
        this.systemCode = systemCode;
        this.eiListener = eiListener;
    }

    /*
     * @see com.ds.bpm.client.Listener#getListenerId()
     */
    public String getListenerId() {
        return eiListener.getListenerId();
    }

    /*
     * @see com.ds.bpm.client.Listener#getListenerName()
     */
    public String getListenerName() {
        return eiListener.getListenerName();
    }

    /*
     * @see com.ds.bpm.client.Listener#getListenerEvent()
     */
    public ListenerEnums getListenerEvent() {
        ListenerEnums listenerType = ListenerEnums.ACTIVITY_LISTENER_EVENT;
        if (eiListener.getListenerEvent() != null && !eiListener.getListenerEvent().equals("")) {
            listenerType = ListenerEnums.fromType(eiListener.getListenerEvent());
        }

        return listenerType;
    }

    /*
     * @see com.ds.bpm.client.Listener#getRealizeClass()
     */
    public String getRealizeClass() {
        return eiListener.getRealizeClass();
    }

    public EventEnums getExpressionEventType() {
        ListenerEnums listenerType = getListenerEvent();
        switch (listenerType) {
            case ACTIVITY_LISTENER_EVENT:
                return ActivityEventEnums.fromMethod(eiListener.getExpressionEventType());
            case PROCESS_LISTENER_EVENT:
                return ProcessEventEnums.fromMethod(eiListener.getExpressionEventType());
            case RIGHT_LISTENER_EVENT:
                return RightEventEnums.fromMethod(eiListener.getExpressionEventType());
            default:
                break;
        }
        return null;
    }

    public ListenerTypeEnums getExpressionListenerType() {
        ListenerTypeEnums listenerTypeEnums = ListenerTypeEnums.ExpressionEventType;

        if (eiListener.getExpressionListenerType() != null && !eiListener.getExpressionListenerType().equals("")) {
            listenerTypeEnums = ListenerTypeEnums.fromType(eiListener.getExpressionListenerType());
        }
        return listenerTypeEnums;
    }

    public String getExpressionStr() {
        return eiListener.getExpressionStr();
    }

}
