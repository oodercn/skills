/**
 * $RCSfile: BPMEventControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/01/23 16:29:55 $
 * <p>
 * Copyright (C) 2008 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.event.ActivityEvent;
import net.ooder.bpm.client.event.ProcessEvent;
import net.ooder.bpm.client.event.RightEvent;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.proxy.ActivityInstProxy;
import net.ooder.bpm.engine.proxy.ProcessInstProxy;
import net.ooder.common.JDSEvent;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;
import net.ooder.engine.event.JDSEventDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统事件控制核心，所有引擎事件都在这里中转处理
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class BPMCoreEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, BPMCoreEventControl.class);

    // singleton instance
    private static BPMCoreEventControl instance = null;
    // 核心流程事件监听器列表
    public List coreProcessEventListeners = new ArrayList();

    // 核心活动事件监听器列表
    public List coreActivityEventListeners = new ArrayList();

    public static BPMCoreEventControl getInstance() {
        if (instance == null) {
            synchronized (BPMCoreEventControl.class) {
                if (instance == null) {
                    instance = new BPMCoreEventControl();
                }
            }
        }
        return instance;
    }

    protected BPMCoreEventControl() {
        // 初始化核心事件监听器
        initCoreListeners();
    }

    protected void initCoreListeners() {

        String[] processListeners = JDSConfig.getValues("event.ProcessEventListeners.listener");
        String[] activityListeners = JDSConfig.getValues("event.ActivityEventListeners.listener");

        String listener;

        // 装载流程事件监听器
        if (processListeners != null)
            for (int i = 0; i < processListeners.length; i++) {
                listener = processListeners[i];
                try {
                    EIProcessListener processListener = (EIProcessListener) ClassUtility.loadClass(listener).newInstance();
                    coreProcessEventListeners.add(processListener);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }

        // 装载活动事件监听器
        if (activityListeners != null)
            for (int i = 0; i < activityListeners.length; i++) {
                listener = activityListeners[i];

                try {
                    EIActivityListener activityListener = (EIActivityListener) ClassUtility.loadClass(listener).newInstance();
                    coreActivityEventListeners.add(activityListener);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }

        coreProcessEventListeners = Collections.unmodifiableList(coreProcessEventListeners);
        coreActivityEventListeners = Collections.unmodifiableList(coreActivityEventListeners);
    }

    /**
     * 事件分发方法，所有的BPM事件都通过该方法进行分发。
     *
     * @param event
     *            BPM事件
     */
    public void dispatchEvent(JDSEvent event) throws JDSException {
        if (event != null) {
            if (event instanceof EIProcessEvent) {
                dispatchCoreProcessEvent((EIProcessEvent) event);
            } else if (event instanceof EIActivityEvent) {
                //dispatchCoreActivityEvent((EIActivityEvent) event);
                BPMEventControl.getInstance().dispatchEvent(event);
            } else if (event instanceof ProcessEvent) {
                BPMEventControl.getInstance().dispatchEvent(event);
            }
        }
    }

    /**
     * 进行核心流程事件分发操作，同时分发相应的客户流程事件。
     *
     * @param event
     *            核心流程事件
     * @throws JDSException
     */
    public static void dispatchProcessEvent(final EIProcessEvent event, String systemCode) throws JDSException {
        if (event != null) {
            EIProcessEvent eiProcessEvent = event;
            BPMCoreEventControl ec = getInstance();

            // 分发核心流程事件
            ec.dispatchEvent(eiProcessEvent);

            // 分发客户流程事件
            EIProcessInst eiProcessInst = event.getProcessInst();
            ProcessInst processInst = new ProcessInstProxy(eiProcessInst, systemCode);
            ProcessEvent processEvent = new ProcessEvent(processInst, event.getID(), event.getClientService(), event.getContextMap());
            BPMEventControl.getInstance().dispatchEvent(processEvent);
        }
    }

    /**
     * 进行核心活动事件分发操作，同时分发相应的客户活动事件。
     *
     * @param event
     *            核心活动事件
     * @throws JDSException
     */
    public static void dispatchActivityEvent(final EIActivityEvent event, String systemCode) throws JDSException {
        if (event != null) {
            EIActivityEvent eiActivityEvent = event;
            BPMCoreEventControl ec = getInstance();

            // 分发核心活动事件
            ec.dispatchEvent(eiActivityEvent);

            // 分发客户活动事件
            EIActivityInst[] eiActivityInsts = eiActivityEvent.getActivityInsts();
            if (eiActivityInsts != null && eiActivityInsts.length != 0) {
                List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();
                for (int i = 0; i < eiActivityInsts.length; i++) {
                    ActivityInst activityInst = new ActivityInstProxy(eiActivityInsts[i], systemCode);

                    activityInsts.add(activityInst);
                }
                ActivityEvent activityEvent = new ActivityEvent(activityInsts, event.getID(), eiActivityEvent.getClientService(), eiActivityEvent.getContextMap());

                BPMEventControl.getInstance().dispatchEvent(activityEvent);
            }
        }
    }

    /**
     * 进行核心活动事件分发操作，同时分发相应的客户活动事件。
     *
     * @param event
     *            核心活动事件
     * @throws JDSException
     */
    public static void dispatchRightEvent(final EIRightEvent event, String systemCode) throws JDSException {
        if (event != null) {
            EIRightEvent eiRightEvent = event;
            BPMCoreEventControl ec = getInstance();

            // 分发核心活动事件
            ec.dispatchEvent(eiRightEvent);

            // 分发客户活动事件
            EIActivityInst[] eiActivityInsts = eiRightEvent.getActivityInsts();
            if (eiActivityInsts != null && eiActivityInsts.length != 0) {
                ActivityInst[] activityInsts = new ActivityInst[eiActivityInsts.length];
                for (int i = 0; i < eiActivityInsts.length; i++) {
                    ActivityInst activityInst = new ActivityInstProxy(eiActivityInsts[i], systemCode);
                    activityInsts[i] = activityInst;
                    RightEvent rightEvent = new RightEvent(activityInst, eiRightEvent.getID(), eiRightEvent.getClientService(), eiRightEvent.getContextMap());

                    BPMEventControl.getInstance().dispatchEvent(rightEvent);

                }

            }
        }
    }

    /**
     * 分发核心流程事件
     *
     * @param event
     *            核心流程事件
     */
    private void dispatchCoreProcessEvent(final EIProcessEvent event) {
        EIProcessEvent pe = event;
        for (Iterator it = coreProcessEventListeners.iterator(); it.hasNext(); ) {
            try {
                EIProcessListener listener = (EIProcessListener) it.next();
                switch (pe.getID()) {
                    case STARTING:
                        // 流程实例正在被启动
                        listener.processStarting(pe);
                        break;
                    case STARTED:
                        // 流程实例已经被启动
                        listener.processStarted(pe);
                        break;
                    case SAVING:
                        // 流程实例正在被保存
                        listener.processSaving(pe);
                        break;
                    case SAVED:
                        // 流程实例已经被保存
                        listener.processSaved(pe);
                        break;
                    case SUSPENDING:
                        // 流程实例正在被挂起
                        listener.processSuspending(pe);
                        break;
                    case SUSPENDED:
                        // 流程实例已经被挂起
                        listener.processSuspended(pe);
                        break;
                    case RESUMING:
                        // 流程实例正在被恢复（从挂起状态）
                        listener.processResuming(pe);
                        break;
                    case RESUMED:
                        // 流程实例已经被恢复（从挂起状态）
                        listener.processResumed(pe);
                        break;
                    case ABORTING:
                        // 流程实例正在被取消
                        listener.processAborting(pe);
                        break;
                    case ABORTED:
                        // 流程实例已经被取消
                        listener.processAborted(pe);
                        break;
                    case COMPLETING:
                        // 流程实例正在被完成
                        listener.processCompleting(pe);
                        break;
                    case COMPLETED:
                        // 流程实例已经被完成
                        listener.processCompleted(pe);
                        break;
                    case DELETING:
                        // 流程实例正在被删除
                        listener.processDeleting(pe);
                        break;
                    case DELETED:
                        // 流程实例已经被删除
                        listener.processDeleted(pe);
                        break;
                    default:
                        throw new JDSException("Unsupport core process event type: " + pe.getID(), JDSException.UNSUPPORTCOREPROCESSEVENTERROR);
                }
            } catch (Throwable e) {
                logger.warn("Listener execute failed!", e);
            }

        }
    }

    /**
     * 分发核心活动事件
     *
     * @param event
     *            核心活动事件
     */
    private void dispatchCoreActivityEvent(final EIActivityEvent event) {
        EIActivityEvent ae = event;
        for (Iterator it = coreActivityEventListeners.iterator(); it.hasNext(); ) {
            try {
                EIActivityListener listener = (EIActivityListener) it.next();

                switch (ae.getID()) {
                    case INITED:
                        // 活动初始化完毕，进入inactive状态
                        listener.activityInited(ae);
                        break;
                    case ROUTING:
                        // 活动开始执行路由操作
                        listener.activityRouting(ae);
                        break;
                    case ROUTED:
                        // 活动完成路由操作
                        listener.activityRouted(ae);
                        break;
                    case ACTIVING:
                        // 活动开始被激活(进入active状态)
                        listener.activityActiving(ae);
                        break;
                    case ACTIVED:
                        // 活动完成激活(进入active状态)
                        listener.activityActived(ae);
                        break;
                    case SAVEING:
                        // 表单开始保存
                        listener.activityFormSaveing(event);
                        break;
                    case SAVEED:
                        // 表单已经保存
                        listener.activityFormSaveed(event);
                        break;
                    case SPLITING:
                        // 活动开始执行路由分裂
                        listener.activitySpliting(ae);
                        break;
                    case SPLITED:
                        // 活动已经分裂为多个活动实例
                        listener.activitySplited(ae);
                        break;
                    case JOINING:
                        // 活动开始执行合并操作
                        listener.activityJoining(ae);
                        break;
                    case JOINED:
                        // 活动已经完成合并操作
                        listener.activityJoined(ae);
                        break;
                    case OUTFLOWING:
                        // 活动开始跳转到其他流程上
                        listener.activityOutFlowing(ae);
                        break;
                    case OUTFLOWED:
                        // 活动已经跳转到其他流程上
                        listener.activityOutFlowed(ae);
                        break;
                    case OUTFLOWRETURNING:
                        // 外流活动开始返回
                        listener.activityOutFlowReturning(ae);
                        break;
                    case OUTFLOWRETURNED:
                        // 外流活动完成返回
                        listener.activityOutFlowReturned(ae);
                        break;
                    case SUSPENDING:
                        // 活动开始挂起
                        listener.activitySuspending(ae);
                        break;
                    case SUSPENDED:
                        // 活动已经挂起
                        listener.activitySuspending(ae);
                        break;
                    case RESUMING:
                        // 活动开始恢复
                        listener.activityResuming(ae);
                        break;
                    case RESUMED:
                        // 活动已经恢复
                        listener.activityResumed(ae);
                        break;
                    case COMPLETING:
                        // 活动开始完成
                        listener.activityCompleting(ae);
                        break;
                    case COMPLETED:
                        // 活动已经完成
                        listener.activityCompleted(ae);
                        break;
                    case TAKEBACKING:
                        // 活动开始收回
                        listener.activityTakebacking(event);
                        break;
                    case TAKEBACKED:
                        // 活动已经收回
                        listener.activityTakebacked(event);
                        break;
                    case DISP:
                        // 展示活动表单
                        listener.activityDisplay(event);
                        break;
                    default:
                        throw new JDSException("Unsupport core activity event type: " + ae.getID(), JDSException.UNSUPPORTCOREACTIVITYEVENTERROR);
                }
            } catch (Throwable e) {
                logger.warn("Listener execute failed!", e);
            }

        }
    }


}


