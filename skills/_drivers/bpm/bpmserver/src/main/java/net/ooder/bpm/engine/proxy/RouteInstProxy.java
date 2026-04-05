package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.RouteInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.EIRouteInst;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

import java.util.Date;

public class RouteInstProxy implements RouteInst {
    private final String systemCode;
    private String routeInstId;

    private String processInstId;

    private String routeName;;

    private String description;

    private String fromActivityId;

    private String toActivityId;

    private RouteDirction routeDirection;

    private RouteInstType routeType;


    private Date routeTime;


    public RouteInstProxy(EIRouteInst routeInst,String systemCode) {
        this.systemCode=systemCode;
        this.description=routeInst.getDescription();
        this.fromActivityId=routeInst.getFromActivityId();
        this.processInstId=routeInst.getProcessInstId();
        this.toActivityId=routeInst.getToActivityId();
        this.routeDirection=routeInst.getRouteDirection();
        this.routeInstId=routeInst.getRouteInstId();
        this.routeName=routeInst.getRouteName();
        this.routeTime=routeInst.getRouteTime();
        this.routeType=routeInst.getRouteType();
    }

    @Override
    public String getRouteInstId() {
        return this.routeInstId;
    }

    @Override
    public String getProcessInstId() {
        return this.processInstId;
    }

    @Override
    public String getRouteName() {
        return this.routeName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFromActivityId() {
        return this.fromActivityId;
    }

    @Override
    public String getToActivityId() {
        return this.toActivityId;
    }

    @Override
    public RouteDirction getRouteDirection() {
        return this.routeDirection;
    }

    @Override
    public RouteInstType getRouteType() {
        return this.routeType;
    }

    @Override
    public Date getRouteTime() {
        return this.routeTime;
    }

    
    @JSONField(serialize = false)
    private WorkflowClientService getClient() {
        JDSSessionFactory bpmSessionFactory = new JDSSessionFactory(null);
        WorkflowClientService client = null;
        try {
            JDSClientService jdsclient = bpmSessionFactory.getClientService(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());
            client = BPMServer.getInstance().getWorkflowService(jdsclient);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return client;
    }

    @Override
    
    @JSONField(serialize = false)
    public ProcessInst getProcessInst() throws BPMException {
        ProcessInst processInst=null;
        try {
            processInst= getClient().getProcessInst(this.getProcessInstId());
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return processInst;
    }
}
