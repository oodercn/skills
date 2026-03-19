package net.ooder.skill.res.service.impl;

import net.ooder.skill.res.dto.*;
import net.ooder.skill.res.service.ResService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ResServiceImpl implements ResService {

    private final Map<String, Organization> organizations = new ConcurrentHashMap<>();
    private final Map<String, Department> departments = new ConcurrentHashMap<>();
    private final Map<String, Member> members = new ConcurrentHashMap<>();

    @Override
    public List<Organization> listOrganizations() {
        return new ArrayList<>(organizations.values());
    }

    @Override
    public Organization createOrganization(Organization org) {
        if (org.getOrgId() == null || org.getOrgId().isEmpty()) {
            org.setOrgId("org-" + UUID.randomUUID().toString().substring(0, 8));
        }
        org.setCreateTime(System.currentTimeMillis());
        org.setUpdateTime(System.currentTimeMillis());
        if (org.getParentId() != null) {
            Organization parent = organizations.get(org.getParentId());
            if (parent != null) {
                org.setLevel(parent.getLevel() + 1);
            }
        }
        organizations.put(org.getOrgId(), org);
        return org;
    }

    @Override
    public Organization getOrganization(String orgId) {
        return organizations.get(orgId);
    }

    @Override
    public boolean deleteOrganization(String orgId) {
        boolean hasChildren = organizations.values().stream()
                .anyMatch(o -> orgId.equals(o.getParentId()));
        if (hasChildren) {
            return false;
        }
        return organizations.remove(orgId) != null;
    }

    @Override
    public Organization updateOrganization(Organization org) {
        Organization existing = organizations.get(org.getOrgId());
        if (existing == null) {
            return null;
        }
        org.setUpdateTime(System.currentTimeMillis());
        organizations.put(org.getOrgId(), org);
        return org;
    }

    @Override
    public List<Organization> getOrganizationTree(String orgId) {
        List<Organization> result = new ArrayList<>();
        Organization org = organizations.get(orgId);
        if (org == null) {
            return result;
        }
        result.add(org);
        addChildOrganizations(orgId, result);
        return result;
    }
    
    private void addChildOrganizations(String parentId, List<Organization> result) {
        for (Organization org : organizations.values()) {
            if (parentId.equals(org.getParentId())) {
                result.add(org);
                addChildOrganizations(org.getOrgId(), result);
            }
        }
    }

    @Override
    public List<Department> listDepartments(String orgId) {
        return departments.values().stream()
                .filter(d -> orgId == null || orgId.equals(d.getOrgId()))
                .collect(Collectors.toList());
    }

    @Override
    public Department createDepartment(Department dept) {
        if (dept.getDeptId() == null || dept.getDeptId().isEmpty()) {
            dept.setDeptId("dept-" + UUID.randomUUID().toString().substring(0, 8));
        }
        dept.setCreateTime(System.currentTimeMillis());
        dept.setUpdateTime(System.currentTimeMillis());
        if (dept.getParentId() != null) {
            Department parent = departments.get(dept.getParentId());
            if (parent != null) {
                dept.setLevel(parent.getLevel() + 1);
            }
        }
        departments.put(dept.getDeptId(), dept);
        return dept;
    }

    @Override
    public Department getDepartment(String deptId) {
        return departments.get(deptId);
    }

    @Override
    public boolean deleteDepartment(String deptId) {
        boolean hasChildren = departments.values().stream()
                .anyMatch(d -> deptId.equals(d.getParentId()));
        if (hasChildren) {
            return false;
        }
        return departments.remove(deptId) != null;
    }

    @Override
    public Department updateDepartment(Department dept) {
        Department existing = departments.get(dept.getDeptId());
        if (existing == null) {
            return null;
        }
        dept.setUpdateTime(System.currentTimeMillis());
        departments.put(dept.getDeptId(), dept);
        return dept;
    }

    @Override
    public List<Department> getDepartmentTree(String deptId) {
        List<Department> result = new ArrayList<>();
        Department dept = departments.get(deptId);
        if (dept == null) {
            return result;
        }
        result.add(dept);
        addChildDepartments(deptId, result);
        return result;
    }
    
    private void addChildDepartments(String parentId, List<Department> result) {
        for (Department dept : departments.values()) {
            if (parentId.equals(dept.getParentId())) {
                result.add(dept);
                addChildDepartments(dept.getDeptId(), result);
            }
        }
    }

    @Override
    public List<Member> listMembers(String orgId, String deptId) {
        return members.values().stream()
                .filter(m -> orgId == null || orgId.equals(m.getOrgId()))
                .filter(m -> deptId == null || deptId.equals(m.getDeptId()))
                .collect(Collectors.toList());
    }

    @Override
    public Member createMember(Member member) {
        if (member.getMemberId() == null || member.getMemberId().isEmpty()) {
            member.setMemberId("member-" + UUID.randomUUID().toString().substring(0, 8));
        }
        member.setCreateTime(System.currentTimeMillis());
        member.setUpdateTime(System.currentTimeMillis());
        members.put(member.getMemberId(), member);
        
        if (member.getDeptId() != null) {
            Department dept = departments.get(member.getDeptId());
            if (dept != null) {
                dept.setMemberCount(dept.getMemberCount() + 1);
            }
        }
        
        return member;
    }

    @Override
    public Member getMember(String memberId) {
        return members.get(memberId);
    }

    @Override
    public boolean deleteMember(String memberId) {
        Member member = members.remove(memberId);
        if (member != null && member.getDeptId() != null) {
            Department dept = departments.get(member.getDeptId());
            if (dept != null) {
                dept.setMemberCount(Math.max(0, dept.getMemberCount() - 1));
            }
        }
        return member != null;
    }

    @Override
    public Member updateMember(Member member) {
        Member existing = members.get(member.getMemberId());
        if (existing == null) {
            return null;
        }
        member.setUpdateTime(System.currentTimeMillis());
        members.put(member.getMemberId(), member);
        return member;
    }

    @Override
    public List<Member> getDepartmentMembers(String deptId) {
        return members.values().stream()
                .filter(m -> deptId.equals(m.getDeptId()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrganizations", organizations.size());
        stats.put("totalDepartments", departments.size());
        stats.put("totalMembers", members.size());
        
        Map<String, Long> orgMemberCounts = new HashMap<>();
        for (Member m : members.values()) {
            orgMemberCounts.merge(m.getOrgId(), 1L, Long::sum);
        }
        stats.put("orgMemberCounts", orgMemberCounts);
        
        return stats;
    }
}
