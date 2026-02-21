package net.ooder.skill.network.controller;

import net.ooder.skill.network.dto.*;
import net.ooder.skill.network.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("/status")
    public ResponseEntity<NetworkStatus> getStatus() {
        return ResponseEntity.ok(networkService.getStatus());
    }

    @GetMapping("/stats")
    public ResponseEntity<NetworkStats> getStats() {
        return ResponseEntity.ok(networkService.getStats());
    }

    @GetMapping("/links")
    public ResponseEntity<PageResult<NetworkLink>> listLinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(networkService.listLinks(page, size));
    }

    @GetMapping("/links/{linkId}")
    public ResponseEntity<NetworkLink> getLink(@PathVariable String linkId) {
        NetworkLink link = networkService.getLink(linkId);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(link);
    }

    @PostMapping("/links/{linkId}/disconnect")
    public ResponseEntity<Boolean> disconnectLink(@PathVariable String linkId) {
        return ResponseEntity.ok(networkService.disconnectLink(linkId));
    }

    @PostMapping("/links/{linkId}/reconnect")
    public ResponseEntity<Boolean> reconnectLink(@PathVariable String linkId) {
        return ResponseEntity.ok(networkService.reconnectLink(linkId));
    }

    @GetMapping("/routes")
    public ResponseEntity<PageResult<NetworkRoute>> listRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(networkService.listRoutes(page, size));
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<NetworkRoute> getRoute(@PathVariable String routeId) {
        NetworkRoute route = networkService.getRoute(routeId);
        if (route == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(route);
    }

    @GetMapping("/routes/find")
    public ResponseEntity<NetworkRoute> findRoute(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String target,
            @RequestParam(defaultValue = "shortest") String algorithm,
            @RequestParam(defaultValue = "10") int maxHops) {
        return ResponseEntity.ok(networkService.findRoute(source, target, algorithm, maxHops));
    }

    @GetMapping("/topology")
    public ResponseEntity<NetworkTopology> getTopology() {
        return ResponseEntity.ok(networkService.getTopology());
    }

    @GetMapping("/quality")
    public ResponseEntity<NetworkQuality> getQuality() {
        return ResponseEntity.ok(networkService.getQuality());
    }
}
