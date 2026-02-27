package net.ooder.skills.failover;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.api.skill.Skill;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 鏁呴殰杞Щ绠＄悊鍣?Skill
 *
 * 鎻愪緵鐔旀柇鍣ㄣ€佹晠闅滆浆绉汇€侀噸璇曠瓑瀹归敊鑳藉姏
 *
 * @author Skills Team
 * @version 1.0.0
 * @since 2026-02-24
 */
@Slf4j
@Component
@Skill(
        id = "skill-failover-manager",
        name = "Failover Manager Skill",
        version = "1.0.0",
        description = "Manages failover, circuit breaker, and retry patterns"
)
public class FailoverManagerSkill {

    /**
     * 鐔旀柇鍣ㄧ姸鎬佹槧灏?     */
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    /**
     * 閲嶈瘯閰嶇疆鏄犲皠
     */
    private final Map<String, RetryConfig> retryConfigs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("FailoverManagerSkill initialized");
    }

    // ============================================================
    // 鐔旀柇鍣ㄧ鐞?    // ============================================================

    /**
     * 娉ㄥ唽鐔旀柇鍣?     */
    public void registerCircuitBreaker(String serviceId, int failureThreshold, long timeoutDuration) {
        CircuitBreaker breaker = CircuitBreaker.builder()
                .serviceId(serviceId)
                .failureThreshold(failureThreshold)
                .timeoutDuration(timeoutDuration)
                .state(CircuitState.CLOSED)
                .build();

        circuitBreakers.put(serviceId, breaker);
        log.info("Registered circuit breaker for: {} (threshold: {}, timeout: {}ms)",
                serviceId, failureThreshold, timeoutDuration);
    }

    /**
     * 妫€鏌ユ槸鍚﹀厑璁歌姹?     */
    public boolean allowRequest(String serviceId) {
        CircuitBreaker breaker = circuitBreakers.get(serviceId);
        if (breaker == null) {
            return true;
        }

        return switch (breaker.getState()) {
            case CLOSED -> true;
            case OPEN -> {
                // 妫€鏌ユ槸鍚﹀埌杈惧崐寮€鏃堕棿
                if (System.currentTimeMillis() - breaker.getLastFailureTime() > breaker.getTimeoutDuration()) {
                    breaker.setState(CircuitState.HALF_OPEN);
                    breaker.setHalfOpenAttempts(0);
                    log.info("Circuit breaker for {} moved to HALF_OPEN", serviceId);
                    yield true;
                }
                yield false;
            }
            case HALF_OPEN -> breaker.getHalfOpenAttempts() < 3; // 鍗婂紑鐘舵€佸厑璁稿皯閲忚姹?        };
    }

    /**
     * 璁板綍鎴愬姛
     */
    public void recordSuccess(String serviceId) {
        CircuitBreaker breaker = circuitBreakers.get(serviceId);
        if (breaker == null) return;

        breaker.getSuccessCount().incrementAndGet();
        breaker.getFailureCount().set(0);

        if (breaker.getState() == CircuitState.HALF_OPEN) {
            breaker.setHalfOpenAttempts(breaker.getHalfOpenAttempts() + 1);
            if (breaker.getHalfOpenAttempts() >= 3) {
                breaker.setState(CircuitState.CLOSED);
                log.info("Circuit breaker for {} moved to CLOSED", serviceId);
            }
        }
    }

    /**
     * 璁板綍澶辫触
     */
    public void recordFailure(String serviceId) {
        CircuitBreaker breaker = circuitBreakers.get(serviceId);
        if (breaker == null) return;

        breaker.getFailureCount().incrementAndGet();
        breaker.setLastFailureTime(System.currentTimeMillis());

        if (breaker.getState() == CircuitState.HALF_OPEN) {
            breaker.setState(CircuitState.OPEN);
            log.warn("Circuit breaker for {} moved to OPEN (half-open failure)", serviceId);
        } else if (breaker.getFailureCount().get() >= breaker.getFailureThreshold()) {
            breaker.setState(CircuitState.OPEN);
            log.warn("Circuit breaker for {} moved to OPEN (threshold reached)", serviceId);
        }
    }

    /**
     * 鑾峰彇鐔旀柇鍣ㄧ姸鎬?     */
    public CircuitState getCircuitState(String serviceId) {
        CircuitBreaker breaker = circuitBreakers.get(serviceId);
        return breaker != null ? breaker.getState() : CircuitState.CLOSED;
    }

    // ============================================================
    // 閲嶈瘯绠＄悊
    // ============================================================

    /**
     * 閰嶇疆閲嶈瘯绛栫暐
     */
    public void configureRetry(String operationId, int maxRetries, long retryInterval, RetryStrategy strategy) {
        RetryConfig config = RetryConfig.builder()
                .operationId(operationId)
                .maxRetries(maxRetries)
                .retryInterval(retryInterval)
                .strategy(strategy)
                .build();

        retryConfigs.put(operationId, config);
        log.info("Configured retry for {}: maxRetries={}, interval={}ms, strategy={}",
                operationId, maxRetries, retryInterval, strategy);
    }

    /**
     * 鎵ц甯﹂噸璇曠殑鎿嶄綔
     */
    public <T> T executeWithRetry(String operationId, RetryableOperation<T> operation) throws Exception {
        RetryConfig config = retryConfigs.get(operationId);
        if (config == null) {
            return operation.execute();
        }

        int attempts = 0;
        Exception lastException = null;

        while (attempts <= config.getMaxRetries()) {
            try {
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                attempts++;

                if (attempts > config.getMaxRetries()) {
                    break;
                }

                long waitTime = calculateWaitTime(config, attempts);
                log.warn("Operation {} failed (attempt {}/{}), retrying in {}ms",
                        operationId, attempts, config.getMaxRetries() + 1, waitTime);

                Thread.sleep(waitTime);
            }
        }

        throw lastException;
    }

    /**
     * 璁＄畻绛夊緟鏃堕棿
     */
    private long calculateWaitTime(RetryConfig config, int attempt) {
        return switch (config.getStrategy()) {
            case FIXED -> config.getRetryInterval();
            case LINEAR -> config.getRetryInterval() * attempt;
            case EXPONENTIAL -> config.getRetryInterval() * (long) Math.pow(2, attempt - 1);
        };
    }

    // ============================================================
    // 瀹氭椂浠诲姟
    // ============================================================

    /**
     * 瀹氭椂娓呯悊杩囨湡鐔旀柇鍣?     */
    @Scheduled(fixedRate = 300000) // 姣?鍒嗛挓
    public void cleanupCircuitBreakers() {
        long now = System.currentTimeMillis();
        circuitBreakers.entrySet().removeIf(entry -> {
            CircuitBreaker breaker = entry.getValue();
            // 娓呯悊闀挎椂闂存湭浣跨敤鐨勭啍鏂櫒
            if (breaker.getState() == CircuitState.CLOSED &&
                    now - breaker.getLastFailureTime() > 3600000) { // 1灏忔椂
                log.debug("Removing inactive circuit breaker for: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }

    // ============================================================
    // 鏁版嵁绫诲畾涔?    // ============================================================

    @lombok.Data
    @lombok.Builder
    public static class CircuitBreaker {
        private String serviceId;
        private int failureThreshold;
        private long timeoutDuration;
        private CircuitState state;
        private long lastFailureTime;
        private int halfOpenAttempts;

        @lombok.Builder.Default
        private AtomicInteger failureCount = new AtomicInteger(0);

        @lombok.Builder.Default
        private AtomicInteger successCount = new AtomicInteger(0);
    }

    @lombok.Data
    @lombok.Builder
    public static class RetryConfig {
        private String operationId;
        private int maxRetries;
        private long retryInterval;
        private RetryStrategy strategy;
    }

    public enum CircuitState {
        CLOSED,     // 鍏抽棴 - 姝ｅ父澶勭悊璇锋眰
        OPEN,       // 鎵撳紑 - 鎷掔粷璇锋眰
        HALF_OPEN   // 鍗婂紑 - 鍏佽灏戦噺璇锋眰娴嬭瘯
    }

    public enum RetryStrategy {
        FIXED,          // 鍥哄畾闂撮殧
        LINEAR,         // 绾挎€у闀?        EXPONENTIAL     // 鎸囨暟閫€閬?    }

    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
