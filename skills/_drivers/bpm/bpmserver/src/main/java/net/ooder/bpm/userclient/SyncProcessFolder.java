package net.ooder.bpm.userclient;

import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.event.BPDEvent;
import net.ooder.bpm.client.event.BPDListener;
import net.ooder.bpm.engine.BPMException;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.org.OrgManager;
import net.ooder.server.OrgManagerFactory;
import net.ooder.vfs.Folder;

@EsbBeanAnnotation(id = "SyncProcessFolder", name = "流程文件夹本地同步", expressionArr = "SyncProcessFolder()",flowType = EsbFlowType.listener,desc = "流程文件夹本地同步")
public class SyncProcessFolder implements BPDListener {

    public void createProcessFolder(ProcessDefVersion processDefVersion) throws BPMException {
        OrgManager orgManager = OrgManagerFactory.getOrgManager();
        String versionid = processDefVersion.getProcessDefVersionId();
        Folder processFolder = null;
//        try {
//            processFolder = CtVfsFactory.getCtVfsService().mkDir(OrgConstants.WORKFLOWBASEPATH+OrgConstants.WORKFLOWFORMPATH  + versionid, processDefVersion.getProcessDefName());
//            List<ActivityDef> acs = processDefVersion.getAllActivityDefs();
//            String initPath = processFolder.getPath();
//            for (ActivityDef ac : acs) {
//                if (ac.getImplementation().equals(ActivityDefImpl.NO)) {
//                    Folder activityFolder = CtVfsFactory.getCtVfsService().mkDir(initPath + ac.getActivityDefId(), ac.getName());
//                }
//            }
//        } catch (JDSException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    public void ProcessDefUpdated(BPDEvent event) throws BPMException {
        createProcessFolder((ProcessDefVersion) event.getSource());

    }

    @Override
    public void ProcessDefCreaded(BPDEvent event) throws BPMException {

    }

    @Override
    public void ProcessDefDeleted(BPDEvent event) throws BPMException {

    }

    @Override
    public void ProcessDefFreezed(BPDEvent event) throws BPMException {

    }

    @Override
    public void ProcessDefActivaed(BPDEvent event) throws BPMException {

    }
}


