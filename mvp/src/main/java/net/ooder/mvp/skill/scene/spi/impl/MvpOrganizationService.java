package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.OrganizationService;
import net.ooder.mvp.skill.scene.spi.org.DepartmentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MvpOrganizationService implements OrganizationService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpOrganizationService.class);
    
    private final Map<String, DepartmentInfo> departmentCache = new ConcurrentHashMap<>();
    
    public MvpOrganizationService() {
        initDefaultDepartments();
    }
    
    private void initDefaultDepartments() {
        DepartmentInfo root = new DepartmentInfo();
        root.setDepartmentId("dept-root");
        root.setDepartmentName("根部门");
        root.setParentId(null);
        root.setLevel(0);
        root.setManagerId("default-user");
        root.setMemberCount(1);
        departmentCache.put("dept-root", root);
        
        DepartmentInfo defaultDept = new DepartmentInfo();
        defaultDept.setDepartmentId("dept-default");
        defaultDept.setDepartmentName("默认部门");
        defaultDept.setParentId("dept-root");
        defaultDept.setLevel(1);
        defaultDept.setManagerId("default-user");
        defaultDept.setMemberCount(1);
        departmentCache.put("dept-default", defaultDept);
    }
    
    @Override
    public DepartmentInfo getDepartment(String departmentId) {
        if (departmentId == null || departmentId.isEmpty()) {
            return null;
        }
        
        DepartmentInfo dept = departmentCache.get(departmentId);
        if (dept == null) {
            log.warn("Department not found: {}", departmentId);
            dept = createUnknownDepartment(departmentId);
        }
        return dept;
    }
    
    @Override
    public List<DepartmentInfo> getChildDepartments(String parentDepartmentId) {
        if (parentDepartmentId == null || parentDepartmentId.isEmpty()) {
            return Collections.emptyList();
        }
        
        return departmentCache.values().stream()
            .filter(d -> parentDepartmentId.equals(d.getParentId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getDepartmentMembers(String departmentId) {
        DepartmentInfo dept = getDepartment(departmentId);
        if (dept == null) {
            return Collections.emptyList();
        }
        
        List<String> members = new ArrayList<>();
        if (dept.getManagerId() != null) {
            members.add(dept.getManagerId());
        }
        return members;
    }
    
    @Override
    public String getDepartmentManager(String departmentId) {
        DepartmentInfo dept = getDepartment(departmentId);
        return dept != null ? dept.getManagerId() : null;
    }
    
    @Override
    public List<DepartmentInfo> getUserHierarchy(String userId) {
        List<DepartmentInfo> hierarchy = new ArrayList<>();
        
        for (DepartmentInfo dept : departmentCache.values()) {
            if (userId.equals(dept.getManagerId())) {
                hierarchy.add(dept);
                addParentHierarchy(dept.getParentId(), hierarchy);
                break;
            }
        }
        
        if (hierarchy.isEmpty()) {
            hierarchy.add(getDepartment("dept-root"));
        }
        
        return hierarchy;
    }
    
    private void addParentHierarchy(String parentId, List<DepartmentInfo> hierarchy) {
        if (parentId == null) return;
        
        DepartmentInfo parent = departmentCache.get(parentId);
        if (parent != null) {
            hierarchy.add(parent);
            addParentHierarchy(parent.getParentId(), hierarchy);
        }
    }
    
    private DepartmentInfo createUnknownDepartment(String departmentId) {
        DepartmentInfo dept = new DepartmentInfo();
        dept.setDepartmentId(departmentId);
        dept.setDepartmentName("未知部门");
        dept.setParentId("dept-unknown");
        dept.setLevel(0);
        dept.setMemberCount(0);
        return dept;
    }
    
    public void registerDepartment(DepartmentInfo department) {
        if (department != null && department.getDepartmentId() != null) {
            departmentCache.put(department.getDepartmentId(), department);
            log.info("Registered department: {}", department.getDepartmentId());
        }
    }
}
