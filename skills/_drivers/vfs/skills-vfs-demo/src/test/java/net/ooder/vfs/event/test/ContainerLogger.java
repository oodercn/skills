package net.ooder.vfs.event.test;

import java.io.*;
import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ContainerLogger {

    private final String containerId;
    private final PrintWriter writer;
    private final AtomicLong logCount = new AtomicLong(0);
    private final ConcurrentLinkedQueue<String> logEntries = new ConcurrentLinkedQueue<>();

    private final File logFile;

    public ContainerLogger(String containerId, String logDir) {
        this.containerId = containerId;
        File dir = new File(logDir);
        dir.mkdirs();
        this.logFile = new File(dir, containerId + ".log");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)), true);
        } catch (IOException e) {
            System.err.println("Failed to create log file: " + logFile.getAbsolutePath());
            pw = new PrintWriter(System.out, true);
        }
        this.writer = pw;
        info("ContainerLogger initialized for " + containerId);
    }

    public void info(String msg) {
        String entry = format("INFO", msg);
        writer.println(entry);
        logEntries.add(entry);
        logCount.incrementAndGet();
    }

    public void sent(String eventId, String eventType, String detail) {
        String entry = format("SENT", eventId + " " + eventType + " " + detail);
        writer.println(entry);
        logEntries.add(entry);
        logCount.incrementAndGet();
    }

    public void received(String eventId, String eventType, String source, String detail) {
        String entry = format("RECV", eventId + " from=" + source + " " + eventType + " " + detail);
        writer.println(entry);
        logEntries.add(entry);
        logCount.incrementAndGet();
    }

    public void consumed(String eventId, String eventType, String content, String threadName, String objectRef) {
        String entry = format("CONS", eventId + " " + eventType + " content=[" + content + "] thread=" + threadName + " ref=" + objectRef);
        writer.println(entry);
        logEntries.add(entry);
        logCount.incrementAndGet();
    }

    public void deepCopyCheck(String eventId, int sentIdentity, int receivedIdentity, boolean isDifferent) {
        String entry = format("COPY", eventId + " sentRef=" + sentIdentity + " recvRef=" + receivedIdentity + " isDifferent=" + isDifferent);
        writer.println(entry);
        logEntries.add(entry);
        logCount.incrementAndGet();
    }

    private String format(String level, String msg) {
        return String.format("[%s][%s][%s] %s", Instant.now(), containerId, level, msg);
    }

    public long getLogCount() { return logCount.get(); }

    public void dumpSummary() {
        writer.println("=== SUMMARY for " + containerId + " ===");
        writer.println("Total log entries: " + logCount.get());
        writer.flush();
    }

    public void close() {
        dumpSummary();
        writer.close();
    }

    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }
}
