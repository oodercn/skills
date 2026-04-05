package net.ooder.skills.bpm.demo.service;

import net.ooder.bpm.client.ct.BPMClientFactory;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.JDSException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

public class GetBPMClientService {

    public WorkflowClientService getClient(JDSClientService clientService) throws BPMException {
        if (clientService == null) {
            try {
                clientService = JDSServer.getInstance().getAdminClient();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

        if (clientService == null) {
            throw new BPMException("session失效,请重新登录！", BPMException.NOTLOGINEDERROR);
        }

        WorkflowClientService client = null;
        try {
            client = BPMClientFactory.getWorkflowClient(clientService);
        } catch (JDSException e) {
            throw new BPMException(e.getMessage(), BPMException.NOTLOGINEDERROR);
        }
        if (client == null) {
            throw new BPMException(" SessionHandle:[" + clientService.getSessionHandle() + "]未获取流程授权！", BPMException.NOTLOGINEDERROR);
        }
        return client;
    }
}
