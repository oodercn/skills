package net.ooder.bpm.web;

import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.client.service.FDTService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;

import java.util.List;

@EsbBeanAnnotation(id = "FDTService", name = "FDTService", expressionArr = "FDTServiceImpl()", desc = "FDTService")
public class FDTServiceImpl implements FDTService {

    @Override
    public ResultModel<FormClassBean> getActivityMainFormDef(String activityDefId) {
        ResultModel<FormClassBean> result=new  ResultModel<FormClassBean>();

        return result;
    }

    @Override
    public ResultModel<List<FormClassBean>> getAllActivityDataFormDef(String activityDefId) {

        ResultModel<List<FormClassBean>> result=new  ResultModel<List<FormClassBean>>();

        return result;
    }

    @Override
    public ResultModel<ProcessDefForm> getProcessDefForm(String processDefVersionId) {

        ResultModel<ProcessDefForm> result=new  ResultModel<ProcessDefForm>();
        try {
            ProcessDefForm processDefForm =getClient().getMapDAODataEngine().getProcessDefForm(processDefVersionId, null);
            result.setData(processDefForm);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }


    /**
     *
     *
     * @return
     */
    public WorkflowClientService getClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }

}


