package net.ooder.skill.openwrt.ssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class OpenWrtCommandExecutor {
    
    @Autowired
    private SshConnectionManager sshConnectionManager;
    
    public SshConnectionManager getSshConnectionManager() {
        return sshConnectionManager;
    }
    
    public String executeUciGet(String configPath) throws Exception {
        String command = "uci get " + configPath;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        if (!result.isSuccess()) {
            throw new RuntimeException("UCI get failed: " + result.getStderr());
        }
        
        return result.getStdout();
    }
    
    public boolean executeUciSet(String configPath, String value) throws Exception {
        String command = "uci set " + configPath + "=" + value;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean executeUciAddList(String configPath, String value) throws Exception {
        String command = "uci add_list " + configPath + "=" + value;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean executeUciDelete(String configPath) throws Exception {
        String command = "uci delete " + configPath;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean executeUciCommit(String config) throws Exception {
        String command = "uci commit " + config;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean executeReloadConfig() throws Exception {
        String command = "/etc/init.d/uci reload_config";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public List<Map<String, String>> getWifiNetworks() throws Exception {
        String command = "iwinfo";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        List<Map<String, String>> networks = new ArrayList<>();
        if (result.isSuccess() && !result.getStdout().isEmpty()) {
            String[] lines = result.getStdout().split("\n");
            for (String line : lines) {
                if (line.contains("ESSID")) {
                    Map<String, String> network = new HashMap<>();
                    String[] parts = line.split("\\s+");
                    if (parts.length > 0) {
                        network.put("interface", parts[0]);
                    }
                    int essidStart = line.indexOf('"');
                    int essidEnd = line.lastIndexOf('"');
                    if (essidStart >= 0 && essidEnd > essidStart) {
                        network.put("ssid", line.substring(essidStart + 1, essidEnd));
                    }
                    networks.add(network);
                }
            }
        }
        
        return networks;
    }
    
    public List<Map<String, String>> getDhcpLeases() throws Exception {
        String command = "cat /tmp/dhcp.leases";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        List<Map<String, String>> leases = new ArrayList<>();
        if (result.isSuccess() && !result.getStdout().isEmpty()) {
            String[] lines = result.getStdout().split("\n");
            for (String line : lines) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    Map<String, String> lease = new HashMap<>();
                    lease.put("mac", parts[0]);
                    lease.put("ip", parts[1]);
                    lease.put("hostname", parts[2]);
                    lease.put("leaseTime", parts[3]);
                    leases.add(lease);
                }
            }
        }
        
        return leases;
    }
    
    public Map<String, Object> getSystemInfo() throws Exception {
        Map<String, Object> info = new HashMap<>();
        
        SshConnectionManager.SshExecuteResult hostnameResult = sshConnectionManager.executeCommand("hostname");
        if (hostnameResult.isSuccess()) {
            info.put("hostname", hostnameResult.getStdout().trim());
        }
        
        SshConnectionManager.SshExecuteResult modelResult = sshConnectionManager.executeCommand("cat /tmp/sysinfo/model 2>/dev/null || echo 'Unknown'");
        if (modelResult.isSuccess()) {
            info.put("model", modelResult.getStdout().trim());
        }
        
        SshConnectionManager.SshExecuteResult firmwareResult = sshConnectionManager.executeCommand("cat /etc/openwrt_release | grep DISTRIB_DESCRIPTION | cut -d= -f2 | tr -d \"'\"");
        if (firmwareResult.isSuccess()) {
            info.put("firmware", firmwareResult.getStdout().trim());
        }
        
        SshConnectionManager.SshExecuteResult kernelResult = sshConnectionManager.executeCommand("uname -r");
        if (kernelResult.isSuccess()) {
            info.put("kernel", kernelResult.getStdout().trim());
        }
        
        SshConnectionManager.SshExecuteResult uptimeResult = sshConnectionManager.executeCommand("cat /proc/uptime | cut -d. -f1");
        if (uptimeResult.isSuccess()) {
            info.put("uptime", Long.parseLong(uptimeResult.getStdout().trim()));
        }
        
        return info;
    }
    
    public Map<String, Object> getSystemStatus() throws Exception {
        Map<String, Object> status = new HashMap<>();
        
        SshConnectionManager.SshExecuteResult memResult = sshConnectionManager.executeCommand("cat /proc/meminfo");
        if (memResult.isSuccess()) {
            String[] lines = memResult.getStdout().split("\n");
            long totalMem = 0, freeMem = 0;
            for (String line : lines) {
                if (line.startsWith("MemTotal:")) {
                    totalMem = Long.parseLong(line.split("\\s+")[1]);
                } else if (line.startsWith("MemFree:")) {
                    freeMem = Long.parseLong(line.split("\\s+")[1]);
                }
            }
            status.put("totalMemory", totalMem * 1024);
            status.put("freeMemory", freeMem * 1024);
            status.put("memoryUsage", totalMem > 0 ? (1 - (double) freeMem / totalMem) * 100 : 0);
        }
        
        SshConnectionManager.SshExecuteResult loadResult = sshConnectionManager.executeCommand("cat /proc/loadavg");
        if (loadResult.isSuccess()) {
            String[] loads = loadResult.getStdout().split("\\s+");
            if (loads.length >= 3) {
                status.put("load1", Double.parseDouble(loads[0]));
                status.put("load5", Double.parseDouble(loads[1]));
                status.put("load15", Double.parseDouble(loads[2]));
            }
        }
        
        return status;
    }
    
    public List<Map<String, Object>> listPackages() throws Exception {
        String command = "opkg list-installed";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        List<Map<String, Object>> packages = new ArrayList<>();
        if (result.isSuccess() && !result.getStdout().isEmpty()) {
            String[] lines = result.getStdout().split("\n");
            for (String line : lines) {
                String[] parts = line.split("\\s+", 3);
                if (parts.length >= 2) {
                    Map<String, Object> pkg = new HashMap<>();
                    pkg.put("name", parts[0]);
                    pkg.put("version", parts[1]);
                    pkg.put("installed", true);
                    if (parts.length > 2) {
                        pkg.put("description", parts[2]);
                    }
                    packages.add(pkg);
                }
            }
        }
        
        return packages;
    }
    
    public boolean installPackage(String packageName) throws Exception {
        String command = "opkg install " + packageName;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean removePackage(String packageName) throws Exception {
        String command = "opkg remove " + packageName;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public boolean updatePackages() throws Exception {
        String command = "opkg update";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return result.isSuccess();
    }
    
    public List<String> getSystemLogs(int lines) throws Exception {
        String command = "logread | tail -n " + lines;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        if (result.isSuccess()) {
            return Arrays.asList(result.getStdout().split("\n"));
        }
        return new ArrayList<>();
    }
    
    public List<String> getKernelLogs(int lines) throws Exception {
        String command = "dmesg | tail -n " + lines;
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        
        if (result.isSuccess()) {
            return Arrays.asList(result.getStdout().split("\n"));
        }
        return new ArrayList<>();
    }
    
    public boolean reboot() throws Exception {
        String command = "reboot";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return true;
    }
    
    public boolean reset() throws Exception {
        String command = "firstboot -y && reboot";
        SshConnectionManager.SshExecuteResult result = sshConnectionManager.executeCommand(command);
        return true;
    }
}
