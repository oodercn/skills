package net.ooder.bpm.webservice.XPDLBean;

import java.util.ArrayList;
import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.subflow.db.ActRefPd;
import net.ooder.bpm.engine.subflow.db.DbActRefPdDAO;

public class SubProcessDefUtil {

    private static final String SubFlow = "SubFlow";
    private static final String OutFlow = "OutFlow";
    private static final String BlockFlow = "Block";

    public List<String> getActivitySetsXPDL(String versionId) throws BPMException {
        return this.getSubPorcessXPDL(versionId, BlockFlow);
    }

    public List<String> getSubPorcessXPDL(String versionId) throws BPMException {

	return this.getSubPorcessXPDL(versionId, SubFlow);
    }

    public List<String> getOutPorcessXPDL(String versionId) throws BPMException {
	return this.getSubPorcessXPDL(versionId, OutFlow);

    }

    public List<String> getSubPorcessXPDL(String versionId, String type) throws BPMException {
	List<String> versionIds = new ArrayList();

	if (versionId != null) {

	    DbActRefPdDAO dbDao = new DbActRefPdDAO();

	    List<ActRefPd> daoList = dbDao.findBymainprocessVerId(versionId);

	    for (ActRefPd actRefPd : daoList) {

		String subProcessId = actRefPd.getDestprocessVerId();

		if (actRefPd.getProcesstype().equals(type)) {
		    versionIds.add(subProcessId);
		}

	    }
	}

	return versionIds;
    }

    public String getProcessDefVersionId(String ProcessDefId) throws BPMException {
	String versionId = null;
	List epdList = EIProcessDefVersionManager.getInstance().loadByProcessdefId(ProcessDefId);
	if (epdList != null && epdList.size() > 0) {
	    EIProcessDefVersion epd = (EIProcessDefVersion) epdList.get(0);
	    versionId = epd.getProcessDefVersionId();
	}

	return versionId;
    }

    public List<String> getProcessDefVersionIdByProcessDefId(String ProcessDefId) throws BPMException {
	// String versionId=null;
	List epdList = EIProcessDefVersionManager.getInstance().loadByProcessdefId(ProcessDefId);
	// 子流程原则上应该没有版本,版本由主流程控制
	List<String> versionIds = new ArrayList();
	for (int k = 0; epdList.size() > k; k++) {
	    EIProcessDefVersion epd = (EIProcessDefVersion) epdList.get(k);
	    versionIds.add(epd.getProcessDefVersionId());
	}
	return versionIds;
    }
}
