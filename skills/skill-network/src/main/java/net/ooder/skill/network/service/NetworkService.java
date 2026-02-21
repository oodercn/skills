package net.ooder.skill.network.service;

import net.ooder.skill.network.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NetworkService {
    
    private final Map<String, NetworkSetting> settings = new HashMap<>();
    private final Map<String, IPAddress> ipAddresses = new HashMap<>();
    private final Map<String, IPBlacklist> ipBlacklist = new HashMap<>();
    private final Map<String, NetworkDevice> networkDevices = new HashMap<>();
    
    public NetworkService() {
        initDefaultSettings();
    }
    
    private void initDefaultSettings() {
        NetworkSetting basic = new NetworkSetting();
        basic.setType("basic");
        basic.setName("Basic Network Settings");
        basic.setCategory("network");
        basic.setStatus("active");
        basic.setDescription("Basic network configuration");
        basic.setConfig(new HashMap<>());
        basic.setLastUpdated(System.currentTimeMillis());
        settings.put("basic", basic);
        
        NetworkSetting dns = new NetworkSetting();
        dns.setType("dns");
        dns.setName("DNS Settings");
        dns.setCategory("network");
        dns.setStatus("active");
        dns.setDescription("DNS server configuration");
        dns.setConfig(new HashMap<>());
        dns.setLastUpdated(System.currentTimeMillis());
        settings.put("dns", dns);
        
        NetworkSetting dhcp = new NetworkSetting();
        dhcp.setType("dhcp");
        dhcp.setName("DHCP Settings");
        dhcp.setCategory("network");
        dhcp.setStatus("active");
        dhcp.setDescription("DHCP server configuration");
        dhcp.setConfig(new HashMap<>());
        dhcp.setLastUpdated(System.currentTimeMillis());
        settings.put("dhcp", dhcp);
        
        NetworkSetting wifi = new NetworkSetting();
        wifi.setType("wifi");
        wifi.setName("WiFi Settings");
        wifi.setCategory("network");
        wifi.setStatus("active");
        wifi.setDescription("Wireless network configuration");
        wifi.setConfig(new HashMap<>());
        wifi.setLastUpdated(System.currentTimeMillis());
        settings.put("wifi", wifi);
    }
    
    public NetworkSetting getNetworkSetting(String settingType) {
        return settings.get(settingType);
    }
    
    public List<NetworkSetting> getAllNetworkSettings() {
        return new ArrayList<>(settings.values());
    }
    
    public NetworkSetting updateNetworkSetting(String settingType, Map<String, Object> data) {
        NetworkSetting setting = settings.get(settingType);
        if (setting == null) {
            setting = new NetworkSetting();
            setting.setType(settingType);
            settings.put(settingType, setting);
        }
        setting.setConfig(data);
        setting.setLastUpdated(System.currentTimeMillis());
        return setting;
    }
    
    public List<IPAddress> getIPAddresses(String type, String status) {
        return new ArrayList<>(ipAddresses.values());
    }
    
    public IPAddress addStaticIPAddress(Map<String, Object> ipData) {
        IPAddress address = new IPAddress();
        address.setId(UUID.randomUUID().toString());
        address.setIpAddress((String) ipData.get("ipAddress"));
        address.setType("static");
        address.setStatus("online");
        address.setDeviceName((String) ipData.get("deviceName"));
        address.setMacAddress((String) ipData.get("macAddress"));
        address.setLastActive(System.currentTimeMillis());
        ipAddresses.put(address.getId(), address);
        return address;
    }
    
    public IPAddress deleteIPAddress(String ipId) {
        return ipAddresses.remove(ipId);
    }
    
    public List<IPBlacklist> getIPBlacklist() {
        return new ArrayList<>(ipBlacklist.values());
    }
    
    public IPBlacklist addIPToBlacklist(Map<String, Object> data) {
        IPBlacklist item = new IPBlacklist();
        item.setId(UUID.randomUUID().toString());
        item.setIpAddress((String) data.get("ipAddress"));
        item.setReason((String) data.get("reason"));
        item.setSource("manual");
        item.setEnabled(true);
        item.setCreated(System.currentTimeMillis());
        ipBlacklist.put(item.getId(), item);
        return item;
    }
    
    public IPBlacklist removeIPFromBlacklist(String id) {
        return ipBlacklist.remove(id);
    }
    
    public List<NetworkDevice> getNetworkDevices() {
        return new ArrayList<>(networkDevices.values());
    }
}
