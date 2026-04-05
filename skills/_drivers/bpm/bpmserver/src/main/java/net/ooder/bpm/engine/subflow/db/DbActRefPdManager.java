package net.ooder.bpm.engine.subflow.db;

import java.util.List;
import java.util.UUID;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;

public class DbActRefPdManager {

    private Cache<String, ActRefPd> actRefPdCache;
    private boolean cacheEnabled;

    public DbActRefPdManager() {
	init();
    }

    private void init() {
	actRefPdCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, "ActRefPdCache");
	cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(BPMConstants.CONFIG_KEY).isCacheEnabled();
    }

    public ActRefPd getActRefPd(String activityId) {
	ActRefPd eap = (ActRefPd) actRefPdCache.get(activityId);

	if (eap == null || !cacheEnabled) {
	    DbActRefPdDAO ap = new DbActRefPdDAO();
	    eap = ap.findById(activityId);
	    actRefPdCache.put(activityId, eap);
	}

	return eap;
    }

    public ActRefPd getNewDbActRefPd() {
	ActRefPd ActRefPd = new ActRefPd();
	ActRefPd.setActivitydefId(UUID.randomUUID().toString());
	return ActRefPd;
    }

    public void delete(ActRefPd actRefPd) {
	DbActRefPdDAO dao = new DbActRefPdDAO();
	dao.delete(actRefPd);
	actRefPdCache.remove(actRefPd.getActivitydefId());

    }

    public void deleteByVersId(String vresId) {

	DbActRefPdDAO ap = new DbActRefPdDAO();
	List<ActRefPd> afList = ap.findBydestprocessVerId(vresId);

	for (ActRefPd af : afList) {
	    ap.delete(af);
	    actRefPdCache.remove(af.getActivitydefId());
	}
    }

    public void saveInstance(ActRefPd actRefPd) {

	DbActRefPdDAO ap = new DbActRefPdDAO();
 
	ActRefPd arp = ap.findById(actRefPd.getActivitydefId());

	if (arp != null) {
	    ap.update(actRefPd);
	} else {

	    ap.insert(actRefPd);
	}

	actRefPdCache.put(actRefPd.getActivitydefId(), actRefPd);

    }

}
