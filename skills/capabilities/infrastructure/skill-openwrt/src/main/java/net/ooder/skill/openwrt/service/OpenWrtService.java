package net.ooder.skill.openwrt.service;

import net.ooder.skill.openwrt.dto.*;

import java.util.List;
import java.util.Map;

public interface OpenWrtService {
    RouterConnection connect(String host, int port, String username, String password, String type);
    boolean disconnect();
    RouterConnection getStatus();
    RouterInfo getRouterInfo();
    String getUciConfig(String configPath);
    boolean setUciConfig(String configPath, Map<String, Object> config);
    boolean commitUciConfig(String configPath);
    CommandResult executeCommand(String command);
    List<WifiNetwork> getWifiNetworks();
    boolean updateWifiNetwork(String networkId, Map<String, Object> config);
    boolean scanWifiNetworks();
    List<DhcpLease> getDhcpLeases();
    List<Map<String, Object>> listPackages();
    boolean reboot();
}
