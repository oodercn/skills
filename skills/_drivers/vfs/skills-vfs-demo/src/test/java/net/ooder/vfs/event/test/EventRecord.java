package net.ooder.vfs.event.test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class EventRecord {

    private final String nodeId;
    private final Queue<RecordEntry> sentEvents = new ConcurrentLinkedQueue<>();
    private final Queue<RecordEntry> receivedEvents = new ConcurrentLinkedQueue<>();
    private final Map<String, ConsumedEntry> consumedById = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> sentCountByType = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> receivedCountByType = new ConcurrentHashMap<>();
    private final AtomicLong totalSent = new AtomicLong(0);
    private final AtomicLong totalReceived = new AtomicLong(0);
    private final AtomicLong totalConsumed = new AtomicLong(0);
    private final Map<String, RecordEntry> sentById = new ConcurrentHashMap<>();
    private final Map<String, RecordEntry> receivedById = new ConcurrentHashMap<>();
    private final Map<String, Integer> sentObjectIdentityById = new ConcurrentHashMap<>();
    private final Map<String, Integer> receivedObjectIdentityById = new ConcurrentHashMap<>();
    private final Map<String, String> sentContentSignatureById = new ConcurrentHashMap<>();
    private final List<Long> sentTimestamps = Collections.synchronizedList(new ArrayList<>());
    private final List<Long> receivedTimestamps = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock writeLock = new ReentrantLock();
    private volatile boolean offline = false;
    private final Queue<RecordEntry> offlineBuffer = new ConcurrentLinkedQueue<>();

    public EventRecord(String nodeId) {
        this.nodeId = nodeId;
    }

    public void recordSent(String eventId, String eventType, String targetNode, Object event) {
        writeLock.lock();
        try {
            RecordEntry entry = new RecordEntry(eventId, eventType, nodeId, targetNode, System.currentTimeMillis(), event);
            sentEvents.add(entry);
            sentById.put(eventId, entry);
            sentObjectIdentityById.put(eventId, System.identityHashCode(event));
            sentContentSignatureById.put(eventId, buildContentSignature(event));
            sentTimestamps.add(entry.timestamp);
            sentCountByType.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
            totalSent.incrementAndGet();
        } finally {
            writeLock.unlock();
        }
    }

    public void recordReceived(String eventId, String eventType, String sourceNode, Object event) {
        writeLock.lock();
        try {
            if (offline) {
                offlineBuffer.add(new RecordEntry(eventId, eventType, sourceNode, nodeId, System.currentTimeMillis(), event));
                return;
            }
            RecordEntry entry = new RecordEntry(eventId, eventType, sourceNode, nodeId, System.currentTimeMillis(), event);
            receivedEvents.add(entry);
            receivedById.put(eventId, entry);
            receivedObjectIdentityById.put(eventId, System.identityHashCode(event));
            receivedTimestamps.add(entry.timestamp);
            receivedCountByType.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
            totalReceived.incrementAndGet();
        } finally {
            writeLock.unlock();
        }
    }

    public void recordConsumed(String eventId, String eventType, String consumedContent) {
        writeLock.lock();
        try {
            consumedById.put(eventId, new ConsumedEntry(eventId, eventType, consumedContent, System.currentTimeMillis()));
            totalConsumed.incrementAndGet();
        } finally {
            writeLock.unlock();
        }
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isOffline() {
        return offline;
    }

    public int flushOfflineBuffer() {
        writeLock.lock();
        try {
            int count = 0;
            RecordEntry entry;
            while ((entry = offlineBuffer.poll()) != null) {
                receivedEvents.add(entry);
                receivedById.put(entry.eventId, entry);
                receivedTimestamps.add(entry.timestamp);
                receivedCountByType.computeIfAbsent(entry.eventType, k -> new AtomicLong()).incrementAndGet();
                totalReceived.incrementAndGet();
                count++;
            }
            return count;
        } finally {
            writeLock.unlock();
        }
    }

    public int getOfflineBufferCount() {
        return offlineBuffer.size();
    }

    private String buildContentSignature(Object event) {
        if (event == null) return "null";
        if (event instanceof net.ooder.vfs.event.VfsFileEvent) {
            net.ooder.vfs.event.VfsFileEvent fe = (net.ooder.vfs.event.VfsFileEvent) event;
            return "VfsFileEvent:" + fe.getFileId() + "|" + fe.getFileName() + "|" + fe.getFolderId() + "|" + fe.getAction();
        }
        if (event instanceof net.ooder.vfs.event.VfsFolderEvent) {
            net.ooder.vfs.event.VfsFolderEvent fe = (net.ooder.vfs.event.VfsFolderEvent) event;
            return "VfsFolderEvent:" + fe.getFolderId() + "|" + fe.getFolderName() + "|" + fe.getParentId() + "|" + fe.getAction();
        }
        if (event instanceof net.ooder.vfs.event.VfsSyncEvent) {
            net.ooder.vfs.event.VfsSyncEvent se = (net.ooder.vfs.event.VfsSyncEvent) event;
            return "VfsSyncEvent:" + se.getAction() + "|" + se.getLocalPath() + "|" + se.getVfsPath();
        }
        return event.getClass().getSimpleName() + ":" + System.identityHashCode(event);
    }

    public boolean verifyContentIntegrity(String eventId, Object receivedEvent) {
        String sentSig = sentContentSignatureById.get(eventId);
        if (sentSig == null) return false;
        String receivedSig = buildContentSignature(receivedEvent);
        return sentSig.equals(receivedSig);
    }

    public boolean verifyReceivedEventFromSource(String eventId, String expectedSource) {
        RecordEntry rec = receivedById.get(eventId);
        return rec != null && expectedSource.equals(rec.fromNode);
    }

    public boolean verifyConsumedContent(String eventId, String expectedContentFragment) {
        ConsumedEntry ce = consumedById.get(eventId);
        return ce != null && ce.consumedContent.contains(expectedContentFragment);
    }

    public boolean verifyConsumedField(String eventId, String fieldName, String expectedValue) {
        ConsumedEntry ce = consumedById.get(eventId);
        if (ce == null) return false;
        String[] pairs = ce.consumedContent.split(", ");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(fieldName)) {
                return kv[1].equals(expectedValue);
            }
        }
        return false;
    }

    public boolean verifyConsumedFieldStartsWith(String eventId, String fieldName, String prefix) {
        ConsumedEntry ce = consumedById.get(eventId);
        if (ce == null) return false;
        String[] pairs = ce.consumedContent.split(", ");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(fieldName)) {
                return kv[1].startsWith(prefix);
            }
        }
        return false;
    }

    public boolean verifyReceivedEventField(String eventId, String fieldSignature) {
        RecordEntry rec = receivedById.get(eventId);
        if (rec == null || rec.event == null) return false;
        String sig = buildContentSignature(rec.event);
        return sig.contains(fieldSignature);
    }

    public boolean verifyDeepCopyDifferentObject(String eventId) {
        Integer sentIdentity = sentObjectIdentityById.get(eventId);
        Integer receivedIdentity = receivedObjectIdentityById.get(eventId);
        if (sentIdentity == null || receivedIdentity == null) return false;
        return !sentIdentity.equals(receivedIdentity);
    }

    public int getSentObjectIdentity(String eventId) {
        Integer i = sentObjectIdentityById.get(eventId);
        return i != null ? i : 0;
    }

    public int getReceivedObjectIdentity(String eventId) {
        Integer i = receivedObjectIdentityById.get(eventId);
        return i != null ? i : 0;
    }

    public List<String> verifyReceivedOrderPreserved(String eventType, String fromNode) {
        List<String> violations = new ArrayList<>();
        List<RecordEntry> filtered = new ArrayList<>();
        for (RecordEntry e : receivedEvents) {
            if (eventType.equals(e.eventType) && (fromNode == null || fromNode.equals(e.fromNode))) {
                filtered.add(e);
            }
        }
        for (int i = 1; i < filtered.size(); i++) {
            if (filtered.get(i).timestamp < filtered.get(i - 1).timestamp) {
                violations.add(String.format("%s from %s: t[%d]=%d < t[%d]=%d",
                    eventType, fromNode, i, filtered.get(i).timestamp, i - 1, filtered.get(i - 1).timestamp));
            }
        }
        return violations;
    }

    public Map<String, List<RecordEntry>> getReceivedBySource() {
        Map<String, List<RecordEntry>> bySource = new HashMap<>();
        for (RecordEntry e : receivedEvents) {
            bySource.computeIfAbsent(e.fromNode, k -> new ArrayList<>()).add(e);
        }
        return bySource;
    }

    public Set<String> findMissingReceivedIds(Set<String> expectedIds) {
        Set<String> missing = new HashSet<>(expectedIds);
        missing.removeAll(receivedById.keySet());
        return missing;
    }

    public Set<String> findMissingConsumedIds(Set<String> expectedIds) {
        Set<String> missing = new HashSet<>(expectedIds);
        missing.removeAll(consumedById.keySet());
        return missing;
    }

    public Set<String> findDuplicateReceivedIds() {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (RecordEntry e : receivedEvents) {
            if (!seen.add(e.eventId)) {
                duplicates.add(e.eventId);
            }
        }
        return duplicates;
    }

    public long getTotalSent() { return totalSent.get(); }
    public long getTotalReceived() { return totalReceived.get(); }
    public long getTotalConsumed() { return totalConsumed.get(); }
    public String getNodeId() { return nodeId; }

    public long getSentCountByType(String eventType) {
        AtomicLong c = sentCountByType.get(eventType);
        return c != null ? c.get() : 0;
    }

    public long getReceivedCountByType(String eventType) {
        AtomicLong c = receivedCountByType.get(eventType);
        return c != null ? c.get() : 0;
    }

    public List<RecordEntry> getSentEvents() { return new ArrayList<>(sentEvents); }
    public List<RecordEntry> getReceivedEvents() { return new ArrayList<>(receivedEvents); }

    public Set<String> getSentEventIds() {
        Set<String> ids = new HashSet<>();
        for (RecordEntry e : sentEvents) ids.add(e.eventId);
        return ids;
    }

    public Set<String> getReceivedEventIds() {
        Set<String> ids = new HashSet<>();
        for (RecordEntry e : receivedEvents) ids.add(e.eventId);
        return ids;
    }

    public RecordEntry getSentEntry(String eventId) { return sentById.get(eventId); }
    public RecordEntry getReceivedEntry(String eventId) { return receivedById.get(eventId); }
    public ConsumedEntry getConsumedEntry(String eventId) { return consumedById.get(eventId); }

    public void clear() {
        sentEvents.clear();
        receivedEvents.clear();
        consumedById.clear();
        sentCountByType.clear();
        receivedCountByType.clear();
        totalSent.set(0);
        totalReceived.set(0);
        totalConsumed.set(0);
        sentById.clear();
        receivedById.clear();
        sentObjectIdentityById.clear();
        receivedObjectIdentityById.clear();
        sentContentSignatureById.clear();
        sentTimestamps.clear();
        receivedTimestamps.clear();
        offlineBuffer.clear();
        offline = false;
    }

    public String summary() {
        return String.format("[%s] sent=%d, received=%d, consumed=%d, offline_buf=%d",
            nodeId, totalSent.get(), totalReceived.get(), totalConsumed.get(), offlineBuffer.size());
    }

    public static class RecordEntry {
        public final String eventId;
        public final String eventType;
        public final String fromNode;
        public final String toNode;
        public final long timestamp;
        public final Object event;

        public RecordEntry(String eventId, String eventType, String fromNode, String toNode, long timestamp, Object event) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.timestamp = timestamp;
            this.event = event;
        }

        @Override
        public String toString() {
            return String.format("Record[%s|%s|%s->%s@%d]", eventId, eventType, fromNode, toNode, timestamp);
        }
    }

    public static class ConsumedEntry {
        public final String eventId;
        public final String eventType;
        public final String consumedContent;
        public final long timestamp;

        public ConsumedEntry(String eventId, String eventType, String consumedContent, long timestamp) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.consumedContent = consumedContent;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("Consumed[%s|%s|%s@%d]", eventId, eventType, consumedContent, timestamp);
        }
    }
}
