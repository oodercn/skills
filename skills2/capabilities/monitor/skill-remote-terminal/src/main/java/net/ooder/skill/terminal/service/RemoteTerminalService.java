package net.ooder.skill.terminal.service;

import net.ooder.skill.terminal.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 杩滅▼缁堢鏈嶅姟鎺ュ彛
 * 
 * <p>鎻愪緵SSH杩滅▼杩炴帴銆佸懡浠ゆ墽琛屽拰鏂囦欢浼犺緭鍔熻兘锛屾敮鎸佽繙绋嬫湇鍔″櫒绠＄悊銆?/p>
 * 
 * <h3>鏍稿績鍔熻兘锛?/h3>
 * <ul>
 *   <li><b>SSH杩炴帴</b>锛氭敮鎸佸瘑鐮佸拰瀵嗛挜涓ょ璁よ瘉鏂瑰紡</li>
 *   <li><b>鍛戒护鎵ц</b>锛氬湪杩滅▼鏈嶅姟鍣ㄤ笂鎵цShell鍛戒护</li>
 *   <li><b>鏂囦欢浼犺緭</b>锛氭敮鎸丼FTP鍗忚鐨勪笂浼犲拰涓嬭浇</li>
 *   <li><b>浼氳瘽绠＄悊</b>锛氱鐞嗗涓苟鍙慡SH浼氳瘽</li>
 * </ul>
 * 
 * <h3>搴旂敤鍦烘櫙锛?/h3>
 * <ul>
 *   <li>杩愮淮绠＄悊锛氳繙绋嬫湇鍔″櫒杩愮淮銆佹壒閲忓懡浠ゆ墽琛?/li>
 *   <li>DevOps锛欳I/CD娴佹按绾夸腑鐨勮繙绋嬮儴缃?/li>
 *   <li>浜戠鐞嗭細浜戞湇鍔″櫒杩滅▼绠＄悊</li>
 *   <li>缃戠粶璁惧锛氳矾鐢卞櫒銆佷氦鎹㈡満閰嶇疆绠＄悊</li>
 *   <li>IoT璁惧锛氳竟缂樿澶囪繙绋嬭皟璇?/li>
 * </ul>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 寤虹珛SSH杩炴帴
 * ConnectionRequest connReq = new ConnectionRequest();
 * connReq.setHost("192.168.1.100");
 * connReq.setPort(22);
 * connReq.setUsername("admin");
 * connReq.setPassword("password");
 * SessionInfo session = remoteTerminalService.connect(connReq);
 * 
 * // 鎵ц鍛戒护
 * CommandResult result = remoteTerminalService.executeCommand(
 *     session.getSessionId(), 
 *     "ls -la /home", 
 *     null);
 * System.out.println(result.getOutput());
 * 
 * // 涓婁紶鏂囦欢
 * FileTransferResult upload = remoteTerminalService.uploadFile(
 *     session.getSessionId(),
 *     "/local/file.txt",
 *     "/remote/file.txt");
 * 
 * // 鏂紑杩炴帴
 * remoteTerminalService.disconnect(session.getSessionId());
 * }</pre>
 * 
 * <h3>瀹夊叏寤鸿锛?/h3>
 * <ul>
 *   <li>浼樺厛浣跨敤瀵嗛挜璁よ瘉鑰岄潪瀵嗙爜璁よ瘉</li>
 *   <li>浣跨敤SSH璇佷功杩涜涓绘満楠岃瘉</li>
 *   <li>闄愬埗浼氳瘽瓒呮椂鏃堕棿</li>
 *   <li>璁板綍鎵€鏈夋搷浣滄棩蹇楃敤浜庡璁?/li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface RemoteTerminalService {
    
    /**
     * 寤虹珛SSH杩炴帴
     * 
     * <p>涓庤繙绋嬫湇鍔″櫒寤虹珛SSH杩炴帴锛屾敮鎸佸瘑鐮佽璇佸拰瀵嗛挜璁よ瘉涓ょ鏂瑰紡銆?/p>
     * 
     * <h4>璁よ瘉鏂瑰紡锛?/h4>
     * <ul>
     *   <li>瀵嗙爜璁よ瘉锛氳缃畃assword瀛楁</li>
     *   <li>瀵嗛挜璁よ瘉锛氳缃畃rivateKey瀛楁锛屽彲閫夎缃畃assphrase</li>
     * </ul>
     * 
     * @param request 杩炴帴璇锋眰锛屽寘鍚富鏈哄湴鍧€銆佺鍙ｃ€佽璇佷俊鎭瓑
     * @return 浼氳瘽淇℃伅锛屽寘鍚細璇滻D鍜岃繛鎺ョ姸鎬?
     * @throws RuntimeException 濡傛灉杩炴帴澶辫触鎴栬揪鍒版渶澶т細璇濇暟闄愬埗
     */
    SessionInfo connect(ConnectionRequest request);
    
    /**
     * 鏂紑SSH杩炴帴
     * 
     * <p>鍏抽棴鎸囧畾浼氳瘽骞堕噴鏀剧浉鍏宠祫婧愩€?/p>
     * 
     * @param sessionId 浼氳瘽ID
     * @return 鏄惁鏂紑鎴愬姛
     */
    boolean disconnect(String sessionId);
    
    /**
     * 鑾峰彇浼氳瘽璇︽儏
     * 
     * @param sessionId 浼氳瘽ID
     * @return 浼氳瘽淇℃伅锛屼笉瀛樺湪鍒欒繑鍥瀗ull
     */
    SessionInfo getSession(String sessionId);
    
    /**
     * 鍒楀嚭鎵€鏈変細璇?
     * 
     * <p>杩斿洖褰撳墠鎵€鏈夋椿璺冨拰闈炴椿璺冪殑浼氳瘽鍒楄〃銆?/p>
     * 
     * @return 浼氳瘽鍒楄〃
     */
    List<SessionInfo> listSessions();
    
    /**
     * 鎵ц杩滅▼鍛戒护
     * 
     * <p>鍦ㄦ寚瀹氫細璇濈殑杩滅▼鏈嶅姟鍣ㄤ笂鎵цShell鍛戒护銆?/p>
     * 
     * <h4>鎵ц娴佺▼锛?/h4>
     * <ol>
     *   <li>楠岃瘉浼氳瘽鏄惁鏈夋晥</li>
     *   <li>鍙€夊垏鎹㈠伐浣滅洰褰?/li>
     *   <li>鎵ц鍛戒护骞舵崟鑾疯緭鍑?/li>
     *   <li>杩斿洖鎵ц缁撴灉鍜岄€€鍑虹爜</li>
     * </ol>
     * 
     * @param sessionId 浼氳瘽ID
     * @param command 瑕佹墽琛岀殑鍛戒护
     * @param workingDirectory 宸ヤ綔鐩綍锛堝彲閫夛紝涓簄ull浣跨敤榛樿鐩綍锛?
     * @return 鍛戒护鎵ц缁撴灉锛屽寘鍚緭鍑恒€侀敊璇拰閫€鍑虹爜
     * @throws RuntimeException 濡傛灉浼氳瘽涓嶅瓨鍦ㄦ垨鏈繛鎺?
     */
    CommandResult executeCommand(String sessionId, String command, String workingDirectory);
    
    /**
     * 涓婁紶鏂囦欢
     * 
     * <p>閫氳繃SFTP鍗忚灏嗘湰鍦版枃浠朵笂浼犲埌杩滅▼鏈嶅姟鍣ㄣ€?/p>
     * 
     * @param sessionId 浼氳瘽ID
     * @param localPath 鏈湴鏂囦欢璺緞
     * @param remotePath 杩滅▼鐩爣璺緞
     * @return 浼犺緭缁撴灉锛屽寘鍚紶杈撳瓧鑺傛暟鍜岀姸鎬?
     */
    FileTransferResult uploadFile(String sessionId, String localPath, String remotePath);
    
    /**
     * 涓嬭浇鏂囦欢
     * 
     * <p>閫氳繃SFTP鍗忚浠庤繙绋嬫湇鍔″櫒涓嬭浇鏂囦欢鍒版湰鍦般€?/p>
     * 
     * @param sessionId 浼氳瘽ID
     * @param remotePath 杩滅▼鏂囦欢璺緞
     * @param localPath 鏈湴鐩爣璺緞
     * @return 浼犺緭缁撴灉锛屽寘鍚紶杈撳瓧鑺傛暟鍜岀姸鎬?
     */
    FileTransferResult downloadFile(String sessionId, String remotePath, String localPath);
    
    /**
     * 鑾峰彇鏈嶅姟鐘舵€?
     * 
     * <p>杩斿洖杩滅▼缁堢鏈嶅姟鐨勮繍琛岀姸鎬佺粺璁°€?/p>
     * 
     * <h4>杩斿洖鏁版嵁锛?/h4>
     * <ul>
     *   <li>totalSessions - 鎬讳細璇濇暟</li>
     *   <li>activeSessions - 娲昏穬浼氳瘽鏁?/li>
     *   <li>maxSessions - 鏈€澶т細璇濇暟闄愬埗</li>
     *   <li>status - 鏈嶅姟鐘舵€?/li>
     * </ul>
     * 
     * @return 鐘舵€佷俊鎭疢ap
     */
    Map<String, Object> getStatus();
}
