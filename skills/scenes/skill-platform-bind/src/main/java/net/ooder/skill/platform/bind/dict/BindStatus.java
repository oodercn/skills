package net.ooder.skill.platform.bind.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "bind_status", name = "绑定状态")
public enum BindStatus implements DictItem {
    PENDING("PENDING", "待扫码", "等待用户扫码", "ri-qr-code-line", 1),
    SCANNED("SCANNED", "已扫码", "用户已扫码待确认", "ri-smartphone-line", 2),
    CONFIRMED("CONFIRMED", "已确认", "用户已确认授权", "ri-check-line", 3),
    BOUND("BOUND", "已绑定", "绑定成功", "ri-link-line", 4),
    EXPIRED("EXPIRED", "已过期", "二维码过期", "ri-time-line", 5),
    FAILED("FAILED", "绑定失败", "绑定过程失败", "ri-close-line", 6),
    UNBOUND("UNBOUND", "已解绑", "已解除绑定", "ri-link-unlink-line", 7);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    BindStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
