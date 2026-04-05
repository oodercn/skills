package net.ooder.bpm.userclient;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.server.JDSClientService;

public class GetBPMClient extends AbstractFunction {

    public WorkflowClientService perform(JDSClientService clientService) throws BPMException {

	if (clientService == null) {
	    throw new BPMException("session失效,请重新登录！", BPMException.NOTLOGINEDERROR);
	}

	WorkflowClientService client=null;
	try {
	    client = BPMServer.getInstance().getWorkflowService(clientService);
	} catch (JDSException e) {
	    throw new BPMException(e.getMessage(), BPMException.NOTLOGINEDERROR);
	}
	if (client == null) {
	    throw new BPMException(" SessionHandle:[" +clientService.getSessionHandle()+"]未获取流程授权！", BPMException.NOTLOGINEDERROR);
	}
	return client;
    }
}

