package net.ooder.mvp.skill.scene.network.controller;

import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.network.dto.LinkDTO;
import net.ooder.mvp.skill.scene.network.dto.NetworkTopologyDTO;
import net.ooder.mvp.skill.scene.network.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("/topology")
    public ResultModel<NetworkTopologyDTO> getTopology() {
        NetworkTopologyDTO topology = networkService.getTopology();
        return ResultModel.success(topology);
    }

    @GetMapping("/links")
    public ResultModel<List<LinkDTO>> listLinks() {
        List<LinkDTO> links = networkService.listLinks();
        return ResultModel.success(links);
    }

    @GetMapping("/links/{linkId}")
    public ResultModel<LinkDTO> getLink(@PathVariable String linkId) {
        LinkDTO link = networkService.getLink(linkId);
        if (link != null) {
            return ResultModel.success(link);
        }
        return ResultModel.error("Link not found: " + linkId);
    }

    @GetMapping("/links/search")
    public ResultModel<List<LinkDTO>> searchLinks(@RequestParam String keyword) {
        List<LinkDTO> links = networkService.searchLinks(keyword);
        return ResultModel.success(links);
    }

    @PostMapping("/links/{linkId}/reconnect")
    public ResultModel<Boolean> reconnectLink(@PathVariable String linkId) {
        boolean success = networkService.reconnectLink(linkId);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to reconnect link: " + linkId);
    }

    @DeleteMapping("/links/{linkId}")
    public ResultModel<Boolean> disconnectLink(@PathVariable String linkId) {
        boolean success = networkService.disconnectLink(linkId);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to disconnect link: " + linkId);
    }

    @GetMapping("/links/{linkId}/binding-count")
    public ResultModel<Map<String, Object>> getBindingCount(@PathVariable String linkId) {
        int count = networkService.getBindingCount(linkId);
        Map<String, Object> result = new HashMap<>();
        result.put("linkId", linkId);
        result.put("bindingCount", count);
        return ResultModel.success(result);
    }

    @GetMapping("/stats")
    public ResultModel<Map<String, Object>> getStats() {
        List<LinkDTO> allLinks = networkService.listLinks();
        
        int total = allLinks.size();
        int active = 0;
        int degraded = 0;
        int failed = 0;
        
        for (LinkDTO link : allLinks) {
            String status = link.getStatus();
            if ("active".equalsIgnoreCase(status)) {
                active++;
            } else if ("degraded".equalsIgnoreCase(status)) {
                degraded++;
            } else if ("failed".equalsIgnoreCase(status)) {
                failed++;
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("degraded", degraded);
        stats.put("failed", failed);
        
        return ResultModel.success(stats);
    }
}
