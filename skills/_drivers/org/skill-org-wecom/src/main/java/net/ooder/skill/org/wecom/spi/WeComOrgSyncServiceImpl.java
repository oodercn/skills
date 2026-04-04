package net.ooder.skill.org.wecom.spi;

import net.ooder.skill.common.spi.OrgSyncService;
import net.ooder.skill.common.spi.orgsync.SyncResult;
import net.ooder.skill.common.spi.orgsync.OrgUserInfo;
import net.ooder.skill.common.spi.orgsync.OrgDepartmentInfo;
import net.ooder.skill.org.wecom.service.WeComOrgSyncService;
import net.ooder.skill.org.wecom.model.WeComUser;
import net.ooder.skill.org.wecom.model.WeComDepartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.org.wecom.enabled", havingValue = "true", matchIfMissing = false)
public class WeComOrgSyncServiceImpl implements OrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(WeComOrgSyncServiceImpl.class);
    
    @Autowired
    private WeComOrgSyncService wecomOrgSyncService;
    
    @Override
    public SyncResult syncAll(String platform) {
        log.info("[syncAll] platform={}", platform);
        try {
            net.ooder.skill.org.wecom.dto.SyncResultDTO result = wecomOrgSyncService.syncAll();
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
            List<WeComUser> users = wecomOrgSyncService.syncUsers();
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
            List<WeComDepartment> depts = wecomOrgSyncService.syncDepartments();
            return SyncResult.success(0, depts.size());
        } catch (Exception e) {
            log.error("[syncDepartments] Failed to sync departments", e);
            return SyncResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<OrgUserInfo> getUsers(String platform) {
        List<WeComUser> users = wecomOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (WeComUser user : users) {
            result.add(convertUser(user));
        }
        return result;
    }
    
    @Override
    public List<OrgUserInfo> getUsersByDepartment(String platform, String departmentId) {
        List<WeComUser> users = wecomOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (WeComUser user : users) {
            if (departmentId.equals(user.getDepartment())) {
                result.add(convertUser(user));
            }
        }
        return result;
    }
    
    @Override
    public OrgUserInfo getUser(String platform, String userId) {
        WeComUser user = wecomOrgSyncService.getCachedUser(userId);
        return user != null ? convertUser(user) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getDepartments(String platform) {
        List<WeComDepartment> depts = wecomOrgSyncService.getCachedDepartments();
        List<OrgDepartmentInfo> result = new ArrayList<>();
        for (WeComDepartment dept : depts) {
            result.add(convertDepartment(dept));
        }
        return result;
    }
    
    @Override
    public OrgDepartmentInfo getDepartment(String platform, String departmentId) {
        WeComDepartment dept = wecomOrgSyncService.getCachedDepartment(departmentId);
        return dept != null ? convertDepartment(dept) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getOrgTree(String platform) {
        return getDepartments(platform);
    }
    
    @Override
    public void clearCache(String platform) {
        wecomOrgSyncService.clearCache();
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Collections.singletonList("wecom");
    }
    
    private OrgUserInfo convertUser(WeComUser user) {
        OrgUserInfo info = new OrgUserInfo();
        info.setUserId(user.getUserid());
        info.setName(user.getName());
        info.setEmail(user.getEmail());
        info.setPhone(user.getMobile());
        info.setAvatar(user.getAvatar());
        info.setTitle(user.getPosition());
        info.setDepartmentIds(java.util.Arrays.asList(user.getDepartment()));
        info.setActive(true);
        return info;
    }
    
    private OrgDepartmentInfo convertDepartment(WeComDepartment dept) {
        OrgDepartmentInfo info = new OrgDepartmentInfo();
        info.setDepartmentId(dept.getDeptId());
        info.setName(dept.getName());
        info.setParentId(dept.getParentId());
        info.setManagerId(dept.getManager());
        return info;
    }
}
