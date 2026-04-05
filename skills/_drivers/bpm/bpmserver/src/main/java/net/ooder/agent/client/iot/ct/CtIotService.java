package net.ooder.agent.client.iot.ct;

import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.command.Command;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CtIotService {
    
    public List<DeviceEndPoint> getEndPointByPerson(String personId) {
        return new ArrayList<>();
    }

    public DeviceEndPoint getEndPointById(String endPointId) throws JDSException, HomeException {
        return null;
    }

    public DeviceEndPoint getEndPointByIeee(String ieee) {
        return null;
    }

    public Command getCommandById(String commandId) {
        return null;
    }

    public CompletableFuture<Command> sendCommand(Command command, Integer delayTime) {
        return CompletableFuture.completedFuture(command);
    }
}
