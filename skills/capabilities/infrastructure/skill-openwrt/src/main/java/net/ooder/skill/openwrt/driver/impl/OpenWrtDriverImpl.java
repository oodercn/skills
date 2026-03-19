package net.ooder.skill.openwrt.driver.impl;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.openwrt.driver.*;
import net.ooder.skill.openwrt.ssh.OpenWrtCommandExecutor;
import net.ooder.skill.openwrt.ssh.SshConnectionManager;
import net.ooder.skill.openwrt.ssh.UciConfigHandler;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class OpenWrtDriverImpl implements OpenWrtDriver {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    @Autowired
    private SshConnectionManager sshConnectionManager;
    
    @Autowired
    private OpenWrtCommandExecutor commandExecutor;
    
    @Autowired
    private UciConfigHandler uciConfigHandler;
    
    private ConnectionStatus connectionStatus = new ConnectionStatus();
    private final Map<String, NetworkSetting> networkSettings = new HashMap<>();
    private final Map<String, IPAddress> ipAddresses = new HashMap<>();
    private final Map<String, IPBlacklist> ipBlacklist = new HashMap<>();
    private final Map<String, StaticLease> staticLeases = new HashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-openwrt";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }
    
    @Override
    public void start() {
        this.running = true;
    }
    
    @Override
    public void stop() {
        this.running = false;
        if (sshConnectionManager.isConnected()) {
            sshConnectionManager.disconnect();
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public Result<String> getUciConfig(String configPath) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            String config = uciConfigHandler.getConfig(configPath);
            return Result.success(config);
        } catch (Exception e) {
            log.error("Failed to get UCI config: {}", configPath, e);
            return Result.error("Failed to get UCI config: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> setUciConfig(String configPath, Map<String, Object> config) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            boolean result = uciConfigHandler.setConfigSection(
                configPath.split("\\.")[0],
                configPath.split("\\.")[1],
                config
            );
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to set UCI config: {}", configPath, e);
            return Result.error("Failed to set UCI config: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> commitUciConfig(String configPath) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            String config = configPath.split("\\.")[0];
            boolean result = uciConfigHandler.commitConfig(config);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to commit UCI config: {}", configPath, e);
            return Result.error("Failed to commit UCI config: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<WifiNetwork>> getWifiNetworks() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<Map<String, String>> networks = commandExecutor.getWifiNetworks();
            List<WifiNetwork> result = new ArrayList<>();
            for (Map<String, String> net : networks) {
                WifiNetwork network = new WifiNetwork();
                network.setNetworkId(net.get("interface"));
                network.setSsid(net.get("ssid"));
                network.setEnabled(true);
                result.add(network);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get WiFi networks", e);
            return Result.error("Failed to get WiFi networks: " + e.getMessage());
        }
    }
    
    @Override
    public Result<WifiNetwork> getWifiNetwork(String networkId) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            Map<String, Object> config = uciConfigHandler.getConfigSection("wireless", networkId);
            WifiNetwork network = new WifiNetwork();
            network.setNetworkId(networkId);
            network.setSsid((String) config.get("ssid"));
            network.setEnabled(!"0".equals(config.get("disabled")));
            network.setEncryption((String) config.get("encryption"));
            return Result.success(network);
        } catch (Exception e) {
            log.error("Failed to get WiFi network: {}", networkId, e);
            return Result.error("Failed to get WiFi network: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> updateWifiNetwork(String networkId, Map<String, Object> config) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            boolean result = uciConfigHandler.setWirelessConfig(networkId, config);
            uciConfigHandler.commitConfig("wireless");
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to update WiFi network: {}", networkId, e);
            return Result.error("Failed to update WiFi network: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> scanWifiNetworks() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand("iwlist scan");
            return Result.success(result.isSuccess());
        } catch (Exception e) {
            log.error("Failed to scan WiFi networks", e);
            return Result.error("Failed to scan WiFi networks: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<DhcpLease>> getDhcpLeases() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<Map<String, String>> leases = commandExecutor.getDhcpLeases();
            List<DhcpLease> result = new ArrayList<>();
            for (Map<String, String> lease : leases) {
                DhcpLease dhcpLease = new DhcpLease();
                dhcpLease.setIp(lease.get("ip"));
                dhcpLease.setMac(lease.get("mac"));
                dhcpLease.setHostname(lease.get("hostname"));
                dhcpLease.setLeaseTime(lease.get("leaseTime"));
                result.add(dhcpLease);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get DHCP leases", e);
            return Result.error("Failed to get DHCP leases: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<StaticLease>> getStaticLeases() {
        return Result.success(new ArrayList<>(staticLeases.values()));
    }
    
    @Override
    public Result<StaticLease> addStaticLease(Map<String, Object> leaseData) {
        StaticLease lease = new StaticLease();
        lease.setId(UUID.randomUUID().toString());
        lease.setIp((String) leaseData.get("ip"));
        lease.setMac((String) leaseData.get("mac"));
        lease.setHostname((String) leaseData.get("hostname"));
        staticLeases.put(lease.getId(), lease);
        return Result.success(lease);
    }
    
    @Override
    public Result<Boolean> deleteStaticLease(String leaseId) {
        staticLeases.remove(leaseId);
        return Result.success(true);
    }
    
    @Override
    public Result<List<PackageInfo>> listPackages() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<Map<String, Object>> packages = commandExecutor.listPackages();
            List<PackageInfo> result = new ArrayList<>();
            for (Map<String, Object> pkg : packages) {
                PackageInfo info = new PackageInfo();
                info.setName((String) pkg.get("name"));
                info.setVersion((String) pkg.get("version"));
                info.setInstalled(true);
                info.setDescription((String) pkg.get("description"));
                result.add(info);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to list packages", e);
            return Result.error("Failed to list packages: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> installPackage(String packageName) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            boolean result = commandExecutor.installPackage(packageName);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to install package: {}", packageName, e);
            return Result.error("Failed to install package: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> removePackage(String packageName) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            boolean result = commandExecutor.removePackage(packageName);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to remove package: {}", packageName, e);
            return Result.error("Failed to remove package: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> updatePackages() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            boolean result = commandExecutor.updatePackages();
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to update packages", e);
            return Result.error("Failed to update packages: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<LogEntry>> getSystemLogs(int lines) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<String> logs = commandExecutor.getSystemLogs(lines);
            List<LogEntry> result = new ArrayList<>();
            for (String log : logs) {
                LogEntry entry = new LogEntry();
                entry.setTimestamp(System.currentTimeMillis());
                entry.setMessage(log);
                entry.setSource("system");
                result.add(entry);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get system logs", e);
            return Result.error("Failed to get system logs: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<LogEntry>> getKernelLogs(int lines) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<String> logs = commandExecutor.getKernelLogs(lines);
            List<LogEntry> result = new ArrayList<>();
            for (String log : logs) {
                LogEntry entry = new LogEntry();
                entry.setTimestamp(System.currentTimeMillis());
                entry.setMessage(log);
                entry.setSource("kernel");
                result.add(entry);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get kernel logs", e);
            return Result.error("Failed to get kernel logs: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> connect(Map<String, Object> connectionData) {
        try {
            String host = (String) connectionData.get("host");
            int port = (Integer) connectionData.getOrDefault("port", 22);
            String username = (String) connectionData.get("username");
            String password = (String) connectionData.get("password");
            String privateKeyPath = (String) connectionData.get("privateKeyPath");
            
            sshConnectionManager.connect(host, port, username, password, privateKeyPath);
            
            connectionStatus.setConnected(true);
            connectionStatus.setHost(host);
            connectionStatus.setPort(port);
            connectionStatus.setUsername(username);
            connectionStatus.setConnectedAt(System.currentTimeMillis());
            
            log.info("Connected to OpenWrt device: {}@{}:{}", username, host, port);
            return Result.success(true);
        } catch (JSchException e) {
            log.error("Failed to connect to OpenWrt device", e);
            connectionStatus.setConnected(false);
            connectionStatus.setError(e.getMessage());
            return Result.error("Failed to connect: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> disconnect() {
        sshConnectionManager.disconnect();
        connectionStatus.setConnected(false);
        log.info("Disconnected from OpenWrt device");
        return Result.success(true);
    }
    
    @Override
    public Result<ConnectionStatus> getConnectionStatus() {
        connectionStatus.setConnected(sshConnectionManager.isConnected());
        return Result.success(connectionStatus);
    }
    
    @Override
    public Result<NetworkSetting> getNetworkSetting(String settingType) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            Map<String, Object> config = uciConfigHandler.getConfigSection("network", settingType);
            NetworkSetting setting = new NetworkSetting();
            setting.setType(settingType);
            setting.setName(settingType);
            setting.setConfig(config);
            setting.setLastUpdated(System.currentTimeMillis());
            return Result.success(setting);
        } catch (Exception e) {
            log.error("Failed to get network setting: {}", settingType, e);
            return Result.error("Failed to get network setting: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<NetworkSetting>> getAllNetworkSettings() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            List<Map<String, Object>> sections = uciConfigHandler.getConfigSections("network");
            List<NetworkSetting> result = new ArrayList<>();
            for (Map<String, Object> section : sections) {
                NetworkSetting setting = new NetworkSetting();
                setting.setType((String) section.get(".type"));
                setting.setName((String) section.get(".name"));
                setting.setConfig(section);
                setting.setLastUpdated(System.currentTimeMillis());
                result.add(setting);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get all network settings", e);
            return Result.error("Failed to get all network settings: " + e.getMessage());
        }
    }
    
    @Override
    public Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            uciConfigHandler.setConfigSection("network", settingType, data);
            uciConfigHandler.commitConfig("network");
            
            NetworkSetting setting = new NetworkSetting();
            setting.setType(settingType);
            setting.setConfig(data);
            setting.setLastUpdated(System.currentTimeMillis());
            return Result.success(setting);
        } catch (Exception e) {
            log.error("Failed to update network setting: {}", settingType, e);
            return Result.error("Failed to update network setting: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<IPAddress>> getIPAddresses(String type, String status) {
        return Result.success(new ArrayList<>(ipAddresses.values()));
    }
    
    @Override
    public Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData) {
        IPAddress address = new IPAddress();
        address.setId(UUID.randomUUID().toString());
        address.setIpAddress((String) ipData.get("ipAddress"));
        address.setType("static");
        address.setStatus("active");
        address.setDeviceName((String) ipData.get("deviceName"));
        address.setMacAddress((String) ipData.get("macAddress"));
        address.setLastActive(System.currentTimeMillis());
        ipAddresses.put(address.getId(), address);
        return Result.success(address);
    }
    
    @Override
    public Result<IPAddress> deleteIPAddress(String ipId) {
        return Result.success(ipAddresses.remove(ipId));
    }
    
    @Override
    public Result<List<IPBlacklist>> getIPBlacklist() {
        return Result.success(new ArrayList<>(ipBlacklist.values()));
    }
    
    @Override
    public Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data) {
        IPBlacklist item = new IPBlacklist();
        item.setId(UUID.randomUUID().toString());
        item.setIpAddress((String) data.get("ipAddress"));
        item.setReason((String) data.get("reason"));
        item.setEnabled(true);
        item.setCreated(System.currentTimeMillis());
        ipBlacklist.put(item.getId(), item);
        return Result.success(item);
    }
    
    @Override
    public Result<IPBlacklist> removeIPFromBlacklist(String id) {
        return Result.success(ipBlacklist.remove(id));
    }
    
    @Override
    public Result<List<NetworkDevice>> getNetworkDevices() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand("ip link show");
            List<NetworkDevice> devices = new ArrayList<>();
            if (result.isSuccess()) {
                String[] lines = result.getStdout().split("\n");
                for (int i = 0; i < lines.length; i += 2) {
                    if (i + 1 < lines.length) {
                        NetworkDevice device = new NetworkDevice();
                        String[] parts = lines[i].split(":");
                        if (parts.length >= 2) {
                            device.setName(parts[1].trim().split("@")[0]);
                            device.setId(String.valueOf(i / 2));
                            device.setStatus(lines[i].contains("UP") ? "up" : "down");
                        }
                        if (lines[i + 1].contains("link/ether")) {
                            String[] macParts = lines[i + 1].trim().split("\\s+");
                            if (macParts.length >= 2) {
                                device.setMacAddress(macParts[1]);
                            }
                        }
                        devices.add(device);
                    }
                }
            }
            return Result.success(devices);
        } catch (Exception e) {
            log.error("Failed to get network devices", e);
            return Result.error("Failed to get network devices: " + e.getMessage());
        }
    }
    
    @Override
    public Result<DeviceInfo> getDeviceInfo() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            Map<String, Object> info = commandExecutor.getSystemInfo();
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setModel((String) info.getOrDefault("model", "Unknown"));
            deviceInfo.setFirmware((String) info.getOrDefault("firmware", "Unknown"));
            deviceInfo.setKernel((String) info.getOrDefault("kernel", "Unknown"));
            deviceInfo.setHostname((String) info.getOrDefault("hostname", "OpenWrt"));
            deviceInfo.setUptime(info.containsKey("uptime") ? (Long) info.get("uptime") : 0);
            return Result.success(deviceInfo);
        } catch (Exception e) {
            log.error("Failed to get device info", e);
            return Result.error("Failed to get device info: " + e.getMessage());
        }
    }
    
    @Override
    public Result<SystemStatus> getSystemStatus() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            Map<String, Object> status = commandExecutor.getSystemStatus();
            SystemStatus systemStatus = new SystemStatus();
            systemStatus.setStatus("running");
            systemStatus.setTotalMemory((Long) status.getOrDefault("totalMemory", 0L));
            systemStatus.setFreeMemory((Long) status.getOrDefault("freeMemory", 0L));
            systemStatus.setMemoryUsage((Double) status.getOrDefault("memoryUsage", 0.0));
            systemStatus.setLoad1(((Double) status.getOrDefault("load1", 0.0)).intValue());
            systemStatus.setLoad5(((Double) status.getOrDefault("load5", 0.0)).intValue());
            systemStatus.setLoad15(((Double) status.getOrDefault("load15", 0.0)).intValue());
            return Result.success(systemStatus);
        } catch (Exception e) {
            log.error("Failed to get system status", e);
            return Result.error("Failed to get system status: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> reboot() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            commandExecutor.reboot();
            return Result.success(true);
        } catch (Exception e) {
            log.error("Failed to reboot", e);
            return Result.error("Failed to reboot: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> reset() {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            commandExecutor.reset();
            return Result.success(true);
        } catch (Exception e) {
            log.error("Failed to reset", e);
            return Result.error("Failed to reset: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CommandResult> executeCommand(String command) {
        if (!isConnected()) {
            return Result.error("Not connected to OpenWrt device");
        }
        try {
            long startTime = System.currentTimeMillis();
            SshConnectionManager.SshExecuteResult sshResult = sshConnectionManager.executeCommand(command);
            long duration = System.currentTimeMillis() - startTime;
            
            CommandResult result = new CommandResult();
            result.setExitCode(sshResult.getExitCode());
            result.setStdout(sshResult.getStdout());
            result.setStderr(sshResult.getStderr());
            result.setDuration(duration);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to execute command: {}", command, e);
            return Result.error("Failed to execute command: " + e.getMessage());
        }
    }
    
    private boolean isConnected() {
        return sshConnectionManager.isConnected();
    }
}
