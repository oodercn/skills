package net.ooder.skill.audit.controller;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 瀹¤鏃ュ織REST API鎺у埗鍣?
 * 
 * <p>鎻愪緵瀹¤鏃ュ織鐩稿叧鐨凥TTP鎺ュ彛锛屾敮鎸佹棩蹇楄褰曘€佹煡璇€佺粺璁″拰瀵煎嚭鍔熻兘銆?/p>
 * 
 * <h3>API绔偣鍒楄〃锛?/h3>
 * <table border="1">
 *   <tr><th>鏂规硶</th><th>璺緞</th><th>鎻忚堪</th></tr>
 *   <tr><td>POST</td><td>/api/audit/record</td><td>璁板綍瀹¤鏃ュ織</td></tr>
 *   <tr><td>GET</td><td>/api/audit/logs/{logId}</td><td>鏍规嵁ID鑾峰彇鏃ュ織</td></tr>
 *   <tr><td>POST</td><td>/api/audit/logs</td><td>鏌ヨ瀹¤鏃ュ織</td></tr>
 *   <tr><td>GET</td><td>/api/audit/statistics</td><td>鑾峰彇瀹¤缁熻</td></tr>
 *   <tr><td>POST</td><td>/api/audit/export</td><td>瀵煎嚭瀹¤鏃ュ織</td></tr>
 *   <tr><td>GET</td><td>/api/audit/users/{userId}/logs</td><td>鑾峰彇鐢ㄦ埛鏃ュ織</td></tr>
 * </table>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 璁板綍瀹¤鏃ュ織
 * POST /api/audit/record
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "userName": "寮犱笁",
 *   "action": "login",
 *   "resourceType": "session",
 *   "result": "success",
 *   "ipAddress": "192.168.1.100"
 * }
 * 
 * // 鏌ヨ瀹¤鏃ュ織
 * POST /api/audit/logs
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "startTime": 1700000000000,
 *   "endTime": 1700086400000,
 *   "page": 0,
 *   "size": 20
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 璁板綍瀹¤鏃ュ織
     * 
     * <p>灏嗕竴鏉″璁℃棩蹇楄褰曚繚瀛樺埌绯荤粺涓€傞€氬父鍦ㄦ墽琛屾晱鎰熸搷浣滄椂璋冪敤姝ゆ帴鍙ｈ繘琛岃褰曘€?/p>
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "userId": "user-001",
     *   "userName": "寮犱笁",
     *   "action": "delete",
     *   "resourceType": "file",
     *   "resourceId": "file-123",
     *   "resourceName": "閲嶈鏂囨。.pdf",
     *   "result": "success",
     *   "ipAddress": "192.168.1.100",
     *   "userAgent": "Mozilla/5.0...",
     *   "description": "鍒犻櫎閲嶈鏂囨。"
     * }
     * }</pre>
     * 
     * @param log 瀹¤鏃ュ織瀵硅薄
     * @return 淇濆瓨鍚庣殑瀹¤鏃ュ織锛屽寘鍚敓鎴愮殑ID鍜屾椂闂存埑
     */
    @PostMapping("/record")
    public ResponseEntity<AuditLog> record(@RequestBody AuditLog log) {
        return ResponseEntity.ok(auditService.record(log));
    }

    /**
     * 鏍规嵁ID鑾峰彇瀹¤鏃ュ織
     * 
     * @param logId 鏃ュ織ID
     * @return 瀹¤鏃ュ織瀵硅薄锛屽鏋滀笉瀛樺湪杩斿洖404
     */
    @GetMapping("/logs/{logId}")
    public ResponseEntity<AuditLog> getById(@PathVariable String logId) {
        AuditLog log = auditService.getById(logId);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }

    /**
     * 鏌ヨ瀹¤鏃ュ織
     * 
     * <p>鏀寔澶氭潯浠剁粍鍚堟煡璇紝鍖呮嫭鐢ㄦ埛ID銆佹搷浣滅被鍨嬨€佽祫婧愮被鍨嬨€佹椂闂磋寖鍥寸瓑鏉′欢銆?
     * 鏀寔鍒嗛〉鍜屾帓搴忋€?/p>
     * 
     * <h4>鏌ヨ鏉′欢锛?/h4>
     * <ul>
     *   <li>userId - 鐢ㄦ埛ID</li>
     *   <li>action - 鎿嶄綔绫诲瀷锛堝锛歭ogin銆乴ogout銆乧reate銆乽pdate銆乨elete锛?/li>
     *   <li>resourceType - 璧勬簮绫诲瀷锛堝锛歶ser銆乫ile銆乻ession锛?/li>
     *   <li>resourceId - 璧勬簮ID</li>
     *   <li>result - 鎿嶄綔缁撴灉锛坰uccess/failure锛?/li>
     *   <li>startTime - 寮€濮嬫椂闂达紙姣鏃堕棿鎴筹級</li>
     *   <li>endTime - 缁撴潫鏃堕棿锛堟绉掓椂闂存埑锛?/li>
     *   <li>page - 椤电爜锛堜粠0寮€濮嬶級</li>
     *   <li>size - 姣忛〉澶у皬</li>
     *   <li>sortBy - 鎺掑簭瀛楁锛堥粯璁わ細timestamp锛?/li>
     *   <li>sortOrder - 鎺掑簭鏂瑰悜锛坅sc/desc锛岄粯璁わ細desc锛?/li>
     * </ul>
     * 
     * @param request 鏌ヨ璇锋眰瀵硅薄
     * @return 鏌ヨ缁撴灉锛屽寘鍚棩蹇楀垪琛ㄥ拰鍒嗛〉淇℃伅
     */
    @PostMapping("/logs")
    public ResponseEntity<AuditQueryResult> query(@RequestBody AuditQueryRequest request) {
        return ResponseEntity.ok(auditService.query(request));
    }

    /**
     * 鑾峰彇瀹¤缁熻鏁版嵁
     * 
     * <p>缁熻鎸囧畾鏃堕棿鑼冨洿鍐呯殑瀹¤鏁版嵁锛屽寘鎷€婚噺缁熻銆佺粨鏋滅粺璁″拰鍒嗗竷缁熻銆?/p>
     * 
     * <h4>杩斿洖鏁版嵁锛?/h4>
     * <ul>
     *   <li>totalLogs - 鎬绘棩蹇楁暟</li>
     *   <li>todayLogs - 浠婃棩鏃ュ織鏁?/li>
     *   <li>successCount - 鎴愬姛娆℃暟</li>
     *   <li>failureCount - 澶辫触娆℃暟</li>
     *   <li>actionCounts - 鎿嶄綔绫诲瀷鍒嗗竷</li>
     *   <li>resourceTypeCounts - 璧勬簮绫诲瀷鍒嗗竷</li>
     *   <li>userCounts - 鐢ㄦ埛娲昏穬搴︾粺璁?/li>
     * </ul>
     * 
     * @param startTime 缁熻寮€濮嬫椂闂达紙鍙€夛級
     * @param endTime 缁熻缁撴潫鏃堕棿锛堝彲閫夛級
     * @return 瀹¤缁熻鏁版嵁
     */
    @GetMapping("/statistics")
    public ResponseEntity<AuditStatistics> getStatistics(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        return ResponseEntity.ok(auditService.getStatistics(startTime, endTime));
    }

    /**
     * 瀵煎嚭瀹¤鏃ュ織
     * 
     * <p>灏嗘煡璇㈢粨鏋滃鍑轰负鎸囧畾鏍煎紡鐨勬枃浠朵笅杞姐€?/p>
     * 
     * <h4>鏀寔鐨勫鍑烘牸寮忥細</h4>
     * <ul>
     *   <li>json - JSON鏍煎紡锛岄€傚悎绋嬪簭澶勭悊</li>
     *   <li>csv - CSV鏍煎紡锛屽彲鐢‥xcel鎵撳紑</li>
     * </ul>
     * 
     * @param request 鏌ヨ璇锋眰瀵硅薄锛岀敤浜庣瓫閫夎瀵煎嚭鐨勬棩蹇?
     * @param format 瀵煎嚭鏍煎紡锛堥粯璁わ細json锛?
     * @return 鏂囦欢涓嬭浇鍝嶅簲
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody AuditQueryRequest request,
                                         @RequestParam(defaultValue = "json") String format) {
        byte[] content = auditService.export(request, format);
        String filename = "audit-export." + format;
        String contentType = "csv".equalsIgnoreCase(format) ? "text/csv" : "application/json";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }

    /**
     * 鑾峰彇鎸囧畾鐢ㄦ埛鐨勫璁℃棩蹇?
     * 
     * <p>渚挎嵎鎺ュ彛锛岀敤浜庡揩閫熸煡璇㈡煇涓敤鎴风殑鎵€鏈夋搷浣滆褰曘€?/p>
     * 
     * @param userId 鐢ㄦ埛ID
     * @param page 椤电爜锛堜粠0寮€濮嬶紝榛樿锛?锛?
     * @param size 姣忛〉澶у皬锛堥粯璁わ細20锛?
     * @return 鏌ヨ缁撴灉
     */
    @GetMapping("/users/{userId}/logs")
    public ResponseEntity<AuditQueryResult> getByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getByUserId(userId, page, size));
    }
}
