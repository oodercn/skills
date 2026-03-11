package net.ooder.skill.openwrt.ssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class UciConfigHandler {
    
    @Autowired
    private SshConnectionManager sshConnectionManager;
    
    @Autowired
    private OpenWrtCommandExecutor commandExecutor;
    
    public SshConnectionManager getSshConnectionManager() {
        return sshConnectionManager;
    }
    
    public String getConfig(String configPath) throws Exception {
        return commandExecutor.executeUciGet(configPath);
    }
    
    public Map<String, Object> getConfigSection(String config, String section) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        String basePath = config + "." + section;
        String sectionType = commandExecutor.executeUciGet(basePath);
        result.put(".type", sectionType);
        
        String showCommand = "uci show " + basePath;
        SshConnectionManager.SshExecuteResult showResult = commandExecutor.getSshConnectionManager().executeCommand(showCommand);
        
        if (showResult.isSuccess()) {
            String[] lines = showResult.getStdout().split("\n");
            for (String line : lines) {
                int dotIndex = line.lastIndexOf('.');
                int equalIndex = line.indexOf('=');
                if (dotIndex >= 0 && equalIndex > dotIndex) {
                    String key = line.substring(dotIndex + 1, equalIndex);
                    String value = line.substring(equalIndex + 1);
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    public List<Map<String, Object>> getConfigSections(String config) throws Exception {
        List<Map<String, Object>> sections = new ArrayList<>();
        
        String command = "uci show " + config;
        SshConnectionManager.SshExecuteResult result = commandExecutor.getSshConnectionManager().executeCommand(command);
        
        if (result.isSuccess()) {
            Map<String, Map<String, Object>> sectionMap = new LinkedHashMap<>();
            String[] lines = result.getStdout().split("\n");
            
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String path = parts[0];
                    String value = parts[1];
                    
                    String[] pathParts = path.split("\\.");
                    if (pathParts.length >= 2) {
                        String sectionName = pathParts[1];
                        
                        sectionMap.computeIfAbsent(sectionName, k -> {
                            Map<String, Object> section = new HashMap<>();
                            section.put(".name", k);
                            return section;
                        });
                        
                        if (pathParts.length == 2) {
                            sectionMap.get(sectionName).put(".type", value);
                        } else if (pathParts.length == 3) {
                            sectionMap.get(sectionName).put(pathParts[2], value);
                        }
                    }
                }
            }
            
            sections.addAll(sectionMap.values());
        }
        
        return sections;
    }
    
    public boolean setConfig(String configPath, String value) throws Exception {
        return commandExecutor.executeUciSet(configPath, value);
    }
    
    public boolean setConfigSection(String config, String section, Map<String, Object> values) throws Exception {
        String basePath = config + "." + section;
        
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(".")) {
                String path = basePath + "." + key;
                String value = String.valueOf(entry.getValue());
                commandExecutor.executeUciSet(path, value);
            }
        }
        
        return true;
    }
    
    public boolean addListValue(String configPath, String value) throws Exception {
        return commandExecutor.executeUciAddList(configPath, value);
    }
    
    public boolean deleteConfig(String configPath) throws Exception {
        return commandExecutor.executeUciDelete(configPath);
    }
    
    public boolean commitConfig(String config) throws Exception {
        return commandExecutor.executeUciCommit(config);
    }
    
    public boolean applyConfig(String config) throws Exception {
        commandExecutor.executeUciCommit(config);
        
        String serviceCommand = "/etc/init.d/" + config + " restart";
        SshConnectionManager.SshExecuteResult result = commandExecutor.getSshConnectionManager().executeCommand(serviceCommand);
        
        return result.isSuccess();
    }
    
    public Map<String, Object> getNetworkConfig() throws Exception {
        return getConfigSection("network", "lan");
    }
    
    public boolean setNetworkConfig(Map<String, Object> config) throws Exception {
        return setConfigSection("network", "lan", config);
    }
    
    public Map<String, Object> getWirelessConfig() throws Exception {
        List<Map<String, Object>> sections = getConfigSections("wireless");
        if (!sections.isEmpty()) {
            return sections.get(0);
        }
        return new HashMap<>();
    }
    
    public boolean setWirelessConfig(String device, Map<String, Object> config) throws Exception {
        return setConfigSection("wireless", device, config);
    }
    
    public Map<String, Object> getDhcpConfig() throws Exception {
        return getConfigSection("dhcp", "lan");
    }
    
    public boolean setDhcpConfig(Map<String, Object> config) throws Exception {
        return setConfigSection("dhcp", "lan", config);
    }
    
    public List<Map<String, Object>> getFirewallZones() throws Exception {
        return getConfigSections("firewall");
    }
    
    public boolean addFirewallRule(Map<String, Object> rule) throws Exception {
        String command = "uci add firewall rule";
        SshConnectionManager.SshExecuteResult result = commandExecutor.getSshConnectionManager().executeCommand(command);
        
        if (result.isSuccess()) {
            String ruleName = result.getStdout().trim();
            return setConfigSection("firewall", ruleName, rule);
        }
        
        return false;
    }
}
