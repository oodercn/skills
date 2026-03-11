package net.ooder.skill.network.provider;

import net.ooder.skill.network.model.*;
import net.ooder.skill.network.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NetworkProvider {
    
    @Autowired
    private NetworkService networkService;
    
    public Result<NetworkSetting> getNetworkSetting(String settingType) {
        try {
            NetworkSetting setting = networkService.getNetworkSetting(settingType);
            return Result.success(setting);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<List<NetworkSetting>> getAllNetworkSettings() {
        try {
            List<NetworkSetting> settings = networkService.getAllNetworkSettings();
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data) {
        try {
            NetworkSetting setting = networkService.updateNetworkSetting(settingType, data);
            return Result.success(setting);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<List<IPAddress>> getIPAddresses(String type, String status) {
        try {
            List<IPAddress> addresses = networkService.getIPAddresses(type, status);
            return Result.success(addresses);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData) {
        try {
            IPAddress address = networkService.addStaticIPAddress(ipData);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<IPAddress> deleteIPAddress(String ipId) {
        try {
            IPAddress address = networkService.deleteIPAddress(ipId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<List<IPBlacklist>> getIPBlacklist() {
        try {
            List<IPBlacklist> blacklist = networkService.getIPBlacklist();
            return Result.success(blacklist);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data) {
        try {
            IPBlacklist item = networkService.addIPToBlacklist(data);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<IPBlacklist> removeIPFromBlacklist(String id) {
        try {
            IPBlacklist item = networkService.removeIPFromBlacklist(id);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
    
    public Result<List<NetworkDevice>> getNetworkDevices() {
        try {
            List<NetworkDevice> devices = networkService.getNetworkDevices();
            return Result.success(devices);
        } catch (Exception e) {
            return Result.error(e.getMessage(), 500);
        }
    }
}
