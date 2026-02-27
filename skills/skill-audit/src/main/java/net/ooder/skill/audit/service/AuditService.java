package net.ooder.skill.audit.service;

import net.ooder.skill.audit.dto.*;

import java.util.Map;

/**
 * 瀹¤鏃ュ織鏈嶅姟鎺ュ彛
 * 
 * <p>鎻愪緵瀹¤鏃ュ織鐨勮褰曘€佹煡璇€佺粺璁″拰瀵煎嚭鍔熻兘銆傚璁℃棩蹇楃敤浜庤褰曠郴缁熶腑鎵€鏈夐噸瑕佹搷浣滅殑杞ㄨ抗锛?
 * 鏀寔瀹夊叏瀹¤銆佸悎瑙勬鏌ュ拰闂杩芥函銆?/p>
 * 
 * <h3>搴旂敤鍦烘櫙锛?/h3>
 * <ul>
 *   <li>瀹夊叏瀹¤锛氳褰曠敤鎴风櫥褰曘€佹潈闄愬彉鏇淬€佹晱鎰熸暟鎹闂瓑瀹夊叏鐩稿叧鎿嶄綔</li>
 *   <li>鍚堣妫€鏌ワ細婊¤冻浼佷笟鍐呮帶銆佽涓氱洃绠″鎿嶄綔璁板綍鐣欏瓨鐨勮姹?/li>
 *   <li>闂杩芥函锛氬湪绯荤粺鏁呴殰鎴栧畨鍏ㄤ簨浠跺彂鐢熸椂锛岃拷婧搷浣滃巻鍙插畾浣嶉棶棰?/li>
 *   <li>琛屼负鍒嗘瀽锛氱粺璁″垎鏋愮敤鎴疯涓烘ā寮忥紝鍙戠幇寮傚父鎿嶄綔</li>
 * </ul>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 璁板綍瀹¤鏃ュ織
 * AuditLog log = new AuditLog();
 * log.setUserId("user-001");
 * log.setAction("login");
 * log.setResourceType("session");
 * log.setResult("success");
 * auditService.record(log);
 * 
 * // 鏌ヨ瀹¤鏃ュ織
 * AuditQueryRequest request = new AuditQueryRequest();
 * request.setUserId("user-001");
 * request.setStartTime(System.currentTimeMillis() - 86400000L); // 鏈€杩?4灏忔椂
 * AuditQueryResult result = auditService.query(request);
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface AuditService {
    
    /**
     * 璁板綍瀹¤鏃ュ織
     * 
     * <p>灏嗕竴鏉″璁℃棩蹇楄褰曚繚瀛樺埌瀛樺偍涓€傚鏋滄棩蹇桰D鏈缃紝灏嗚嚜鍔ㄧ敓鎴愶紱
     * 濡傛灉鏃堕棿鎴虫湭璁剧疆锛屽皢浣跨敤褰撳墠鏃堕棿銆?/p>
     * 
     * @param log 瀹¤鏃ュ織瀵硅薄锛屽寘鍚敤鎴稩D銆佹搷浣滅被鍨嬨€佽祫婧愪俊鎭瓑
     * @return 淇濆瓨鍚庣殑瀹¤鏃ュ織瀵硅薄锛屽寘鍚敓鎴愮殑ID鍜屾椂闂存埑
     * @throws IllegalArgumentException 濡傛灉蹇呭～瀛楁涓虹┖
     */
    AuditLog record(AuditLog log);
    
    /**
     * 鏍规嵁ID鑾峰彇瀹¤鏃ュ織
     * 
     * @param logId 鏃ュ織ID
     * @return 瀹¤鏃ュ織瀵硅薄锛屽鏋滀笉瀛樺湪杩斿洖null
     */
    AuditLog getById(String logId);
    
    /**
     * 鏌ヨ瀹¤鏃ュ織
     * 
     * <p>鏀寔澶氭潯浠剁粍鍚堟煡璇紝鍖呮嫭鐢ㄦ埛ID銆佹搷浣滅被鍨嬨€佽祫婧愮被鍨嬨€佹椂闂磋寖鍥寸瓑銆?
     * 鏀寔鍒嗛〉鍜屾帓搴忋€?/p>
     * 
     * @param request 鏌ヨ璇锋眰瀵硅薄锛屽寘鍚煡璇㈡潯浠跺拰鍒嗛〉鍙傛暟
     * @return 鏌ヨ缁撴灉锛屽寘鍚棩蹇楀垪琛ㄥ拰鍒嗛〉淇℃伅
     */
    AuditQueryResult query(AuditQueryRequest request);
    
    /**
     * 鑾峰彇瀹¤缁熻鏁版嵁
     * 
     * <p>缁熻鎸囧畾鏃堕棿鑼冨洿鍐呯殑瀹¤鏁版嵁锛屽寘鎷€绘棩蹇楁暟銆佹垚鍔?澶辫触娆℃暟銆?
     * 鎿嶄綔绫诲瀷鍒嗗竷銆佽祫婧愮被鍨嬪垎甯冦€佺敤鎴锋椿璺冨害绛夈€?/p>
     * 
     * @param startTime 缁熻寮€濮嬫椂闂达紙姣鏃堕棿鎴筹級锛屼负null琛ㄧず涓嶉檺鍒跺紑濮嬫椂闂?
     * @param endTime 缁熻缁撴潫鏃堕棿锛堟绉掓椂闂存埑锛夛紝涓簄ull琛ㄧず涓嶉檺鍒剁粨鏉熸椂闂?
     * @return 瀹¤缁熻鏁版嵁瀵硅薄
     */
    AuditStatistics getStatistics(Long startTime, Long endTime);
    
    /**
     * 瀵煎嚭瀹¤鏃ュ織
     * 
     * <p>灏嗘煡璇㈢粨鏋滃鍑轰负鎸囧畾鏍煎紡鐨勬枃浠讹紝鏀寔JSON鍜孋SV鏍煎紡銆?/p>
     * 
     * @param request 鏌ヨ璇锋眰瀵硅薄锛岀敤浜庣瓫閫夎瀵煎嚭鐨勬棩蹇?
     * @param format 瀵煎嚭鏍煎紡锛屾敮鎸?json"鍜?csv"
     * @return 瀵煎嚭鏂囦欢鐨勫瓧鑺傛暟缁?
     */
    byte[] export(AuditQueryRequest request, String format);
    
    /**
     * 鑾峰彇鎸囧畾鐢ㄦ埛鐨勫璁℃棩蹇?
     * 
     * <p>渚挎嵎鏂规硶锛岀敤浜庡揩閫熸煡璇㈡煇涓敤鎴风殑鎵€鏈夋搷浣滆褰曘€?/p>
     * 
     * @param userId 鐢ㄦ埛ID
     * @param page 椤电爜锛堜粠0寮€濮嬶級
     * @param size 姣忛〉澶у皬
     * @return 鏌ヨ缁撴灉
     */
    AuditQueryResult getByUserId(String userId, int page, int size);
}
