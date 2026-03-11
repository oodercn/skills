package net.ooder.skill.res.service;

import net.ooder.skill.res.dto.*;

import java.util.List;
import java.util.Map;

public interface ResService {
    // Organization operations
    List<Organization> listOrganizations();
    Organization createOrganization(Organization org);
    Organization getOrganization(String orgId);
    boolean deleteOrganization(String orgId);
    Organization updateOrganization(Organization org);
    List<Organization> getOrganizationTree(String orgId);
    
    // Department operations
    List<Department> listDepartments(String orgId);
    Department createDepartment(Department dept);
    Department getDepartment(String deptId);
    boolean deleteDepartment(String deptId);
    Department updateDepartment(Department dept);
    List<Department> getDepartmentTree(String deptId);
    
    // Member operations
    List<Member> listMembers(String orgId, String deptId);
    Member createMember(Member member);
    Member getMember(String memberId);
    boolean deleteMember(String memberId);
    Member updateMember(Member member);
    List<Member> getDepartmentMembers(String deptId);
    
    // Statistics
    Map<String, Object> getStatistics();
}
