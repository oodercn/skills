package net.ooder.bpm.engine.proxy;

import net.ooder.bpm.client.ActivityDefDevice;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.IOTDeviceEngine;
import net.ooder.bpm.engine.database.device.DbActivityDefDevice;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformSequence;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDeviceSpecial;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.command.CommandRetry;
import net.ooder.command.Command;
import net.ooder.common.JDSException;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.agent.client.iot.DeviceEndPoint;

import java.util.List;

public class ActivityDefDeviceProxy implements ActivityDefDevice {

    private  DbActivityDefDevice dbActivityDefDevice;
    private IOTDeviceEngine deviceEngine;


    public ActivityDefDeviceProxy(DbActivityDefDevice dbActivityDefDevice, String systemCode){

        try {
            this.deviceEngine= (IOTDeviceEngine) BPMServer.getDeviceEngine(systemCode);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.dbActivityDefDevice=dbActivityDefDevice;

    }


    @Override
    public CommandExecType getCommandExecType() {
        return dbActivityDefDevice.getCommandExecType();
    }

    @Override
    public CommandRetry getCommandRetry() {
        return dbActivityDefDevice.getCommandRetry();
    }

    @Override
    public Integer getCommandExecRetryTimes() {
        return dbActivityDefDevice.getCommandExecRetryTimes();
    }

    @Override
    public Integer getCommandDelayTime() {
        return dbActivityDefDevice.getCommandDelayTime();
    }

    @Override
    public Integer commandSendTimeout() {
        return dbActivityDefDevice.getCommandSendTimeout();
    }

    @Override
    public Boolean isCanOffLineSend() {
        return dbActivityDefDevice.getCanOffLineSend().equals(CommonYesNoEnum.YES)?true:false;
    }

    @Override
    public ActivityDefDevicePerformSequence getPerformSequence() {
        return dbActivityDefDevice.getPerformSequence();
    }

    @Override
    public ActivityDefDevicePerformtype getPerformType() {
        return dbActivityDefDevice.getPerformType();
    }

    @Override
    public Boolean isCanTakeBack() {
        return dbActivityDefDevice.getCanTakeBack().equals(CommonYesNoEnum.YES)?true:false;
    }

    @Override
    public Boolean isCanReSend() {
        return dbActivityDefDevice.getCanReSend().equals(CommonYesNoEnum.YES)?true:false;
    }

    @Override
    public String getEndpointSelectedId() {
        return dbActivityDefDevice.getEndpointSelectedId();
    }

    @Override
    public String getCommandSelectedId() {
        return dbActivityDefDevice.getCommandSelectedId();
    }

    @Override
    public ActivityDefDeviceSpecial getSpecialSendScope() {
        return dbActivityDefDevice.getSpecialSendScope();
    }

    @Override
    public List<DeviceEndPoint> getEndpoints() throws BPMException {

        return this.deviceEngine.getParticipant(dbActivityDefDevice.getEndpointSelectedAtt(), null);
    }

    @Override
    public List<Command> getCommand() throws BPMException {
        return this.deviceEngine.getParticipant(dbActivityDefDevice.getEndpointSelectedAtt(), null);
    }
}
