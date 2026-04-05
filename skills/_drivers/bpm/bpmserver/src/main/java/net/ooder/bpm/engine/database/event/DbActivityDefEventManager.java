/**
 * $RCSfile: DbActivityDefEventManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * CopyEvent (C) 2003 itjds, Inc. All Events reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.event;

import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;
import net.ooder.annotation.DurationUnit;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation;
import net.ooder.bpm.enums.activitydef.event.ActivityDefEventAtt;
import net.ooder.bpm.enums.event.DeviceAPIEventEnums;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认的活动设备数据管理器
 * </p>
 * <p>
 * CopyEvent: CopyEvent (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 将Cache去掉了，否则在保存活动定义的时候很难保持与数据库的同步更新 同时隐藏了保存方法，不要再这里保存了，如果需要保存，请使用扩展属性的方法。
 *
 * @version 2.0
 */
public class DbActivityDefEventManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefEventManager.class);

    private static DbActivityDefEventManager instance = new DbActivityDefEventManager();

    public static DbActivityDefEventManager getInstance() {
        return instance;
    }

    /** 流程定义cache */
    Cache cache = null; // 流程定义cache

    /** 流程定义cache是否可用 */
    boolean cacheEnabled;

    public DbActivityDefEventManager() {

        cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(BPMConstants.CONFIG_KEY).isCacheEnabled();

    }

    public DbActivityDefEvent createActivityDefEvent() {
        return new DbActivityDefEvent();
    }

    public DbActivityDefEvent loadByKey(String activityDefId) throws BPMException {
        DbActivityDefEvent eventDef = null;

        EIActivityDefManager manager = EIActivityDefManager.getInstance();
        EIActivityDef activityDef = manager.loadByKey(activityDefId);
        if (activityDef == null) {
            return null;
        }

        eventDef = createActivityDefEvent();
        eventDef.setActivityDefId(activityDefId);
        eventDef.setAlertTime(activityDef.getAttributeValue(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.ALERTTIME.getType()));
        eventDef.setAttributName(DeviceDataTypeKey.fromType(activityDef.getAttributeValue(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.ATTRIBUTENAME.getType())));
        eventDef.setDeadLineOperation(ActivityDefDeadLineOperation.fromType(activityDef.getAttributeValue(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.DEADLINEOPERATION.getType())));
        eventDef.setDurationUnit(DurationUnit.fromType(activityDef.getAttributeValue(Attributetype.DEVICE + "." + ActivityDefEventAtt.DEVICESELECTEDID.getType())));

        eventDef.setDeviceEvent(DeviceAPIEventEnums.fromMethod(activityDef.getAttributeValue(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.DEVICEAPI.getType())));
        eventDef.setEndpointSelectedAtt(activityDef.getAttribute(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.DEVICESELECTEDID.getType()));
        eventDef.setEndpointSelectedId(activityDef.getAttributeValue(Attributetype.DEVICEEVENT + "." + ActivityDefEventAtt.DEVICESELECTEDID.getType()));

        return eventDef;
    }


    private EIAttributeDef createEventAttribute(ActivityDefEventAtt name, String value, Attributetype type) {
        EIAttributeDefManager attriuteDefManager = EIAttributeDefManager.getInstance();

        EIAttributeDef eiAtt = attriuteDefManager.createAttributeDef();
        eiAtt.setId(UUID.randomUUID().toString());
        eiAtt.setInterpretClass(AttributeInterpretClass.STRING.getType());
        eiAtt.setCanInstantiate(CommonYesNoEnum.NO.getType());
        eiAtt.setName(name.getType());
        eiAtt.setValue(value);
        eiAtt.setType(type.getType());

        return eiAtt;
    }

    /**
     * @param activityDefId
     * @param EventDef
     */
    private void putToCache(String activityDefId, DbActivityDefEvent EventDef) {
        synchronized (activityDefId.intern()) {
            if (cache.get(activityDefId) == null) {
                cache.put(activityDefId, EventDef);
            }
        }
    }

}


