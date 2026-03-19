package net.ooder.skill.openwrt.driver;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.BaseProvider;

import java.util.List;
import java.util.Map;

public interface OpenWrtDriver extends BaseProvider {

    Result<String> getUciConfig(String configPath);
    Result<Boolean> setUciConfig(String configPath, Map<String, Object> config);
    Result<Boolean> commitUciConfig(String configPath);
    
    Result<List<WifiNetwork>> getWifiNetworks();
    Result<WifiNetwork> getWifiNetwork(String networkId);
    Result<Boolean> updateWifiNetwork(String networkId, Map<String, Object> config);
    Result<Boolean> scanWifiNetworks();
    
    Result<List<DhcpLease>> getDhcpLeases();
    Result<List<StaticLease>> getStaticLeases();
    Result<StaticLease> addStaticLease(Map<String, Object> leaseData);
    Result<Boolean> deleteStaticLease(String leaseId);
    
    Result<List<PackageInfo>> listPackages();
    Result<Boolean> installPackage(String packageName);
    Result<Boolean> removePackage(String packageName);
    Result<Boolean> updatePackages();
    
    Result<List<LogEntry>> getSystemLogs(int lines);
    Result<List<LogEntry>> getKernelLogs(int lines);
    
    Result<Boolean> connect(Map<String, Object> connectionData);
    Result<Boolean> disconnect();
    Result<ConnectionStatus> getConnectionStatus();
    
    Result<NetworkSetting> getNetworkSetting(String settingType);
    Result<List<NetworkSetting>> getAllNetworkSettings();
    Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data);
    
    Result<List<IPAddress>> getIPAddresses(String type, String status);
    Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData);
    Result<IPAddress> deleteIPAddress(String ipId);
    
    Result<List<IPBlacklist>> getIPBlacklist();
    Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data);
    Result<IPBlacklist> removeIPFromBlacklist(String id);
    
    Result<List<NetworkDevice>> getNetworkDevices();
    
    Result<DeviceInfo> getDeviceInfo();
    Result<SystemStatus> getSystemStatus();
    Result<Boolean> reboot();
    Result<Boolean> reset();
    Result<CommandResult> executeCommand(String command);
}
