
package net.ooder.bpm.engine.database.device;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDeviceAtt;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformSequence;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDeviceSpecial;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.command.CommandRetry;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DbActivityDefDeviceManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefDeviceManager.class);

    private static DbActivityDefDeviceManager instance = new DbActivityDefDeviceManager();

    public static DbActivityDefDeviceManager getInstance() {
        return instance;
    }

    /**
     * 流程定义cache
     */
    Cache cache = null; // 流程定义cache

    /**
     * 流程定义cache是否可用
     */
    boolean cacheEnabled;

    public DbActivityDefDeviceManager() {
        // cache = CacheManagerFactory.getCache(BPMConstants.CONFIG_KEY,
        // "ActivityDefDeviceCache");
        cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(BPMConstants.CONFIG_KEY).isCacheEnabled();

    }

    /**
     * get the manager object used to get connections
     *
     * @return the manager used
     */
    DbManager getManager() {
        return DbManager.getInstance();
    }

    /**
     * free connection
     *
     * @param c the connection to release
     */
    void freeConnection(Connection c) {
        getManager().releaseConnection(c); // back to pool
    }

    /**
     * get connection
     */
    Connection getConnection() throws SQLException {
        return getManager().getConnection();
    }

    public DbActivityDefDevice createActivityDefDevice() {
        return new DbActivityDefDevice();
    }

    public DbActivityDefDevice loadByKey(String activityDefId) throws BPMException {
        DbActivityDefDevice deviceDef = null;
        // commet by lxl 2004-04-12 ，去掉Cache
        // DeviceDef = (DbActivityDefDevice) cache.get(activityDefId);
        // if (DeviceDef != null) {
        // return DeviceDef;
        // }

        EIActivityDefManager manager = EIActivityDefManager.getInstance();
        EIActivityDef activityDef = manager.loadByKey(activityDefId);
        if (activityDef == null) {
            return null;
        }

        deviceDef = createActivityDefDevice();
        log.info("load....DbActivityDefDevice");
        deviceDef.setActivityDefId(activityDefId);
        deviceDef.setPerformType(ActivityDefDevicePerformtype.fromType(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.PERFORMTYPE.getType())));

        deviceDef.setPerformSequence(ActivityDefDevicePerformSequence.fromType(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.PERFORMSEQUENCE.getType())));

        deviceDef.setCommandSelectedId(activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDSELECTEDID.getType()));

        deviceDef.setCommandSelectedAtt(activityDef.getAttribute(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDSELECTEDID.getType()));

        deviceDef.setSpecialSendScope(ActivityDefDeviceSpecial.fromType(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.SPECIALSENDSCOPE.getType())));

        deviceDef.setCanTakeBack(CommonYesNoEnum.fromType(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.CANTAKEBACK.getType())));

        deviceDef.setCanReSend(CommonYesNoEnum.fromType(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.CANRESEND.getType())));

        deviceDef.setEndpointSelectedAtt(activityDef.getAttribute(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.DEVICESELECTEDID.getType()));

        deviceDef.setEndpointSelectedId(activityDef.getAttributeValue(Attributetype.DEVICE.getType() + "." + ActivityDefDeviceAtt.DEVICESELECTEDID.getType()));

        deviceDef.setCommandExecType(CommandExecType.fromType(activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDEXECTYPE.getType())));
        deviceDef.setCommandRetry(CommandRetry.fromType(activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDRETRY.getType())));
        deviceDef.setCanOffLineSend(CommonYesNoEnum.fromType(activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.CANOFFLINESEND.getType())));

        String retryTimes = activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDRETRYTIMES.getType());
        if (retryTimes != null) {
            deviceDef.setCommandExecRetryTimes(Integer.valueOf(retryTimes));
        }

        String delayTime = activityDef.getAttributeValue(Attributetype.COMMAND.getType() + "." + ActivityDefDeviceAtt.COMMANDDELAYTIME.getType());
        if (delayTime != null) {
            deviceDef.setCommandDelayTime(Integer.valueOf(delayTime));
        }

        // putToCache(activityDefId, DeviceDef);

        return deviceDef;
    }

    private EIAttributeDef createDeviceAttribute(ActivityDefDeviceAtt name, String value) {
        return createDeviceAttribute(name, value, Attributetype.DEVICE);
    }

    private EIAttributeDef createDeviceAttribute(ActivityDefDeviceAtt name, String value, Attributetype type) {
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
     * @param DeviceDef
     */
    private void putToCache(String activityDefId, DbActivityDefDevice DeviceDef) {
        synchronized (activityDefId.intern()) {
            if (cache.get(activityDefId) == null) {
                cache.put(activityDefId, DeviceDef);
            }
        }
    }

}


