package net.ooder.bpm.engine.proxy;

import net.ooder.annotation.DurationUnit;
import net.ooder.bpm.client.ActivityDefEvent;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.IOTEventEngine;
import net.ooder.bpm.engine.database.event.DbActivityDefEvent;
import net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation;
import net.ooder.bpm.enums.event.DeviceAPIEventEnums;
import net.ooder.common.JDSException;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

import java.util.ArrayList;
import java.util.List;

public class ActivityDefEventProxy implements ActivityDefEvent {

    private IOTEventEngine eventEngine;
    private DbActivityDefEvent event;
    private DeviceDataTypeKey attributeName;

    public DbActivityDefEvent getEvent() {
        return event;
    }

    public void setEvent(DbActivityDefEvent event) {
        this.event = event;
    }

    public DeviceDataTypeKey getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(DeviceDataTypeKey attributeName) {
        this.attributeName = attributeName;
    }

    public ActivityDefEventProxy(DbActivityDefEvent event, String systemCode) {

        try {
            this.eventEngine = (IOTEventEngine) BPMServer.getEventEngine(systemCode);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.event = event;
    }

    @Override
    public DeviceAPIEventEnums getDeviceEvent() {
        return event.getDeviceEvent();
    }

    @Override
    public String getEndpointSelectedId() {
        return event.getEndpointSelectedId();
    }

    @Override
    public List<DeviceEndPoint> getEndpoints() {
        try {
            return this.eventEngine.getParticipant(event.getEndpointSelectedAtt(), null);
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return new ArrayList<DeviceEndPoint>();
    }

    @Override
    public DurationUnit getDurationUnit() {
        return event.getDurationUnit();
    }

    @Override
    public String getAlertTime() {
        return event.getAlertTime();
    }

    @Override
    public ActivityDefDeadLineOperation getDeadLineOperation() {
        return event.getDeadLineOperation();
    }
}
