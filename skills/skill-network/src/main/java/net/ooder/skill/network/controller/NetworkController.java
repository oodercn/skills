package net.ooder.skill.network.controller;

import net.ooder.skill.network.model.*;
import net.ooder.skill.network.provider.NetworkProvider;
import net.ooder.skill.network.provider.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
public class NetworkController {
    
    @Autowired
    private NetworkProvider networkProvider;
    
    @GetMapping("/settings")
    public Result<List<NetworkSetting>> getAllNetworkSettings() {
        return networkProvider.getAllNetworkSettings();
    }
    
    @GetMapping("/settings/{type}")
    public Result<NetworkSetting> getNetworkSetting(@PathVariable String type) {
        return networkProvider.getNetworkSetting(type);
    }
    
    @PutMapping("/settings/{type}")
    public Result<NetworkSetting> updateNetworkSetting(
            @PathVariable String type,
            @RequestBody Map<String, Object> data) {
        return networkProvider.updateNetworkSetting(type, data);
    }
    
    @GetMapping("/ips")
    public Result<List<IPAddress>> getIPAddresses(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return networkProvider.getIPAddresses(type, status);
    }
    
    @PostMapping("/ips")
    public Result<IPAddress> addStaticIPAddress(@RequestBody Map<String, Object> ipData) {
        return networkProvider.addStaticIPAddress(ipData);
    }
    
    @DeleteMapping("/ips/{id}")
    public Result<IPAddress> deleteIPAddress(@PathVariable String id) {
        return networkProvider.deleteIPAddress(id);
    }
    
    @GetMapping("/blacklist")
    public Result<List<IPBlacklist>> getIPBlacklist() {
        return networkProvider.getIPBlacklist();
    }
    
    @PostMapping("/blacklist")
    public Result<IPBlacklist> addIPToBlacklist(@RequestBody Map<String, Object> data) {
        return networkProvider.addIPToBlacklist(data);
    }
    
    @DeleteMapping("/blacklist/{id}")
    public Result<IPBlacklist> removeIPFromBlacklist(@PathVariable String id) {
        return networkProvider.removeIPFromBlacklist(id);
    }
    
    @GetMapping("/devices")
    public Result<List<NetworkDevice>> getNetworkDevices() {
        return networkProvider.getNetworkDevices();
    }
}
