package net.ooder.skill.audit.service.impl;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 瀹¤鏃ュ織鏈嶅姟瀹炵幇绫?
 * 
 * <p>鎻愪緵瀹¤鏃ュ織鐨勬牳蹇冧笟鍔￠€昏緫瀹炵幇锛屼娇鐢ㄥ唴瀛樺瓨鍌紙ConcurrentHashMap锛変綔涓轰复鏃跺瓨鍌ㄦ柟妗堛€?
 * 鐢熶骇鐜寤鸿鏇挎崲涓烘寔涔呭寲瀛樺偍锛堝鏁版嵁搴撱€丒lasticsearch绛夛級銆?/p>
 * 
 * <h3>瀹炵幇鐗圭偣锛?/h3>
 * <ul>
 *   <li>绾跨▼瀹夊叏锛氫娇鐢–oncurrentHashMap淇濊瘉骞跺彂瀹夊叏</li>
 *   <li>鍐呭瓨瀛樺偍锛氶€傚悎寮€鍙戞祴璇曪紝鐢熶骇鐜闇€鏇挎崲</li>
 *   <li>娴佸紡澶勭悊锛氫娇鐢↗ava 8 Stream API杩涜鏁版嵁杩囨护鍜岀粺璁?/li>
 * </ul>
 * 
 * <h3>鎵╁睍寤鸿锛?/h3>
 * <ul>
 *   <li>鏇挎崲涓烘暟鎹簱瀛樺偍锛圡ySQL銆丳ostgreSQL绛夛級</li>
 *   <li>闆嗘垚Elasticsearch瀹炵幇楂樻晥鍏ㄦ枃妫€绱?/li>
 *   <li>娣诲姞鏃ュ織褰掓。鍜屾竻鐞嗙瓥鐣?/li>
 *   <li>瀹炵幇鏃ュ織鍔犲瘑鍜岄槻绡℃敼鏈哄埗</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@Service
public class AuditServiceImpl implements AuditService {

    /**
     * 瀹¤鏃ュ織瀛樺偍
     * 
     * <p>浣跨敤ConcurrentHashMap瀹炵幇绾跨▼瀹夊叏鐨勫唴瀛樺瓨鍌ㄣ€?
     * Key涓烘棩蹇桰D锛孷alue涓哄璁℃棩蹇楀璞°€?/p>
     * 
     * <p>娉ㄦ剰锛氳繖鏄复鏃跺瓨鍌ㄦ柟妗堬紝鏈嶅姟閲嶅惎鍚庢暟鎹細涓㈠け銆?
     * 鐢熶骇鐜搴旀浛鎹负鎸佷箙鍖栧瓨鍌ㄣ€?/p>
     */
    private final Map<String, AuditLog> logs = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     * 
     * <p>瀹炵幇璇存槑锛?/p>
     * <ul>
     *   <li>鑷姩鐢熸垚鏃ュ織ID锛堟牸寮忥細audit-{UUID鍓?浣峿锛?/li>
     *   <li>鑷姩璁剧疆鏃堕棿鎴筹紙濡傛灉鏈缃級</li>
     *   <li>浣跨敤ConcurrentHashMap淇濊瘉绾跨▼瀹夊叏</li>
     * </ul>
     */
    @Override
    public AuditLog record(AuditLog log) {
        if (log.getLogId() == null || log.getLogId().isEmpty()) {
            log.setLogId("audit-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (log.getTimestamp() == 0) {
            log.setTimestamp(System.currentTimeMillis());
        }
        logs.put(log.getLogId(), log);
        return log;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLog getById(String logId) {
        return logs.get(logId);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>瀹炵幇璇存槑锛?/p>
     * <ul>
     *   <li>浣跨敤Stream API杩涜澶氭潯浠惰繃婊?/li>
     *   <li>鏀寔鎸夋椂闂存埑鍗囧簭/闄嶅簭鎺掑簭</li>
     *   <li>鏀寔鍒嗛〉鏌ヨ</li>
     * </ul>
     * 
     * <p>鎬ц兘鑰冭檻锛氬綋鍓嶅疄鐜颁负鍐呭瓨杩囨护锛屽ぇ鏁版嵁閲忔椂寤鸿浣跨敤绱㈠紩浼樺寲銆?/p>
     */
    @Override
    public AuditQueryResult query(AuditQueryRequest request) {
        List<AuditLog> filtered = logs.values().stream()
                .filter(log -> request.getUserId() == null || request.getUserId().equals(log.getUserId()))
                .filter(log -> request.getAction() == null || request.getAction().equals(log.getAction()))
                .filter(log -> request.getResourceType() == null || request.getResourceType().equals(log.getResourceType()))
                .filter(log -> request.getResourceId() == null || request.getResourceId().equals(log.getResourceId()))
                .filter(log -> request.getResult() == null || request.getResult().equals(log.getResult()))
                .filter(log -> request.getStartTime() == null || log.getTimestamp() >= request.getStartTime())
                .filter(log -> request.getEndTime() == null || log.getTimestamp() <= request.getEndTime())
                .sorted((a, b) -> "asc".equals(request.getSortOrder()) 
                        ? Long.compare(a.getTimestamp(), b.getTimestamp())
                        : Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
        
        long total = filtered.size();
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), filtered.size());
        List<AuditLog> pageItems = start < filtered.size() 
                ? filtered.subList(start, end) 
                : new ArrayList<>();
        
        return new AuditQueryResult(pageItems, request.getPage(), request.getSize(), total);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>缁熻缁村害锛?/p>
     * <ul>
     *   <li>鎬婚噺缁熻锛氭€绘棩蹇楁暟銆佷粖鏃ユ棩蹇楁暟</li>
     *   <li>缁撴灉缁熻锛氭垚鍔熸鏁般€佸け璐ユ鏁?/li>
     *   <li>鍒嗗竷缁熻锛氭搷浣滅被鍨嬪垎甯冦€佽祫婧愮被鍨嬪垎甯冦€佺敤鎴锋椿璺冨害</li>
     * </ul>
     */
    @Override
    public AuditStatistics getStatistics(Long startTime, Long endTime) {
        AuditStatistics stats = new AuditStatistics();
        
        List<AuditLog> filtered = logs.values().stream()
                .filter(log -> startTime == null || log.getTimestamp() >= startTime)
                .filter(log -> endTime == null || log.getTimestamp() <= endTime)
                .collect(Collectors.toList());
        
        stats.setTotalLogs(filtered.size());
        
        long todayStart = getTodayStart();
        stats.setTodayLogs(filtered.stream()
                .filter(log -> log.getTimestamp() >= todayStart)
                .count());
        
        stats.setSuccessCount(filtered.stream()
                .filter(log -> "success".equals(log.getResult()))
                .count());
        
        stats.setFailureCount(filtered.stream()
                .filter(log -> "failure".equals(log.getResult()))
                .count());
        
        Map<String, Long> actionCounts = new HashMap<>();
        Map<String, Long> resourceTypeCounts = new HashMap<>();
        Map<String, Long> userCounts = new HashMap<>();
        
        for (AuditLog log : filtered) {
            String action = log.getAction() != null ? log.getAction() : "unknown";
            String resourceType = log.getResourceType() != null ? log.getResourceType() : "unknown";
            String userId = log.getUserId() != null ? log.getUserId() : "anonymous";
            
            actionCounts.merge(action, 1L, Long::sum);
            resourceTypeCounts.merge(resourceType, 1L, Long::sum);
            userCounts.merge(userId, 1L, Long::sum);
        }
        
        stats.setActionCounts(actionCounts);
        stats.setResourceTypeCounts(resourceTypeCounts);
        stats.setUserCounts(userCounts);
        stats.setStartTime(startTime != null ? startTime : 0);
        stats.setEndTime(endTime != null ? endTime : System.currentTimeMillis());
        
        return stats;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>鏀寔鐨勫鍑烘牸寮忥細</p>
     * <ul>
     *   <li>JSON锛氱粨鏋勫寲鏁版嵁鏍煎紡锛岄€傚悎绋嬪簭澶勭悊</li>
     *   <li>CSV锛氳〃鏍兼牸寮忥紝閫傚悎Excel鎵撳紑鏌ョ湅</li>
     * </ul>
     */
    @Override
    public byte[] export(AuditQueryRequest request, String format) {
        AuditQueryResult result = query(request);
        StringBuilder sb = new StringBuilder();
        
        if ("csv".equalsIgnoreCase(format)) {
            sb.append("LogId,UserId,Action,ResourceType,ResourceId,Result,IpAddress,Timestamp\n");
            for (AuditLog log : result.getItems()) {
                sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%d\n",
                        log.getLogId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getResourceType(),
                        log.getResourceId(),
                        log.getResult(),
                        log.getIpAddress(),
                        log.getTimestamp()));
            }
        } else {
            sb.append("[\n");
            for (int i = 0; i < result.getItems().size(); i++) {
                AuditLog log = result.getItems().get(i);
                sb.append(String.format("  {\"logId\":\"%s\",\"userId\":\"%s\",\"action\":\"%s\",\"timestamp\":%d}",
                        log.getLogId(), log.getUserId(), log.getAction(), log.getTimestamp()));
                if (i < result.getItems().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
        }
        
        return sb.toString().getBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditQueryResult getByUserId(String userId, int page, int size) {
        AuditQueryRequest request = new AuditQueryRequest();
        request.setUserId(userId);
        request.setPage(page);
        request.setSize(size);
        return query(request);
    }
    
    /**
     * 鑾峰彇浠婃棩闆剁偣鏃堕棿鎴?
     * 
     * <p>鐢ㄤ簬缁熻浠婃棩鏃ュ織鏁伴噺锛岃绠椾粠褰撳ぉ00:00:00寮€濮嬬殑鏃堕棿鎴炽€?/p>
     * 
     * @return 浠婃棩闆剁偣鐨勬绉掓椂闂存埑
     */
    private long getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
