package net.ooder.mvp.api.circuitbreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreakerState {
    public static final String CLOSED = "CLOSED";
    public static final String OPEN = "OPEN";
    public static final String HALF_OPEN = "HALF_OPEN";
}

