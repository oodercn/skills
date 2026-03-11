package net.ooder.skill.openwrt.controller;

import net.ooder.skill.openwrt.dto.*;
import net.ooder.skill.openwrt.service.OpenWrtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/openwrt")
public class OpenWrtController {

    @Autowired
    private OpenWrtService openWrtService;

    @PostMapping("/connect")
    public ResponseEntity<RouterConnection> connect(@RequestBody Map<String, Object> params) {
        String host = (String) params.get("host");
        int port = params.get("port") != null ? ((Number) params.get("port")).intValue() : 22;
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String type = (String) params.getOrDefault("type", "ssh");
        return ResponseEntity.ok(openWrtService.connect(host, port, username, password, type));
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Boolean> disconnect() {
        return ResponseEntity.ok(openWrtService.disconnect());
    }

    @GetMapping("/status")
    public ResponseEntity<RouterConnection> getStatus() {
        RouterConnection status = openWrtService.getStatus();
        if (status == null) {
            return ResponseEntity.ok(new RouterConnection());
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/info")
    public ResponseEntity<RouterInfo> getRouterInfo() {
        RouterInfo info = openWrtService.getRouterInfo();
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping("/uci/get")
    public ResponseEntity<String> getUciConfig(@RequestParam String configPath) {
        String config = openWrtService.getUciConfig(configPath);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    @PostMapping("/uci/set")
    public ResponseEntity<Boolean> setUciConfig(@RequestBody Map<String, Object> params) {
        String configPath = (String) params.get("configPath");
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) params.get("config");
        return ResponseEntity.ok(openWrtService.setUciConfig(configPath, config));
    }

    @PostMapping("/uci/commit")
    public ResponseEntity<Boolean> commitUciConfig(@RequestBody Map<String, String> params) {
        String configPath = params.get("configPath");
        return ResponseEntity.ok(openWrtService.commitUciConfig(configPath));
    }

    @PostMapping("/command")
    public ResponseEntity<CommandResult> executeCommand(@RequestBody Map<String, String> params) {
        String command = params.get("command");
        return ResponseEntity.ok(openWrtService.executeCommand(command));
    }

    @GetMapping("/wifi/list")
    public ResponseEntity<List<WifiNetwork>> getWifiNetworks() {
        return ResponseEntity.ok(openWrtService.getWifiNetworks());
    }

    @PostMapping("/wifi/update")
    public ResponseEntity<Boolean> updateWifiNetwork(@RequestBody Map<String, Object> params) {
        String networkId = (String) params.get("networkId");
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) params.get("config");
        return ResponseEntity.ok(openWrtService.updateWifiNetwork(networkId, config));
    }

    @PostMapping("/wifi/scan")
    public ResponseEntity<Boolean> scanWifiNetworks() {
        return ResponseEntity.ok(openWrtService.scanWifiNetworks());
    }

    @GetMapping("/dhcp/leases")
    public ResponseEntity<List<DhcpLease>> getDhcpLeases() {
        return ResponseEntity.ok(openWrtService.getDhcpLeases());
    }

    @GetMapping("/packages")
    public ResponseEntity<List<Map<String, Object>>> listPackages() {
        return ResponseEntity.ok(openWrtService.listPackages());
    }

    @PostMapping("/reboot")
    public ResponseEntity<Boolean> reboot() {
        return ResponseEntity.ok(openWrtService.reboot());
    }
}
