package net.ooder.agent.client.iot.ct;

import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;

public class CtIotFactory {
    private static CtIotFactory instance;
    private static CtIotService ctIotService;

    private CtIotFactory() {
    }

    public static CtIotFactory getInstance() {
        if (instance == null) {
            instance = new CtIotFactory();
        }
        return instance;
    }

    public CtIotService getService() {
        if (ctIotService == null) {
            ctIotService = new CtIotService();
        }
        return ctIotService;
    }

    public static CtIotService getCtIotService() {
        return getInstance().getService();
    }

    public static List<DeviceEndPoint> getEndPointByPerson(String personId) {
        return getInstance().getService().getEndPointByPerson(personId);
    }

    public static DeviceEndPoint getEndPointById(String endPointId) throws JDSException, HomeException {
        return getInstance().getService().getEndPointById(endPointId);
    }

    public static DeviceEndPoint getEndPointByIeee(String ieee) {
        return getInstance().getService().getEndPointByIeee(ieee);
    }
}
