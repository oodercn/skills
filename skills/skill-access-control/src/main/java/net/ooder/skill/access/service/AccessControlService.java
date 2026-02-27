package net.ooder.skill.access.service;

import net.ooder.skill.access.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 璁块棶鎺у埗鏈嶅姟鎺ュ彛
 * 
 * <p>鎻愪緵鍩轰簬RBAC锛圧ole-Based Access Control锛夋ā鍨嬬殑鏉冮檺绠＄悊鏈嶅姟锛?
 * 鍖呮嫭鏉冮檺绠＄悊銆佽鑹茬鐞嗗拰璁块棶鎺у埗鍔熻兘銆?/p>
 * 
 * <h3>鏍稿績姒傚康锛?/h3>
 * <ul>
 *   <li><b>鏉冮檺锛圥ermission锛?/b>锛氬鐗瑰畾璧勬簮鐨勬搷浣滆鍙紝濡?璇诲彇鏂囦欢"銆?鍒犻櫎鐢ㄦ埛"</li>
 *   <li><b>瑙掕壊锛圧ole锛?/b>锛氭潈闄愮殑闆嗗悎锛屽"绠＄悊鍛?銆?鏅€氱敤鎴?銆?瀹¤鍛?</li>
 *   <li><b>鐢ㄦ埛锛圲ser锛?/b>锛氳鍒嗛厤瑙掕壊鐨勪富浣擄紝閫氳繃瑙掕壊闂存帴鑾峰緱鏉冮檺</li>
 * </ul>
 * 
 * <h3>搴旂敤鍦烘櫙锛?/h3>
 * <ul>
 *   <li>浼佷笟搴旂敤锛氭牴鎹儴闂ㄣ€佽亴浣嶅垎閰嶄笉鍚屾潈闄?/li>
 *   <li>SaaS骞冲彴锛氬绉熸埛鏉冮檺闅旂锛屼笉鍚屽椁愪笉鍚屾潈闄?/li>
 *   <li>绠＄悊绯荤粺锛氬悗鍙扮鐞嗘潈闄愭帶鍒讹紝鏁忔劅鎿嶄綔瀹℃壒</li>
 *   <li>API缃戝叧锛氭帴鍙ｈ闂潈闄愭牎楠?/li>
 * </ul>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 鍒涘缓鏉冮檺
 * Permission perm = new Permission();
 * perm.setName("鍒犻櫎鐢ㄦ埛");
 * perm.setCode("user:delete");
 * perm.setResourceType("user");
 * perm.setAction("delete");
 * accessControlService.createPermission(perm);
 * 
 * // 鍒涘缓瑙掕壊骞跺垎閰嶆潈闄?
 * Role adminRole = new Role();
 * adminRole.setName("绠＄悊鍛?);
 * adminRole.setCode("admin");
 * accessControlService.createRole(adminRole);
 * accessControlService.assignPermissionsToRole("role-admin", 
 *     Arrays.asList("perm-read", "perm-write", "perm-delete"));
 * 
 * // 缁欑敤鎴峰垎閰嶈鑹?
 * accessControlService.assignRolesToUser("user-001", 
 *     Arrays.asList("role-admin"));
 * 
 * // 妫€鏌ユ潈闄?
 * PermissionCheckRequest request = new PermissionCheckRequest();
 * request.setUserId("user-001");
 * request.setPermissionCode("user:delete");
 * PermissionCheckResult result = accessControlService.checkPermission(request);
 * if (result.isAllowed()) {
 *     // 鎵ц鍒犻櫎鎿嶄綔
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface AccessControlService {
    
    /**
     * 鍒楀嚭鎵€鏈夋潈闄?
     * 
     * @return 鏉冮檺鍒楄〃
     */
    List<Permission> listPermissions();
    
    /**
     * 鍒涘缓鏉冮檺
     * 
     * <p>鍒涘缓涓€涓柊鐨勬潈闄愬畾涔夈€傛潈闄愰€氬父鎸?璧勬簮绫诲瀷:鎿嶄綔"鏍煎紡鍛藉悕锛?
     * 濡?user:read"銆?file:delete"銆?/p>
     * 
     * @param permission 鏉冮檺瀵硅薄
     * @return 鍒涘缓鍚庣殑鏉冮檺瀵硅薄锛屽寘鍚敓鎴愮殑ID
     */
    Permission createPermission(Permission permission);
    
    /**
     * 鑾峰彇鏉冮檺璇︽儏
     * 
     * @param permissionId 鏉冮檺ID
     * @return 鏉冮檺瀵硅薄锛屼笉瀛樺湪鍒欒繑鍥瀗ull
     */
    Permission getPermission(String permissionId);
    
    /**
     * 鍒犻櫎鏉冮檺
     * 
     * <p>鍒犻櫎鏉冮檺浼氬悓鏃朵粠鎵€鏈夎鑹蹭腑绉婚櫎璇ユ潈闄愩€?/p>
     * 
     * @param permissionId 鏉冮檺ID
     * @return 鏄惁鍒犻櫎鎴愬姛
     */
    boolean deletePermission(String permissionId);
    
    /**
     * 鍒楀嚭鎵€鏈夎鑹?
     * 
     * @return 瑙掕壊鍒楄〃
     */
    List<Role> listRoles();
    
    /**
     * 鍒涘缓瑙掕壊
     * 
     * <p>鍒涘缓涓€涓柊鐨勮鑹插畾涔夈€傜郴缁熻鑹诧紙system=true锛変笉鍙垹闄ゃ€?/p>
     * 
     * @param role 瑙掕壊瀵硅薄
     * @return 鍒涘缓鍚庣殑瑙掕壊瀵硅薄锛屽寘鍚敓鎴愮殑ID
     */
    Role createRole(Role role);
    
    /**
     * 鑾峰彇瑙掕壊璇︽儏
     * 
     * @param roleId 瑙掕壊ID
     * @return 瑙掕壊瀵硅薄锛屼笉瀛樺湪鍒欒繑鍥瀗ull
     */
    Role getRole(String roleId);
    
    /**
     * 鍒犻櫎瑙掕壊
     * 
     * <p>绯荤粺瑙掕壊涓嶅彲鍒犻櫎銆傚垹闄よ鑹蹭細鍚屾椂绉婚櫎鎵€鏈夌敤鎴风殑璇ヨ鑹插垎閰嶃€?/p>
     * 
     * @param roleId 瑙掕壊ID
     * @return 鏄惁鍒犻櫎鎴愬姛
     */
    boolean deleteRole(String roleId);
    
    /**
     * 涓鸿鑹插垎閰嶆潈闄?
     * 
     * <p>璁剧疆瑙掕壊鎷ユ湁鐨勬潈闄愬垪琛紝浼氳鐩栧師鏈夋潈闄愩€?/p>
     * 
     * @param roleId 瑙掕壊ID
     * @param permissionIds 鏉冮檺ID鍒楄〃
     * @return 鏄惁鍒嗛厤鎴愬姛
     */
    boolean assignPermissionsToRole(String roleId, List<String> permissionIds);
    
    /**
     * 鑾峰彇鐢ㄦ埛鐨勮鑹插垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 瑙掕壊ID鍒楄〃
     */
    List<String> getUserRoles(String userId);
    
    /**
     * 涓虹敤鎴峰垎閰嶈鑹?
     * 
     * <p>涓虹敤鎴锋坊鍔犺鑹诧紝涓嶄細瑕嗙洊宸叉湁瑙掕壊銆?/p>
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑙掕壊ID鍒楄〃
     * @return 鏄惁鍒嗛厤鎴愬姛
     */
    boolean assignRolesToUser(String userId, List<String> roleIds);
    
    /**
     * 绉婚櫎鐢ㄦ埛鐨勮鑹?
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑕佺Щ闄ょ殑瑙掕壊ID鍒楄〃
     * @return 鏄惁绉婚櫎鎴愬姛
     */
    boolean removeRolesFromUser(String userId, List<String> roleIds);
    
    /**
     * 妫€鏌ョ敤鎴锋潈闄?
     * 
     * <p>妫€鏌ョ敤鎴锋槸鍚︽嫢鏈夋寚瀹氱殑鏉冮檺銆備細妫€鏌ョ敤鎴锋墍鏈夎鑹蹭笅鐨勬潈闄愶紝
     * 鍙鏈変竴涓鑹叉嫢鏈夎鏉冮檺鍗宠繑鍥炲厑璁搞€?/p>
     * 
     * <h4>妫€鏌ラ€昏緫锛?/h4>
     * <ol>
     *   <li>鑾峰彇鐢ㄦ埛鐨勬墍鏈夎鑹?/li>
     *   <li>閬嶅巻瑙掕壊锛屾鏌ユ槸鍚﹀寘鍚姹傜殑鏉冮檺</li>
     *   <li>鏀寔閫氶厤绗﹀尮閰嶏細admin鏉冮檺鎴?鎿嶄綔鍙尮閰嶆墍鏈夋潈闄?/li>
     * </ol>
     * 
     * @param request 鏉冮檺妫€鏌ヨ姹?
     * @return 妫€鏌ョ粨鏋滐紝鍖呭惈鏄惁鍏佽銆佸尮閰嶇殑瑙掕壊绛変俊鎭?
     */
    PermissionCheckResult checkPermission(PermissionCheckRequest request);
    
    /**
     * 鑾峰彇鐢ㄦ埛鐨勬墍鏈夋潈闄?
     * 
     * <p>鑱氬悎鐢ㄦ埛鎵€鏈夎鑹蹭笅鐨勬潈闄愶紝杩斿洖鍘婚噸鍚庣殑鏉冮檺鍒楄〃銆?/p>
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 鑾峰彇璁块棶鎺у埗缁熻鏁版嵁
     * 
     * <p>杩斿洖绯荤粺涓潈闄愩€佽鑹层€佺敤鎴风殑缁熻淇℃伅銆?/p>
     * 
     * @return 缁熻鏁版嵁Map
     */
    Map<String, Object> getAccessStatistics();
}
