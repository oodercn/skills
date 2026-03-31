package net.ooder.skill.org.base.spi;

import net.ooder.skill.common.spi.OrgSyncService;
import net.ooder.skill.common.spi.orgsync.SyncResult;
import net.ooder.skill.common.spi.orgsync.OrgUserInfo;
import net.ooder.skill.common.spi.orgsync.OrgDepartmentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.org.sync.enabled", havingValue = "true", matchIfMissing = false)
public class SkillOrgSyncServiceImpl implements OrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillOrgSyncServiceImpl.class);
    
    @Override
    public SyncResult syncAll(String platform) {
        log.info("[syncAll] platform={}", platform);
        return SyncResult.failure("请配置具体的平台同步服务实现");
    }
    
    @Override
    public SyncResult syncUsers(String platform) {
        log.info("[syncUsers] platform={}", platform);
        return SyncResult.failure("请配置具体的平台同步服务实现");
    }
    
    @Override
    public SyncResult syncDepartments(String platform) {
        log.info("[syncDepartments] platform={}", platform);
        return SyncResult.failure("请配置具体的平台同步服务实现");
    }
    
    @Override
    public List<OrgUserInfo> getUsers(String platform) {
        log.info("[getUsers] platform={}", platform);
        return new ArrayList<>();
    }
    
    @Override
    public List<OrgUserInfo> getUsersByDepartment(String platform, String departmentId) {
        log.info("[getUsersByDepartment] platform={}, departmentId={}", platform, departmentId);
        return new ArrayList<>();
    }
    
    @Override
    public OrgUserInfo getUser(String platform, String userId) {
        log.info("[getUser] platform={}, userId={}", platform, userId);
        return null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getDepartments(String platform) {
        log.info("[getDepartments] platform={}", platform);
        return new ArrayList<>();
    }
    
    @Override
    public OrgDepartmentInfo getDepartment(String platform, String departmentId) {
        log.info("[getDepartment] platform={}, departmentId={}", platform, departmentId);
        return null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getOrgTree(String platform) {
        log.info("[getOrgTree] platform={}", platform);
        return new ArrayList<>();
    }
    
    @Override
    public void clearCache(String platform) {
        log.info("[clearCache] platform={}", platform);
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Arrays.asList("dingtalk", "feishu", "wecom");
    }
}
