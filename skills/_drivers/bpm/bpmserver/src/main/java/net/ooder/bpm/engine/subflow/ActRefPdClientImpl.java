package net.ooder.bpm.engine.subflow;

import java.util.ArrayList;
import java.util.List;

import net.ooder.bpm.engine.subflow.db.ActRefPd;
import net.ooder.bpm.engine.subflow.db.DbActRefPdDAO;
import net.ooder.bpm.engine.subflow.db.DbActRefPdManager;

public class ActRefPdClientImpl implements ActRefPdClient {

    public ActRefPdClientImpl() {

    }

    public ActRefPd getActRefPdbyActivityId(String activityId) {
	ActRefPd eap = null;
	DbActRefPdManager ActRefPdManager = new DbActRefPdManager();
	eap = ActRefPdManager.getActRefPd(activityId);
	return eap;
    }

    public String getSubProcessDefVersionId(String activityId) {
	String subProcessDefVersionId = subProcessDefVersionId = getSubPorcessXPDL(activityId).get(0);
	return subProcessDefVersionId;
    }

    public String getIswaitreturn(String activityId) {
	ActRefPd ActRefPd = getActRefPdbyActivityId(activityId);
	String iswaitreturn = ActRefPd.getIswaitreturn();
	return iswaitreturn;
    }

    public List<String> getSubPorcessXPDL(String versionId) {

	List<String> versionIds = new ArrayList<String>();
	if (versionId != null) {
	    DbActRefPdDAO dao = new DbActRefPdDAO();
	    List<ActRefPd> daoList = dao.findBymainprocessVerId(versionId);
	    for (int k = 0; daoList.size() > k; k++) {
		ActRefPd af = (ActRefPd) daoList.get(k);
		String subProcessVersionId = af.getDestprocessVerId();
		versionIds.add(subProcessVersionId);
	    }

	}

	return versionIds;
    }

    public void delete(String versionId) {
	DbActRefPdDAO dao = new DbActRefPdDAO();
	DbActRefPdManager actRefPdManager = new DbActRefPdManager();
	actRefPdManager.deleteByVersId(versionId);

    }

}
