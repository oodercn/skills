package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 定时配置DTO
 */
public class TimingDTO {

    @JSONField(name = "limitTime")
    private Integer limitTime;

    @JSONField(name = "alertTime")
    private Integer alertTime;

    @JSONField(name = "durationUnit")
    private String durationUnit;

    @JSONField(name = "startTime")
    private String startTime;

    @JSONField(name = "endTime")
    private String endTime;

    @JSONField(name = "remindType")
    private String remindType;

    @JSONField(name = "remindInterval")
    private Integer remindInterval;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public Integer getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Integer limitTime) {
        this.limitTime = limitTime;
    }

    public Integer getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Integer alertTime) {
        this.alertTime = alertTime;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    public Integer getRemindInterval() {
        return remindInterval;
    }

    public void setRemindInterval(Integer remindInterval) {
        this.remindInterval = remindInterval;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}
