package net.ooder.skill.hosting.controller;

import net.ooder.skill.hosting.dto.*;
import net.ooder.skill.hosting.service.HostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosting")
public class HostingController {

    @Autowired
    private HostingService hostingService;

    @GetMapping("/instances")
    public ResponseEntity<List<HostingInstance>> getAllInstances() {
        return ResponseEntity.ok(hostingService.getAllInstances());
    }

    @GetMapping("/instances/paged")
    public ResponseEntity<PageResult<HostingInstance>> getInstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(hostingService.getInstances(page, size));
    }

    @PostMapping("/instances")
    public ResponseEntity<HostingInstance> createInstance(@RequestBody HostingInstance instance) {
        return ResponseEntity.ok(hostingService.createInstance(instance));
    }

    @GetMapping("/instances/{instanceId}")
    public ResponseEntity<HostingInstance> getInstance(@PathVariable String instanceId) {
        HostingInstance instance = hostingService.getInstance(instanceId);
        if (instance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(instance);
    }

    @DeleteMapping("/instances/{instanceId}")
    public ResponseEntity<Boolean> deleteInstance(@PathVariable String instanceId) {
        return ResponseEntity.ok(hostingService.deleteInstance(instanceId));
    }

    @PostMapping("/instances/{instanceId}/start")
    public ResponseEntity<Boolean> startInstance(@PathVariable String instanceId) {
        return ResponseEntity.ok(hostingService.startInstance(instanceId));
    }

    @PostMapping("/instances/{instanceId}/stop")
    public ResponseEntity<Boolean> stopInstance(@PathVariable String instanceId) {
        return ResponseEntity.ok(hostingService.stopInstance(instanceId));
    }

    @PostMapping("/instances/{instanceId}/scale")
    public ResponseEntity<Boolean> scaleInstance(
            @PathVariable String instanceId,
            @RequestBody Map<String, Integer> request) {
        Integer replicas = request.get("replicas");
        if (replicas == null || replicas <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(hostingService.scaleInstance(instanceId, replicas));
    }

    @GetMapping("/instances/{instanceId}/health")
    public ResponseEntity<InstanceHealth> getHealth(@PathVariable String instanceId) {
        InstanceHealth health = hostingService.getHealth(instanceId);
        if (health == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(health);
    }

    @GetMapping("/providers")
    public ResponseEntity<List<CloudProvider>> listProviders() {
        return ResponseEntity.ok(hostingService.listProviders());
    }
}
