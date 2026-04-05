
package net.ooder.bpm.engine.query.right;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.RightEngine;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.DbActivityDefRightManager;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.query.ProcessDefFilter;
import net.ooder.bpm.enums.activitydef.ActivityDefRightAtt;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.config.ActivityDefImpl;
import net.ooder.context.JDSActionContext;
import net.ooder.org.Person;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author 
 * @version 1.0
 */
public class ProcessStartRightFilter extends ProcessDefFilter {

    private Map ctx = new HashMap();
    private String systemCode;

    public ProcessStartRightFilter(Map  ctx, String systemCode) {
	this.systemCode = systemCode;
	this.ctx = ctx;
    }

    /**
     * @see net.ooder.bpm.engine.query.ProcessDefFilter#filterProcessDefVersion(net.ooder.bpm.client.ProcessDefVersion)
     */
    public boolean filterProcessDefVersion(ProcessDefVersion obj) {

	String versionId = obj.getProcessDefVersionId();
	EIProcessDefVersionManager processDefVerMgr = EIProcessDefVersionManager.getInstance();
	EIActivityDefManager activityDefMgr = EIActivityDefManager.getInstance();
	DbActivityDefRightManager actRightMgr = DbActivityDefRightManager.getInstance();
	DbParticipantSelectManager participantMgr = DbParticipantSelectManager.getInstance();

	try {
	    EIProcessDefVersion eiProcessDef = processDefVerMgr.loadByKey(versionId);
	    EIActivityDef firstAct = activityDefMgr.getFirstActivityDefInProcess(versionId);
	    if (firstAct == null || firstAct.getImplementation().equals(ActivityDefImpl.Tool.getType())) {
		return false;
	    }

	 
	    if (firstAct.getImplementation().equals(ActivityDefImpl.No.getType())) {
		RightEngine rightEngine = BPMServer.getRigthEngine(JDSActionContext.getActionContext().getSystemCode());

		List list = (List) rightEngine.getActivityDefRightAttribute(firstAct.getActivityDefId()).getPerFormPersons();
		if (list == null || list.size() == 0) {
		    // 继续处理下面的filter
		    return processChildFilter(obj, systemCode);
		}
		for (Iterator it = list.iterator(); it.hasNext();) {
		    Person p = (Person) it.next();
		    if (p.getID().equalsIgnoreCase((String) ctx.get(RightCtx.USERID))) {
			// 继续处理下面的filter
			return processChildFilter(obj, systemCode);
		    }
		}
//	    } else if (firstAct.getImplementation().equals(ActivityDefImpl.EVENT.getType())) {
//		EventEngine eventEngine = BPMServer.getEventEngine(obj.getSystemCode());
//		
//		
//		String epIeee=(String) ctx.get(RightCtx.CTX_CURRENT_ENDPOINTIEEE);
//		if (epIeee!=null){
//		    DeviceEndPoint currEP = HomeServer.getAppEngine().getEndPointSerialno(epIeee);
//		    List<DeviceEndPoint> list = (List<DeviceEndPoint>) eventEngine.getActivityDefEventAttribute(firstAct.getActivityDefId(), ActivityDefEventAtt.DEVICESELECTEDID, ctx);			
//			if (list == null || list.size() == 0) {
//			    // 继续处理下面的filter
//			    return processChildFilter(obj, systemCode);
//			}else if (list.contains(currEP)) {
//			    return processChildFilter(obj, systemCode);
//			}
//		}else{
//		    return processChildFilter(obj, systemCode);
//		}		
	    }else{
		return processChildFilter(obj, systemCode);
	    }

	} catch (BPMException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return false;
    }

}


