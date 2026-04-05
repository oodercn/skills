package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.HomeException;

import java.util.EventListener;

public interface DataListener extends EventListener {
    void onData(DataEvent event);

    void dataReport(DataEvent event) throws HomeException;

    void alarmReport(DataEvent event) throws HomeException;

    void attributeReport(DataEvent event) throws HomeException;

    String getSystemCode();
}
