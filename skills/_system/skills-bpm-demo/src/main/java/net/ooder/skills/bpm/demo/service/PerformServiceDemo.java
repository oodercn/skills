package net.ooder.skills.bpm.demo.service;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.client.data.FormData;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.util.StringUtility;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/bpm/demo/perform/")
public class PerformServiceDemo {

    @RequestMapping(method = RequestMethod.POST, value = "RouteBack")
    @ResponseBody
    public ResultModel<Boolean> routeBack(String activityInstHistoryId, String activityInstId) {
        ResultModel resultModel = new ResultModel();
        try {
            if (activityInstHistoryId == null) {
                ActivityInst inst = this.getClient().getActivityInst(activityInstId);
                activityInstHistoryId = inst.getRouteBackActivityHistoryInstList().get(0).getActivityHistoryId();
            }
            this.getClient().routeBack(activityInstId, activityInstHistoryId, null);
        } catch (BPMException e) {
            resultModel = new ErrorResultModel();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "TackBack")
    @ResponseBody
    public ResultModel<Boolean> tackBack(String activityInstId) {
        ResultModel resultModel = new ResultModel();
        try {
            this.getClient().takeBack(activityInstId, null);
        } catch (BPMException e) {
            resultModel = new ErrorResultModel();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "SignReceive")
    @ResponseBody
    public ResultModel<Boolean> signReceive(String activityInstId) {
        ResultModel resultModel = new ResultModel();
        try {
            this.getClient().signReceive(activityInstId, null);
        } catch (BPMException e) {
            resultModel = new ErrorResultModel();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "EndRead")
    @ResponseBody
    public ResultModel<Boolean> endRead(String activityInstId) {
        ResultModel resultModel = new ResultModel();
        try {
            this.getClient().endRead(activityInstId, null);
        } catch (BPMException e) {
            resultModel = new ErrorResultModel();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "AutoNext")
    @ResponseBody
    public ResultModel<String> autoNext(String activityInstId, String processInstId, String nextActivityDefId) {
        ResultModel<String> resultModel = new ResultModel();
        List<String> activityDefIds = new ArrayList<String>();
        List<Map<RightCtx, Object>> ctxs = new ArrayList<Map<RightCtx, Object>>();
        activityDefIds.add(nextActivityDefId);
        Map<RightCtx, Object> ctx = new HashMap<>();
        List<String> performList = new ArrayList<>();
        performList.add(this.getClient().getConnectInfo().getUserID());
        ctx.put(RightCtx.PERFORMERS, performList);
        ctxs.add(ctx);
        try {
            this.getClient().routeTo(activityInstId, activityDefIds, ctxs);
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "SaveOnly")
    @ResponseBody
    public ResultModel<Boolean> saveOnly(@RequestBody FormData data) {
        ResultModel resultModel = new ResultModel();
        try {
            ActivityInst inst = this.getClient().getActivityInst(data.getActivityInstId());
            DataMap map = inst.getFormValues();
            Map<String, DataMap> dataMap = data.getTable();
            map.putAll(dataMap);
            inst.updateFormValues(map);
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "RouteToEnd")
    @ResponseBody
    public ResultModel<Boolean> routeToEnd(String processInstId) {
        ResultModel resultModel = new ResultModel();
        try {
            this.getClient().completeProcessInst(processInstId, null);
        } catch (BPMException e) {
            resultModel = new ErrorResultModel();
        }
        return resultModel;
    }

    private Map<RightCtx, Object> fillCtx(RouteBean routeBean) throws BPMException {
        Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
        ActivityDef activityDef = getClient().getActivityDef(routeBean.getNextActivityDefId());
        if (activityDef != null) {
            ActivityDefPerformtype performType = activityDef.getRightAttribute().getPerformType();
            ActivityDefPerformSequence performSequence = activityDef.getRightAttribute().getPerformSequence();
            if (performType.equals(ActivityDefPerformtype.NOSELECT) || performType.equals(ActivityDefPerformtype.NEEDNOTSELECT)) {
                List<String> readList = new ArrayList<String>();
                List<String> performList = new ArrayList<String>();
                ctx.put(RightCtx.PERFORMERS, performList);
                ctx.put(RightCtx.READERS, readList);
            } else {
                String[] performArr = StringUtility.split(routeBean.getPerforms().getPerforms(), ";");
                String[] readArr = StringUtility.split(routeBean.getReaders().getReaders(), ";");
                List<String> performList = Arrays.asList(performArr);
                ctx.put(RightCtx.PERFORMERS, performList);
                ctx.put(RightCtx.READERS, Arrays.asList(readArr));
            }
        }
        return ctx;
    }

    private WorkflowClientService getClient() {
        WorkflowClientService client = EsbUtil.parExpression("$BPMC", WorkflowClientService.class);
        return client;
    }
}
