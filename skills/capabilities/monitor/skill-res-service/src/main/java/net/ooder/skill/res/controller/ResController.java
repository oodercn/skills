package net.ooder.skill.res.controller;

import net.ooder.skill.res.dto.*;
import net.ooder.skill.res.service.ResService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/res")
public class ResController {

    @Autowired
    private ResService resService;

    // Organization APIs
    @GetMapping("/org")
    public ResponseEntity<List<Organization>> listOrganizations() {
        return ResponseEntity.ok(resService.listOrganizations());
    }

    @PostMapping("/org")
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization org) {
        return ResponseEntity.ok(resService.createOrganization(org));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<Organization> getOrganization(@PathVariable String orgId) {
        Organization org = resService.getOrganization(orgId);
        if (org == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(org);
    }

    @DeleteMapping("/org/{orgId}")
    public ResponseEntity<Boolean> deleteOrganization(@PathVariable String orgId) {
        return ResponseEntity.ok(resService.deleteOrganization(orgId));
    }

    @PutMapping("/org/{orgId}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable String orgId, @RequestBody Organization org) {
        org.setOrgId(orgId);
        Organization updated = resService.updateOrganization(org);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/org/{orgId}/tree")
    public ResponseEntity<List<Organization>> getOrganizationTree(@PathVariable String orgId) {
        return ResponseEntity.ok(resService.getOrganizationTree(orgId));
    }

    // Department APIs
    @GetMapping("/dept")
    public ResponseEntity<List<Department>> listDepartments(@RequestParam(required = false) String orgId) {
        return ResponseEntity.ok(resService.listDepartments(orgId));
    }

    @PostMapping("/dept")
    public ResponseEntity<Department> createDepartment(@RequestBody Department dept) {
        return ResponseEntity.ok(resService.createDepartment(dept));
    }

    @GetMapping("/dept/{deptId}")
    public ResponseEntity<Department> getDepartment(@PathVariable String deptId) {
        Department dept = resService.getDepartment(deptId);
        if (dept == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dept);
    }

    @DeleteMapping("/dept/{deptId}")
    public ResponseEntity<Boolean> deleteDepartment(@PathVariable String deptId) {
        return ResponseEntity.ok(resService.deleteDepartment(deptId));
    }

    @PutMapping("/dept/{deptId}")
    public ResponseEntity<Department> updateDepartment(@PathVariable String deptId, @RequestBody Department dept) {
        dept.setDeptId(deptId);
        Department updated = resService.updateDepartment(dept);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/dept/{deptId}/members")
    public ResponseEntity<List<Member>> getDepartmentMembers(@PathVariable String deptId) {
        return ResponseEntity.ok(resService.getDepartmentMembers(deptId));
    }

    // Member APIs
    @GetMapping("/member")
    public ResponseEntity<List<Member>> listMembers(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String deptId) {
        return ResponseEntity.ok(resService.listMembers(orgId, deptId));
    }

    @PostMapping("/member")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        return ResponseEntity.ok(resService.createMember(member));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable String memberId) {
        Member member = resService.getMember(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Boolean> deleteMember(@PathVariable String memberId) {
        return ResponseEntity.ok(resService.deleteMember(memberId));
    }

    @PutMapping("/member/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable String memberId, @RequestBody Member member) {
        member.setMemberId(memberId);
        Member updated = resService.updateMember(member);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // Statistics API
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(resService.getStatistics());
    }
}
