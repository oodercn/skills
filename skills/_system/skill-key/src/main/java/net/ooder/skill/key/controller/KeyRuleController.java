package net.ooder.skill.key.controller;

import net.ooder.skill.key.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/key-rules")
@CrossOrigin(originPatterns = "*")
public class KeyRuleController {

    private static final Logger log = LoggerFactory.getLogger(KeyRuleController.class);

    @Autowired(required = false)
    private KeyRuleService ruleService;

    @GetMapping
    public ResponseEntity<List<KeyRuleDTO>> listRules(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String status) {
        log.info("[listRules] scope={}, status={}", scope, status);
        
        if (ruleService == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<KeyRuleDTO> rules = ruleService.getAllRules();
        
        if (scope != null && !scope.isEmpty()) {
            rules = rules.stream()
                .filter(r -> scope.equals(r.getScope()))
                .toList();
        }
        
        if (status != null && !status.isEmpty()) {
            rules = rules.stream()
                .filter(r -> status.equals(r.getStatus()))
                .toList();
        }
        
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<KeyRuleDTO> getRule(@PathVariable String ruleId) {
        log.info("[getRule] ruleId={}", ruleId);
        
        if (ruleService == null) {
            return ResponseEntity.notFound().build();
        }
        
        KeyRuleDTO rule = ruleService.getRule(ruleId);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(rule);
    }

    @PostMapping
    public ResponseEntity<KeyRuleDTO> createRule(@RequestBody KeyRuleDTO request) {
        log.info("[createRule] request={}", request);
        
        if (ruleService == null) {
            return ResponseEntity.internalServerError().build();
        }
        
        KeyRuleDTO rule = ruleService.createRule(request);
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<KeyRuleDTO> updateRule(
            @PathVariable String ruleId,
            @RequestBody KeyRuleDTO request) {
        log.info("[updateRule] ruleId={}", ruleId);
        
        if (ruleService == null) {
            return ResponseEntity.notFound().build();
        }
        
        request.setRuleId(ruleId);
        KeyRuleDTO rule = ruleService.updateRule(request);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String ruleId) {
        log.info("[deleteRule] ruleId={}", ruleId);
        
        if (ruleService != null) {
            ruleService.deleteRule(ruleId);
        }
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ruleId}/enable")
    public ResponseEntity<KeyRuleDTO> enableRule(@PathVariable String ruleId) {
        log.info("[enableRule] ruleId={}", ruleId);
        
        if (ruleService == null) {
            return ResponseEntity.notFound().build();
        }
        
        KeyRuleDTO rule = ruleService.enableRule(ruleId);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(rule);
    }

    @PostMapping("/{ruleId}/disable")
    public ResponseEntity<KeyRuleDTO> disableRule(@PathVariable String ruleId) {
        log.info("[disableRule] ruleId={}", ruleId);
        
        if (ruleService == null) {
            return ResponseEntity.notFound().build();
        }
        
        KeyRuleDTO rule = ruleService.disableRule(ruleId);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(rule);
    }
}
