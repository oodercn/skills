package net.ooder.skillcenter.controller;

import net.ooder.skillcenter.dto.hosting.*;
import net.ooder.skillcenter.service.hosting.HostingService;
import net.ooder.nexus.protocol.dto.ApiResponse;
import net.ooder.nexus.protocol.dto.PageResult;
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
    public ResponseEntity<ApiResponse<List<HostingInstance>>> getAllInstances() {
        List<HostingInstance> instances = hostingService.getAllInstances();
        return ResponseEntity.ok(ApiResponse.success(instances));
    }

    @GetMapping("/instances/paged")
    public ResponseEntity<ApiResponse<PageResult<HostingInstance>>> getInstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<HostingInstance> result = hostingService.getInstances(page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/instances")
    public ResponseEntity<ApiResponse<HostingInstance>> createInstance(@RequestBody HostingInstance instance) {
        HostingInstance created = hostingService.createInstance(instance);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping("/instances/{instanceId}")
    public ResponseEntity<ApiResponse<HostingInstance>> getInstance(@PathVariable String instanceId) {
        HostingInstance instance = hostingService.getInstance(instanceId);
        if (instance == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "Instance not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(instance));
    }

    @DeleteMapping("/instances/{instanceId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteInstance(@PathVariable String instanceId) {
        boolean result = hostingService.deleteInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/instances/{instanceId}/start")
    public ResponseEntity<ApiResponse<Boolean>> startInstance(@PathVariable String instanceId) {
        boolean result = hostingService.startInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/instances/{instanceId}/stop")
    public ResponseEntity<ApiResponse<Boolean>> stopInstance(@PathVariable String instanceId) {
        boolean result = hostingService.stopInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/instances/{instanceId}/scale")
    public ResponseEntity<ApiResponse<Boolean>> scaleInstance(
            @PathVariable String instanceId,
            @RequestBody Map<String, Integer> request) {
        Integer replicas = request.get("replicas");
        if (replicas == null || replicas <= 0) {
            return ResponseEntity.ok(ApiResponse.error(400, "Invalid replicas value"));
        }
        boolean result = hostingService.scaleInstance(instanceId, replicas);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/instances/{instanceId}/health")
    public ResponseEntity<ApiResponse<InstanceHealth>> getHealth(@PathVariable String instanceId) {
        InstanceHealth health = hostingService.getHealth(instanceId);
        if (health == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "Instance not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(health));
    }

    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<List<CloudProvider>>> listProviders() {
        List<CloudProvider> providers = hostingService.listProviders();
        return ResponseEntity.ok(ApiResponse.success(providers));
    }
}
