/**
 * $RCSfile: DbActivityDefRightManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.right;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.bpm.enums.activitydef.ActivityDefRightAtt;
import net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认的活动权限数据管理器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author lxl
 * @version 2.0
 */
public class DbActivityDefRightManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefRightManager.class);

    private static DbActivityDefRightManager instance = new DbActivityDefRightManager();

    public static DbActivityDefRightManager getInstance() {
        return instance;
    }

    /** 流程定义cache */
    Cache cache = null; // 流程定义cache

    /** 流程定义cache是否可用 */
    boolean cacheEnabled;

    public DbActivityDefRightManager() {
        // cache = CacheManagerFactory.getCache(BPMConstants.CONFIG_KEY,
        // "ActivityDefRightCache");
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
     * @param c
     *            the connection to release
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

    public DbActivityDefRight createActivityDefRight() {
        return new DbActivityDefRight();
    }

    public DbActivityDefRight loadByKey(String activityDefId) throws BPMException {
        DbActivityDefRight rightDef = null;


        EIActivityDefManager manager = EIActivityDefManager.getInstance();
        EIActivityDef activityDef = manager.loadByKey(activityDefId);
        if (activityDef == null) {
            return null;
        }

        rightDef = createActivityDefRight();
        rightDef.setActivityDefId(activityDefId);
        rightDef.setPerformType(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.PERFORMTYPE.getType()));
        rightDef.setPerformSequence(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.PERFORMSEQUENCE.getType()));
        rightDef.setSpecialSendScope(ActivityDefSpecialSendScope.fromType(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.SPECIALSENDSCOPE.getType())));
        rightDef.setCanInsteadSign(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.CANINSTEADSIGN.getType()));

        rightDef.setCanTakeBack(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.CANTAKEBACK.getType()));

        rightDef.setCanReSend(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.CANRESEND.getType()));

        rightDef.setInsteadSignSelectedId(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.INSTEADSIGNSELECTED.getType()));
        rightDef.setInsteadSignSelectedAtt(activityDef.getAttribute(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.INSTEADSIGNSELECTED.getType()));
        rightDef.setPerformerSelectedId(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.PERFORMERSELECTEDID.getType()));
        rightDef.setPerformerSelectedAtt(activityDef.getAttribute(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.PERFORMERSELECTEDID.getType()));
        rightDef.setReaderSelectedId(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.READERSELECTEDID.getType()));
        rightDef.setReaderSelectedAtt(activityDef.getAttribute(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.READERSELECTEDID.getType()));
        rightDef.setMovePerformerTo(RightGroupEnums.fromType(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.MOVEPERFORMERTO.getType())));
        rightDef.setMovePerformerTo(RightGroupEnums.fromType(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.MOVESPONSORTO.getType())));
        rightDef.setMoveReaderTo(RightGroupEnums.fromType(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.MOVEREADERTO.getType())));
        rightDef.setSurrogateId(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.SURROGATEID.getType()));
        rightDef.setSurrogateName(activityDef.getAttributeValue(Attributetype.RIGHT.getType() + "." + ActivityDefRightAtt.SURROGATENAME.getType()));

        return rightDef;
    }

    private EIAttributeDef createRightAttribute(AttributeName name, String value) {
        EIAttributeDefManager attriuteDefManager = EIAttributeDefManager.getInstance();

        EIAttributeDef eiAtt = attriuteDefManager.createAttributeDef();
        eiAtt.setId(UUID.randomUUID().toString());
        eiAtt.setInterpretClass(AttributeInterpretClass.STRING.getType());
        eiAtt.setCanInstantiate(CommonYesNoEnum.NO.getType());
        eiAtt.setName(name.getType());
        eiAtt.setValue(value);
        eiAtt.setType(Attributetype.RIGHT.getType());

        return eiAtt;
    }

    /**
     * @param activityDefId
     * @param rightDef
     */
    private void putToCache(String activityDefId, DbActivityDefRight rightDef) {
        synchronized (activityDefId.intern()) {
            if (cache.get(activityDefId) == null) {
                cache.put(activityDefId, rightDef);
            }
        }
    }

}


