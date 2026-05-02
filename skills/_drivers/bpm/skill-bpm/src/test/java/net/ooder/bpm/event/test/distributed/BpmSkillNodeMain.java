package net.ooder.bpm.event.test.distributed;

import java.io.*;
import java.net.*;

public class BpmSkillNodeMain {

    private static BpmSkillNode node;
    private static volatile boolean running = true;

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: BpmSkillNodeMain <nodeId> <broadcastPort> <controlPort> <tcpEventPort>");
            System.exit(1);
        }

        String nodeId = args[0];
        int broadcastPort = Integer.parseInt(args[1]);
        int controlPort = Integer.parseInt(args[2]);
        int tcpEventPort = Integer.parseInt(args[3]);

        System.out.println("[" + nodeId + "] Starting BpmSkillNode (ooderAgent SDK): broadcast=" + broadcastPort +
            ", control=" + controlPort + ", tcpEvent=" + tcpEventPort);

        node = new BpmSkillNode(nodeId, broadcastPort, controlPort, tcpEventPort);
        node.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            node.stop();
            System.out.println("[" + nodeId + "] Node shutdown complete");
        }));

        while (running) {
            Thread.sleep(1000);
        }
    }
}
