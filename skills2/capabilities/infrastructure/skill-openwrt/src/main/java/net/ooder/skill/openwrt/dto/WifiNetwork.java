package net.ooder.skill.openwrt.dto;

public class WifiNetwork {
    private String networkId;
    private String ssid;
    private String encryption;
    private int channel;
    private boolean enabled;
    private String mode;
    private String hwmode;
    private int txpower;
    private String device;

    public WifiNetwork() {
        this.enabled = true;
        this.mode = "ap";
        this.channel = 1;
        this.txpower = 20;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getHwmode() {
        return hwmode;
    }

    public void setHwmode(String hwmode) {
        this.hwmode = hwmode;
    }

    public int getTxpower() {
        return txpower;
    }

    public void setTxpower(int txpower) {
        this.txpower = txpower;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
