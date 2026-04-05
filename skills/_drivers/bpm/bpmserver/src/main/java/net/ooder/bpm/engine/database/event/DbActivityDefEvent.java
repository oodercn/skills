/**
 * $RCSfile: DbActivityDefRight.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.event;

import net.ooder.annotation.DurationUnit;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation;
import net.ooder.bpm.enums.event.DeviceAPIEventEnums;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认设备模型使用的活动设备任务定义数据封装类
 * </p>
 * <p>
 * 此类数据来自于活动定义的扩展属性
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author lwz
 * @version 1.0
 */
public class DbActivityDefEvent implements Cacheable, Serializable {

    private String activityDefId = null;

    private DeviceAPIEventEnums deviceEvent = null;


    private DeviceDataTypeKey attributName = null;


    private String endpointSelectedId = null;

    private DurationUnit durationUnit;

    private String alertTime;

    private ActivityDefDeadLineOperation deadLineOperation;

    private EIAttributeDef endpointSelectedAtt = null;

    DbActivityDefEvent() {

    }

    public DeviceDataTypeKey getAttributName() {
        return attributName;
    }

    public void setAttributName(DeviceDataTypeKey attributName) {
        this.attributName = attributName;
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


    /**
     * @return Returns the activityDefId.
     */
    public String getActivityDefId() {
        return activityDefId;
    }

    /**
     * @param activityDefId The activityDefId to set.
     */
    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }


    public DeviceAPIEventEnums getDeviceEvent() {
        return deviceEvent;
    }

    public void setDeviceEvent(DeviceAPIEventEnums activityEventEnums) {
        this.deviceEvent = activityEventEnums;
    }


    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }

    public ActivityDefDeadLineOperation getDeadLineOperation() {
        return deadLineOperation;
    }

    public void setDeadLineOperation(ActivityDefDeadLineOperation deadLineOperation) {
        this.deadLineOperation = deadLineOperation;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {

        int size = 0;
        size += CacheSizes.sizeOfString(deadLineOperation.getType());

        size += CacheSizes.sizeOfString(alertTime);

        size += CacheSizes.sizeOfString(durationUnit.getType());

        size += CacheSizes.sizeOfString(activityDefId);

        size += CacheSizes.sizeOfString(attributName.getType());

        size += CacheSizes.sizeOfString(endpointSelectedId);

        size += CacheSizes.sizeOfString(deviceEvent.getMethod());

        return size;
    }

}


