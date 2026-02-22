package net.ooder.skill.k8s.controller;

import net.ooder.skill.k8s.dto.*;
import net.ooder.skill.k8s.service.K8sService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/k8s")
public class K8sController {

    @Autowired
    private K8sService k8sService;

    @GetMapping("/clusters")
    public ResponseEntity<List<ClusterInfo>> listClusters() {
        return ResponseEntity.ok(k8sService.listClusters());
    }

    @GetMapping("/clusters/{clusterId}")
    public ResponseEntity<ClusterInfo> getClusterInfo(@PathVariable String clusterId) {
        ClusterInfo cluster = k8sService.getClusterInfo(clusterId);
        if (cluster == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cluster);
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<NodeInfo>> listNodes() {
        return ResponseEntity.ok(k8sService.listNodes());
    }

    @GetMapping("/nodes/{nodeName}")
    public ResponseEntity<NodeInfo> getNodeInfo(@PathVariable String nodeName) {
        NodeInfo node = k8sService.getNodeInfo(nodeName);
        if (node == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(node);
    }

    @GetMapping("/namespaces")
    public ResponseEntity<List<NamespaceInfo>> listNamespaces() {
        return ResponseEntity.ok(k8sService.listNamespaces());
    }

    @PostMapping("/namespaces")
    public ResponseEntity<NamespaceInfo> createNamespace(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        NamespaceInfo ns = k8sService.createNamespace(name);
        if (ns == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(ns);
    }

    @DeleteMapping("/namespaces/{namespace}")
    public ResponseEntity<K8sResult> deleteNamespace(@PathVariable String namespace) {
        boolean result = k8sService.deleteNamespace(namespace);
        if (result) {
            return ResponseEntity.ok(K8sResult.success("Namespace deleted"));
        }
        return ResponseEntity.internalServerError().body(K8sResult.fail("Failed to delete namespace"));
    }

    @GetMapping("/pods")
    public ResponseEntity<List<PodInfo>> listPods(@RequestParam(required = false) String namespace) {
        return ResponseEntity.ok(k8sService.listPods(namespace));
    }

    @GetMapping("/pods/{namespace}/{podName}")
    public ResponseEntity<PodInfo> getPodInfo(@PathVariable String namespace, @PathVariable String podName) {
        PodInfo pod = k8sService.getPodInfo(namespace, podName);
        if (pod == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pod);
    }

    @GetMapping("/pods/{namespace}/{podName}/logs")
    public ResponseEntity<String> getPodLogs(
            @PathVariable String namespace,
            @PathVariable String podName,
            @RequestParam(required = false, defaultValue = "100") Integer tailLines) {
        String logs = k8sService.getPodLogs(namespace, podName, tailLines);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("mockMode", k8sService.isMockMode());
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}
