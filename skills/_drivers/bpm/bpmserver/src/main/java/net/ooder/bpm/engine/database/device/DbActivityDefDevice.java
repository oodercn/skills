
package net.ooder.bpm.engine.database.device;

import java.io.Serializable;

import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformSequence;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDeviceSpecial;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.command.CommandRetry;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

public class DbActivityDefDevice implements Cacheable, Serializable {

    private String activityDefId = null;

    private CommandExecType commandExecType = CommandExecType.MULTIPLENOWITE;

    private CommandRetry commandRetry = CommandRetry.GOON;

    private Integer commandExecRetryTimes = 3;

    private Integer commandDelayTime = 0;

    private Integer commandSendTimeout = 0;

    private CommonYesNoEnum canOffLineSend = null;

    public Integer getCommandDelayTime() {
	return commandDelayTime;
    }

    public void setCommandDelayTime(Integer commandDelayTime) {
	this.commandDelayTime = commandDelayTime;
    }

    private ActivityDefDevicePerformSequence performSequence = ActivityDefDevicePerformSequence.AUTOSIGN;

    private ActivityDefDevicePerformtype performType = ActivityDefDevicePerformtype.SINGLE;

    private CommonYesNoEnum canTakeBack = null;

    private CommonYesNoEnum canReSend = null;

    private String endpointSelectedId = null;

    private String commandSelectedId = null;

    private ActivityDefDeviceSpecial specialSendScope = ActivityDefDeviceSpecial.ALL;

    private EIAttributeDef endpointSelectedAtt = null;

    private EIAttributeDef commandSelectedAtt = null;

    DbActivityDefDevice() {
	loadFromDb();
    }

    void loadFromDb() {

    }

    public String getEndpointSelectedId() {
	return endpointSelectedId;
    }

    public void setEndpointSelectedId(String endpointSelectedId) {
	this.endpointSelectedId = endpointSelectedId;
    }

    public EIAttributeDef getEndpointSelectedAtt() {
	return endpointSelectedAtt;
    }

    public void setEndpointSelectedAtt(EIAttributeDef endpointSelectedAtt) {
	this.endpointSelectedAtt = endpointSelectedAtt;
    }

    public ActivityDefDevicePerformSequence getPerformSequence() {
	return performSequence;
    }

    public void setPerformSequence(ActivityDefDevicePerformSequence performSequence) {
	this.performSequence = performSequence;
    }

    /**
     * @return Returns the activityDefId.
     */
    public String getActivityDefId() {
	return activityDefId;
    }

    /**
     * @param activityDefId
     *            The activityDefId to set.
     */
    public void setActivityDefId(String activityDefId) {
	this.activityDefId = activityDefId;
    }

    public ActivityDefDevicePerformtype getPerformType() {
	return performType;
    }

    public void setPerformType(ActivityDefDevicePerformtype performType) {
	this.performType = performType;
    }

    public String getCommandSelectedId() {
	return commandSelectedId;
    }

    public void setCommandSelectedId(String commandSelectedId) {
	this.commandSelectedId = commandSelectedId;
    }

    public EIAttributeDef getCommandSelectedAtt() {
	return commandSelectedAtt;
    }

    /**
     * @param commandSelectedAtt
     *            The commandSelectedAtt to set.
     */
    public void setCommandSelectedAtt(EIAttributeDef commandSelectedAtt) {
	this.commandSelectedAtt = commandSelectedAtt;
    }

    /**
     * @return Returns the specialSendScope.
     */
    public ActivityDefDeviceSpecial getSpecialSendScope() {
	return specialSendScope;
    }

    /**
     * @param specialSendScope
     *            The specialSendScope to set.
     */
    public void setSpecialSendScope(ActivityDefDeviceSpecial specialSendScope) {
	this.specialSendScope = specialSendScope;
    }

    public CommonYesNoEnum getCanReSend() {
	return canReSend;
    }

    public void setCanReSend(CommonYesNoEnum canReSend) {
	this.canReSend = canReSend;
    }

    public CommonYesNoEnum getCanTakeBack() {
	return canTakeBack;
    }

    public void setCanTakeBack(CommonYesNoEnum canTakeBack) {
	this.canTakeBack = canTakeBack;
    }

    public CommandExecType getCommandExecType() {
	return commandExecType;
    }

    public void setCommandExecType(CommandExecType commandExecType) {
	this.commandExecType = commandExecType;
    }

    public CommandRetry getCommandRetry() {
	return commandRetry;
    }

    public void setCommandRetry(CommandRetry commandRetry) {
	this.commandRetry = commandRetry;
    }

    public Integer getCommandExecRetryTimes() {
	return commandExecRetryTimes;
    }

    public void setCommandExecRetryTimes(Integer commandExecRetryTimes) {
	this.commandExecRetryTimes = commandExecRetryTimes;
    }

    public Integer getCommandSendTimeout() {
	return commandSendTimeout;
    }

    public void setCommandSendTimeout(Integer commandSendTimeout) {
	this.commandSendTimeout = commandSendTimeout;
    }

    public CommonYesNoEnum getCanOffLineSend() {
	return canOffLineSend;
    }

    public void setCanOffLineSend(CommonYesNoEnum canOffLineSend) {
	this.canOffLineSend = canOffLineSend;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {

	int size = 0;

	size += CacheSizes.sizeOfString(activityDefId);
	size += CacheSizes.sizeOfString(performType.getType());
	size += CacheSizes.sizeOfString(performSequence.getType());
	size += CacheSizes.sizeOfString(specialSendScope.getType());

	size += CacheSizes.sizeOfString(endpointSelectedId);
	size += CacheSizes.sizeOfString(commandSelectedId);
	size += CacheSizes.sizeOfString(this.commandDelayTime.toString());
	size += CacheSizes.sizeOfString(this.canReSend.getType());
	size += CacheSizes.sizeOfString(this.canTakeBack.getType());

	size += CacheSizes.sizeOfString(this.commandExecType.getType());
	size += CacheSizes.sizeOfString(this.commandRetry.getType());
	size += CacheSizes.sizeOfObject(this.commandExecRetryTimes);
	size += CacheSizes.sizeOfObject(this.commandSendTimeout);

	return size;
    }

}


