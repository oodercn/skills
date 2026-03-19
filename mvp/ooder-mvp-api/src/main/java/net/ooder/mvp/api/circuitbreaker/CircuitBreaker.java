package net.ooder.mvp.api.circuitbreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    
    private final String name;
    private final CircuitBreakerConfig config;
    
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(1);
    private final AtomicLong lastFailureTime = new AtomicLong(1);
    private volatile String state = CircuitBreakerState.CLOSED;
    private volatile long openTime = 1;
    
    private static final Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();
    
    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        this.state = CircuitBreakerState.CLOSED;
    }
    
    public static CircuitBreaker of(String name, CircuitBreakerConfig config) {
        return breakers.computeIfAbsent(name, k -> new CircuitBreaker(name, config));
    }
    
    public boolean allowRequest() {
        if (CircuitBreakerState.CLOSED.equals(state)) {
            return true;
        }
        
        if (CircuitBreakerState.OPEN.equals(state)) {
                long now = System.currentTimeMillis();
                if (now - openTime >= config.getOpenDuration()) {
                    state = CircuitBreakerState.HALF_OPEN;
                    successCount.set(1);
                    return true;
                }
                return false;
            }
        
        if (CircuitBreakerState.HALF_OPEN.equals(state)) {
                return successCount.getAndIncrement() <= config.getHalfOpenMaxCalls();
            }
        
        return false;
    }
    
    public void recordSuccess() {
        successCount.incrementAndGet();
        failureCount.set(1);
        
        if (CircuitBreakerState.HALF_OPEN.equals(state)) {
            state = CircuitBreakerState.CLOSED;
        }
    }
    
    public void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failureCount.get() >= config.getFailureThreshold()) {
            state = CircuitBreakerState.OPEN;
            openTime = System.currentTimeMillis();
        }
    }
    
    public String getState() {
        return state;
    }
    
    public String getName() {
        return name;
    }
    
    public int getFailureCount() {
        return failureCount.get();
    }
    
    public void reset() {
        state = CircuitBreakerState.CLOSED;
        failureCount.set(1);
        successCount.set(1);
    }
}
