package net.ooder.bpm.engine;

import java.util.List;
import java.util.Map;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.common.ReturnType;

public class DefaultFileEngine  implements FileEngine{
	
	
	public String systemCode;
	
	
	
	public DefaultFileEngine(String systemCode){
		this.systemCode=systemCode;
		
	}
	public ReturnType abortProcessInst(String processInstId, Map ctx) throws BPMException {
		
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType combineActivityInsts(String[] activityInstIds, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType completeProcessInst(String processInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType copyTo(List<ActivityInst> eiActivityInst, List<String> personIds) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType createProcessInst(String processInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType createProcessInst(String processInstId, String initType, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType deleteProcessInst(String processInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType endRead(String activityInstId,String activityHistoryInstID, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType resumeActivityInst(String activityInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType resumeProcessInst(String processInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType routeBack(String activityInstId,String activityInstHistoryId,
			Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType routeTo(String activityInstId, String activityDefId,
			Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType saveActivityHistoryInst(String activityInstId,
			String activityInstHistoryId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	

	public ReturnType signReceive(String activityInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType splitActivityInst(String activityInstId,
			String[] subActivityInstIds, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType startActivityInst(String activityInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType startProcessInst(String processInstId,
			String activityInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType suspendActivityInst(String activityInstID, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType suspendProcessInst(String processInstID, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType tackBack(String activityInstId, Map ctx) throws BPMException {
		return  new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public void updateActivityInstHisMapDAO(String activityInstHistoryId,  DataMap formdata,
			String userId) throws BPMException {
	}

	public void updateActivityInstMapDAO(String activityInstId,  DataMap formdata,
			String userId) throws BPMException {	
	}

	public void updateProcessInstMapDAO(String processInstId, DataMap formdata,
			String userId) throws BPMException {
	
		
	}
	
	public String getSystemCode() {
		return systemCode;
	}

	@Override
	public void setWorkflowClient(WorkflowClientService service) {

	}

	public void setSystemCode(String systemCode) {
		this.systemCode=systemCode;
	}

	public ReturnType clearHistory(String activityInstHistoryID, Map ctx) throws BPMException {

		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ReturnType deleteHistory(String activityInstHistoryID, Map ctx) throws BPMException {
		
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map ctx) throws BPMException {
		// TODO Auto-generated method stub
		
	}
	public ReturnType endTask(String activityInstID, String activityHistoryId, Map ctxRight) {
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}
	
	public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map ctx) throws BPMException {
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

}


