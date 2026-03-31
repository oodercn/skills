package net.ooder.skill.org.dingding.spi;

import net.ooder.skill.common.spi.OrgSyncService;
import net.ooder.skill.common.spi.orgsync.SyncResult;
import net.ooder.skill.common.spi.orgsync.OrgUserInfo;
import net.ooder.skill.common.spi.orgsync.OrgDepartmentInfo;
import net.ooder.skill.org.dingding.service.DingTalkOrgSyncService;
import net.ooder.skill.org.dingding.model.DingdingUser;
import net.ooder.skill.org.dingding.model.DingdingDepartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.org.dingtalk.enabled", havingValue = "true", matchIfMissing = false)
public class DingTalkOrgSyncServiceImpl implements OrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkOrgSyncServiceImpl.class);
    
    @Autowired
    private DingTalkOrgSyncService dingTalkOrgSyncService;
    
    @Override
    public SyncResult syncAll(String platform) {
        log.info("[syncAll] platform={}", platform);
        try {
            net.ooder.skill.org.dingding.dto.SyncResultDTO result = dingTalkOrgSyncService.syncAll();
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
            List<DingdingUser> users = dingTalkOrgSyncService.syncUsers();
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
            List<DingdingDepartment> depts = dingTalkOrgSyncService.syncDepartments();
            return SyncResult.success(0, depts.size());
        } catch (Exception e) {
            log.error("[syncDepartments] Failed to sync departments", e);
            return SyncResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<OrgUserInfo> getUsers(String platform) {
        List<DingdingUser> users = dingTalkOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (DingdingUser user : users) {
            result.add(convertUser(user));
        }
        return result;
    }
    
    @Override
    public List<OrgUserInfo> getUsersByDepartment(String platform, String departmentId) {
        List<DingdingUser> users = dingTalkOrgSyncService.getCachedUsers();
        List<OrgUserInfo> result = new ArrayList<>();
        for (DingdingUser user : users) {
            if (user.getDeptIdList() != null && user.getDeptIdList().contains(departmentId)) {
                result.add(convertUser(user));
            }
        }
        return result;
    }
    
    @Override
    public OrgUserInfo getUser(String platform, String userId) {
        DingdingUser user = dingTalkOrgSyncService.getCachedUser(userId);
        return user != null ? convertUser(user) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getDepartments(String platform) {
        List<DingdingDepartment> depts = dingTalkOrgSyncService.getCachedDepartments();
        List<OrgDepartmentInfo> result = new ArrayList<>();
        for (DingdingDepartment dept : depts) {
            result.add(convertDepartment(dept));
        }
        return result;
    }
    
    @Override
    public OrgDepartmentInfo getDepartment(String platform, String departmentId) {
        DingdingDepartment dept = dingTalkOrgSyncService.getCachedDepartment(departmentId);
        return dept != null ? convertDepartment(dept) : null;
    }
    
    @Override
    public List<OrgDepartmentInfo> getOrgTree(String platform) {
        return getDepartments(platform);
    }
    
    @Override
    public void clearCache(String platform) {
        dingTalkOrgSyncService.clearCache();
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Collections.singletonList("dingtalk");
    }
    
    private OrgUserInfo convertUser(DingdingUser user) {
        OrgUserInfo info = new OrgUserInfo();
        info.setUserId(user.getUserid());
        info.setName(user.getName());
        info.setEmail(user.getEmail());
        info.setPhone(user.getMobile());
        info.setAvatar(user.getAvatar());
        info.setTitle(user.getTitle());
        info.setDepartmentIds(user.getDeptIdList());
        info.setActive(true);
        return info;
    }
    
    private OrgDepartmentInfo convertDepartment(DingdingDepartment dept) {
        OrgDepartmentInfo info = new OrgDepartmentInfo();
        info.setDepartmentId(dept.getDeptId());
        info.setName(dept.getName());
        info.setParentId(dept.getParentId());
        info.setManagerId(dept.getDeptManagerUserid());
        return info;
    }
}
