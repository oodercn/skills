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
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 榛樿鐨勬椿鍔ㄨ澶囨暟鎹鐞嗗櫒
 * </p>
 * <p>
 * CopyEvent: CopyEvent (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 灏咰ache鍘绘帀浜嗭紝鍚﹀垯鍦ㄤ繚瀛樻椿鍔ㄥ畾涔夌殑鏃跺€欏緢闅句繚鎸佷笌鏁版嵁搴撶殑鍚屾鏇存柊 鍚屾椂闅愯棌浜嗕繚瀛樻柟娉曪紝涓嶈鍐嶈繖閲屼繚瀛樹簡锛屽鏋滈渶瑕佷繚瀛橈紝璇蜂娇鐢ㄦ墿灞曞睘鎬х殑鏂规硶銆?
 *
 * @version 2.0
 */
public class DbActivityDefEventManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefEventManager.class);

    private static DbActivityDefEventManager instance = new DbActivityDefEventManager();

    public static DbActivityDefEventManager getInstance() {
        return instance;
    }

    /** 娴佺▼瀹氫箟cache */
    Cache cache = null; // 娴佺▼瀹氫箟cache

    /** 娴佺▼瀹氫箟cache鏄惁鍙敤 */
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


