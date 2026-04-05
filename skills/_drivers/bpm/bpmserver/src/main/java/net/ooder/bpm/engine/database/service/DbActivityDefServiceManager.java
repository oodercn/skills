
package net.ooder.bpm.engine.database.service;

import net.ooder.annotation.*;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.bpm.enums.activitydef.service.ActivityDefServiceAtt;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DbActivityDefServiceManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefServiceManager.class);

    private static DbActivityDefServiceManager instance = new DbActivityDefServiceManager();

    public static DbActivityDefServiceManager getInstance() {
	return instance;
    }

    /** 流程定义cache */
    Cache cache = null; // 流程定义cache

    /** 流程定义cache是否可用 */
    boolean cacheEnabled;

    public DbActivityDefServiceManager() {

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

    public DbActivityDefService createActivityDefService() {
	return new DbActivityDefService();
    }

    public DbActivityDefService loadByKey(String activityDefId) throws BPMException {
	DbActivityDefService eventDef = null;

	EIActivityDefManager manager = EIActivityDefManager.getInstance();
	EIActivityDef activityDef = manager.loadByKey(activityDefId);
	if (activityDef == null) {
	    return null;
	}

	eventDef = createActivityDefService();
	eventDef.setActivityDefId(activityDefId);
	eventDef.setMethod(HttpMethod.fromType(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.HTTP_METHOD.getType())));
	eventDef.setRequestType(RequestType.fromType(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.HTTP_REQUESTTYPE.getType())));
	eventDef.setResponseType(ResponseType.fromType(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.HTTP_RESPONSETYPE.getType())));
	eventDef.setServiceParams(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.HTTP_SERVICEPARAMS.getType()));
	eventDef.setUrl(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.HTTP_URL.getType()));
	eventDef.setServiceSelectedAtt(activityDef.getAttribute(Attributetype.SERVICE + "." + ActivityDefServiceAtt.SERVICESELECTEDID.getType()));
	eventDef.setServiceSelectedID(activityDef.getAttributeValue(Attributetype.SERVICE + "." + ActivityDefServiceAtt.SERVICESELECTEDID.getType()));

	return eventDef;
    }

    private EIAttributeDef createServiceAttribute(ActivityDefServiceAtt name, String value) {
	return createServiceAttribute(name, value, Attributetype.EVENT);
    }

    private EIAttributeDef createServiceAttribute(ActivityDefServiceAtt name, String value, Attributetype type) {
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
     * @param ServiceDef
     */
    private void putToCache(String activityDefId, DbActivityDefService ServiceDef) {
	synchronized (activityDefId.intern()) {
	    if (cache.get(activityDefId) == null) {
		cache.put(activityDefId, ServiceDef);
	    }
	}
    }

}


