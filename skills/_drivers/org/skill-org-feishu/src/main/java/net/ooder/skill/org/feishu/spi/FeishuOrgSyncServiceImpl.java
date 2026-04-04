package net.ooder.skill.org.feishu.spi;

import net.ooder.skill.common.spi.OrgSyncService;
import net.ooder.skill.common.spi.orgsync.SyncResult;
import net.ooder.skill.common.spi.orgsync.OrgUserInfo;
import net.ooder.skill.common.spi.orgsync.OrgDepartmentInfo;
import net.ooder.skill.org.feishu.service.FeishuOrgSyncService;
import net.ooder.skill.org.feishu.model.FeishuUser;
import net.ooder.skill.org.feishu.model.FeishuDepartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.org.feishu.enabled", havingValue = "true", matchIfMissing = false)
public class FeishuOrgSyncServiceImpl implements OrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuOrgSyncServiceImpl.class);
    
    @Autowired
    private FeishuOrgSyncService feishuOrgSyncService;
    
    @Override
    public SyncResult syncAll(String platform) {
        log.info("[syncAll] platform={}", platform);
        try {
            net.ooder.skill.org.feishu.dto.SyncResultDTO result = feishuOrgSyncService.syncAll();
            if (result.isSuccess()) {
                return SyncResult.success(result.getTotalUsers(), result.getTotalDepartments());
            } else {
                return SyncResult.failure(result.getMessage());
            }
        } catch (Exception e) {
            log.error("[syncAll] Failed to sync", e);
            return SyncResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SyncResult syncUsers(String platform) {
        log.info("[syncUsers] platform={}", platform);
        try {
            List<FeishuUser> users = feishuOrgSyncService.syncUsers();
            return SyncResult.success(users.size(), 0);
        } catch (Exception e) {
            log.error("[syncUsers] Failed to sync users", e);
            return SyncResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SyncResult syncDepartments(String platform) {
        log.info("[syncDepartments] platform={}", platform);
        try {
            List<FeishuDepartment> depts = feishuOrgSyncService.syncDepartments();
            return SyncResult.success(0, depts.size());
        } catch (Exception e) {
            log.error("[syncDepartments] Failed to sync departments", e);
            return SyncResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<OrgUserInfo> getUsers(String platform) {
        List<FeishuUser> users = feishuOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (FeishuUser user : users) {
            result.add(convertUser(user));
        }
        return result;
    }
    
    @Override
    public List<OrgUserInfo> getUsersByDepartment(String platform, String departmentId) {
        List<FeishuUser> users = feishuOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (FeishuUser user : users) {
            if (user.getDepartmentIds() != null && user.getDepartmentIds().contains(departmentId)) {
                result.add(convertUser(user));
            }
        }
        return result;
    }
    
    @Override
    public OrgUserInfo getUser(String platform, String userId) {
        FeishuUser user = feishuOrgSyncService.getCachedUser(userId);
        return user != null ? convertUser(user) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getDepartments(String platform) {
        List<FeishuDepartment> depts = feishuOrgSyncService.getCachedDepartments();
        List<OrgDepartmentInfo> result = new ArrayList<>();
        for (FeishuDepartment dept : depts) {
            result.add(convertDepartment(dept));
        }
        return result;
    }
    
    @Override
    public OrgDepartmentInfo getDepartment(String platform, String departmentId) {
        FeishuDepartment dept = feishuOrgSyncService.getCachedDepartment(departmentId);
        return dept != null ? convertDepartment(dept) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getOrgTree(String platform) {
        return getDepartments(platform);
    }
    
    @Override
    public void clearCache(String platform) {
        feishuOrgSyncService.clearCache();
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Collections.singletonList("feishu");
    }
    
    private OrgUserInfo convertUser(FeishuUser user) {
        OrgUserInfo info = new OrgUserInfo();
        info.setUserId(user.getUserId());
        info.setName(user.getName());
        info.setEmail(user.getEmail());
        info.setPhone(user.getMobile());
        info.setAvatar(user.getAvatarUrl());
        info.setTitle(user.getTitle());
        info.setDepartmentIds(user.getDepartmentIds());
        info.setActive(true);
        return info;
    }
    
    private OrgDepartmentInfo convertDepartment(FeishuDepartment dept) {
        OrgDepartmentInfo info = new OrgDepartmentInfo();
        info.setDepartmentId(dept.getDeptId());
        info.setName(dept.getName());
        info.setParentId(dept.getParentDeptId());
        info.setManagerId(dept.getLeaderUserId());
        return info;
    }
}
