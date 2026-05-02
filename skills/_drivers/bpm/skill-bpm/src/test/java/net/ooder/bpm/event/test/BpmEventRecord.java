package net.ooder.bpm.event.test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class BpmEventRecord {

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
    private volatile boolean offline = false;

    public BpmEventRecord(String nodeId) {
        this.nodeId = nodeId;
    }

    public void recordSent(String eventId, String eventType, String targetNode, Object event) {
        RecordEntry entry = new RecordEntry(eventId, eventType, nodeId, targetNode, System.currentTimeMillis(), event);
        sentEvents.add(entry);
        sentById.put(eventId, entry);
        sentObjectIdentityById.put(eventId, System.identityHashCode(event));
        sentCountByType.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        totalSent.incrementAndGet();
    }

    public void recordReceived(String eventId, String eventType, String sourceNode, Object event) {
        if (offline) return;
        RecordEntry entry = new RecordEntry(eventId, eventType, sourceNode, nodeId, System.currentTimeMillis(), event);
        receivedEvents.add(entry);
        receivedById.put(eventId, entry);
        receivedObjectIdentityById.put(eventId, System.identityHashCode(event));
        receivedCountByType.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        totalReceived.incrementAndGet();
    }

    public void recordConsumed(String eventId, String eventType, Map<String, String> extractedFields) {
        consumedById.put(eventId, new ConsumedEntry(eventId, eventType, extractedFields, System.currentTimeMillis()));
        totalConsumed.incrementAndGet();
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isOffline() {
        return offline;
    }

    public boolean verifyConsumedField(String eventId, String fieldName, String expectedValue) {
        ConsumedEntry ce = consumedById.get(eventId);
        if (ce == null) return false;
        String actual = ce.extractedFields.get(fieldName);
        return expectedValue.equals(actual);
    }

    public boolean verifyConsumedFieldStartsWith(String eventId, String fieldName, String prefix) {
        ConsumedEntry ce = consumedById.get(eventId);
        if (ce == null) return false;
        String actual = ce.extractedFields.get(fieldName);
        return actual != null && actual.startsWith(prefix);
    }

    public String getConsumedFieldValue(String eventId, String fieldName) {
        ConsumedEntry ce = consumedById.get(eventId);
        return ce != null ? ce.extractedFields.get(fieldName) : null;
    }

    public Map<String, String> getConsumedFields(String eventId) {
        ConsumedEntry ce = consumedById.get(eventId);
        return ce != null ? Collections.unmodifiableMap(ce.extractedFields) : Collections.emptyMap();
    }

    public Integer getSentObjectIdentity(String eventId) {
        return sentObjectIdentityById.get(eventId);
    }

    public Integer getReceivedObjectIdentity(String eventId) {
        return receivedObjectIdentityById.get(eventId);
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
        offline = false;
    }

    public String summary() {
        return String.format("[%s] sent=%d, received=%d, consumed=%d",
            nodeId, totalSent.get(), totalReceived.get(), totalConsumed.get());
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
        public final Map<String, String> extractedFields;
        public final long timestamp;

        public ConsumedEntry(String eventId, String eventType, Map<String, String> extractedFields, long timestamp) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.extractedFields = new LinkedHashMap<>(extractedFields);
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("Consumed[%s|%s|%s@%d]", eventId, eventType, extractedFields, timestamp);
        }
    }
}
