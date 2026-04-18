package net.ooder.skill.dict.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 字典服务 - 临时实现，用于兼容现有代码
 * 后续应迁移到独立的 skill-dict 模块
 */
@Service
public class DictService {

    /**
     * 根据分类获取字典项
     */
    public List<DictItem> getDictItems(String category) {
        // 临时实现，返回空列表
        return List.of();
    }

    /**
     * 获取所有字典
     */
    public List<Dict> getAllDicts() {
        // 临时实现，返回空列表
        return List.of();
    }

    /**
     * 添加字典项
     */
    public void addDictItem(String category, String code, String name) {
        // 临时实现，空操作
    }

    /**
     * 根据编码获取字典项
     */
    public DictItem getDictItemByCode(String category, String code) {
        // 临时实现，返回空
        return null;
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        // 临时实现，空操作
    }

    /**
     * 字典项
     */
    public static class DictItem {
        private String code;
        private String name;
        private String category;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    /**
     * 字典
     */
    public static class Dict {
        private String code;
        private String name;
        private List<DictItem> items;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<DictItem> getItems() { return items; }
        public void setItems(List<DictItem> items) { this.items = items; }
    }
}
