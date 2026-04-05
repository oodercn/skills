package net.ooder.agent.client.command;

import net.ooder.command.Command;

public class SensorCommand implements Command {
    private String commandId;
    private String name;
    private String gatewayieee;
    private String sensorieee;

    @Override
    public String getCommandId() {
        return commandId;
    }

    @Override
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGatewayieee(String gatewayieee) {
        this.gatewayieee = gatewayieee;
    }

    public String getGatewayieee() {
        return gatewayieee;
    }

    public String getSensorieee() {
        return sensorieee;
    }

    public void setSensorieee(String sensorieee) {
        this.sensorieee = sensorieee;
    }
}
