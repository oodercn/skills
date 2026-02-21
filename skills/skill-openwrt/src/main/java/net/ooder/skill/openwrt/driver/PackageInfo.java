package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class PackageInfo {
    private String name;
    private String version;
    private boolean installed;
    private String description;
    private long size;
}
