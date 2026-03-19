package net.ooder.skill.openwrt.service.impl;

import net.ooder.skill.openwrt.dto.*;
import net.ooder.skill.openwrt.service.OpenWrtService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OpenWrtServiceImpl implements OpenWrtService {

    private RouterConnection currentConnection;

    @Override
    public RouterConnection connect(String host, int port, String username, String password, String type) {
        disconnect();
        
        currentConnection = new RouterConnection();
        currentConnection.setHost(host);
        currentConnection.setPort(port);
        currentConnection.setUsername(username);
        currentConnection.setConnectionType(type != null ? type : "ssh");
        currentConnection.setStatus("connected");
        currentConnection.setConnectedAt(System.currentTimeMillis());
        currentConnection.setLastActivity(System.currentTimeMillis());
        
        return currentConnection;
    }

    @Override
    public boolean disconnect() {
        if (currentConnection != null) {
            currentConnection.setStatus("disconnected");
            currentConnection = null;
        }
        return true;
    }

    @Override
    public RouterConnection getStatus() {
        return currentConnection;
    }

    @Override
    public RouterInfo getRouterInfo() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return null;
        }
        
        RouterInfo info = new RouterInfo();
        info.setModelName("OpenWrt Router");
        info.setFirmwareVersion("23.05.0");
        info.setKernelVersion("5.15.137");
        info.setHostname("OpenWrt");
        info.setSystem("MediaTek MT7621 ver:1 eco:3");
        info.setArchitecture("mipsel_24kc");
        info.setUptime(System.currentTimeMillis() / 1000 % 1000000);
        info.setTotalMemory(128 * 1024 * 1024);
        info.setFreeMemory(64 * 1024 * 1024);
        info.setLoadAverage(0.5);
        
        return info;
    }

    @Override
    public String getUciConfig(String configPath) {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return null;
        }
        
        return "config interface 'lan'\n" +
               "    option type 'bridge'\n" +
               "    option ifname 'eth0.1'\n" +
               "    option proto 'static'\n" +
               "    option ipaddr '192.168.1.1'\n" +
               "    option netmask '255.255.255.0'\n";
    }

    @Override
    public boolean setUciConfig(String configPath, Map<String, Object> config) {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return false;
        }
        
        currentConnection.setLastActivity(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean commitUciConfig(String configPath) {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return false;
        }
        
        currentConnection.setLastActivity(System.currentTimeMillis());
        return true;
    }

    @Override
    public CommandResult executeCommand(String command) {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            CommandResult result = new CommandResult();
            result.setCommand(command);
            result.setStatus("error");
            result.setError("Not connected to router");
            return result;
        }
        
        CommandResult result = new CommandResult();
        result.setCommand(command);
        result.setStatus("success");
        result.setExitCode(0);
        result.setOutput("Command executed successfully");
        result.setExecutionTime(100);
        
        currentConnection.setLastActivity(System.currentTimeMillis());
        return result;
    }

    @Override
    public List<WifiNetwork> getWifiNetworks() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return new ArrayList<>();
        }
        
        List<WifiNetwork> networks = new ArrayList<>();
        
        WifiNetwork network1 = new WifiNetwork();
        network1.setNetworkId("radio0.network1");
        network1.setSsid("OpenWrt-2.4G");
        network1.setEncryption("psk2");
        network1.setChannel(6);
        network1.setMode("ap");
        network1.setHwmode("11g");
        network1.setDevice("radio0");
        networks.add(network1);
        
        WifiNetwork network2 = new WifiNetwork();
        network2.setNetworkId("radio1.network1");
        network2.setSsid("OpenWrt-5G");
        network2.setEncryption("psk2");
        network2.setChannel(36);
        network2.setMode("ap");
        network2.setHwmode("11ac");
        network2.setDevice("radio1");
        networks.add(network2);
        
        return networks;
    }

    @Override
    public boolean updateWifiNetwork(String networkId, Map<String, Object> config) {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return false;
        }
        
        currentConnection.setLastActivity(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean scanWifiNetworks() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return false;
        }
        
        currentConnection.setLastActivity(System.currentTimeMillis());
        return true;
    }

    @Override
    public List<DhcpLease> getDhcpLeases() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return new ArrayList<>();
        }
        
        List<DhcpLease> leases = new ArrayList<>();
        
        DhcpLease lease1 = new DhcpLease();
        lease1.setIp("192.168.1.100");
        lease1.setMac("00:11:22:33:44:55");
        lease1.setHostname("device1");
        lease1.setLeaseTime("12h");
        leases.add(lease1);
        
        DhcpLease lease2 = new DhcpLease();
        lease2.setIp("192.168.1.101");
        lease2.setMac("00:11:22:33:44:56");
        lease2.setHostname("device2");
        lease2.setLeaseTime("12h");
        leases.add(lease2);
        
        return leases;
    }

    @Override
    public List<Map<String, Object>> listPackages() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> packages = new ArrayList<>();
        
        Map<String, Object> pkg1 = new HashMap<>();
        pkg1.put("name", "luci");
        pkg1.put("version", "git-23.306.41243-fa1cc8b");
        pkg1.put("installed", true);
        packages.add(pkg1);
        
        Map<String, Object> pkg2 = new HashMap<>();
        pkg2.put("name", "firewall4");
        pkg2.put("version", "2023-03-23-04ab1a2b");
        pkg2.put("installed", true);
        packages.add(pkg2);
        
        return packages;
    }

    @Override
    public boolean reboot() {
        if (currentConnection == null || !"connected".equals(currentConnection.getStatus())) {
            return false;
        }
        
        currentConnection.setStatus("disconnected");
        return true;
    }
}
