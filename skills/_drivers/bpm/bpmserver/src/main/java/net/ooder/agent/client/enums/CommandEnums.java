package net.ooder.agent.client.enums;

import net.ooder.command.Command;

public enum CommandEnums {
    SEND("send", net.ooder.agent.client.command.SensorCommand.class),
    RECEIVE("receive", net.ooder.agent.client.command.SensorCommand.class),
    EXECUTE("execute", net.ooder.agent.client.command.SensorCommand.class),
    QUERY("query", net.ooder.agent.client.command.SensorCommand.class);

    private final String name;
    private final Class<? extends Command> commandClass;

    CommandEnums(String name, Class<? extends Command> commandClass) {
        this.name = name;
        this.commandClass = commandClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Command> getCommand() {
        return commandClass;
    }

    public static CommandEnums fromByName(String name) {
        for (CommandEnums e : values()) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return SEND;
    }
}
