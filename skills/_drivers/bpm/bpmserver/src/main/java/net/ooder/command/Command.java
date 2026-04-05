package net.ooder.command;

public interface Command {
    String getCommandId();
    void setCommandId(String commandId);
    String getName();
    void setName(String name);
    void setGatewayieee(String gatewayieee);
}
