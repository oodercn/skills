package net.ooder.skill.dict.service;

import net.ooder.skill.dict.dto.DictDTO;
import net.ooder.skill.dict.dto.DictItemDTO;
import java.util.List;

public class DictService {

    private final java.util.Map<String, DictDTO> dictCache = new java.util.concurrent.ConcurrentHashMap<>();

    public List<DictDTO> getAllDicts() {
        return new java.util.ArrayList<>(dictCache.values());
    }

    public DictDTO getDict(String code) {
        return dictCache.get(code);
    }

    public List<DictItemDTO> getDictItems(String code) {
        DictDTO dict = dictCache.get(code);
        return dict != null ? dict.getItems() : new java.util.ArrayList<>();
    }

    public DictItemDTO getDictItem(String code, String itemCode) {
        List<DictItemDTO> items = getDictItems(code);
        for (DictItemDTO item : items) {
            if (item.getCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    public String getDictItemName(String code, String itemCode) {
        DictItemDTO item = getDictItem(code, itemCode);
        return item != null ? item.getName() : itemCode;
    }

    public void refreshCache() {
        dictCache.clear();
    }

    public void registerDict(DictDTO dict) {
        dictCache.put(dict.getCode(), dict);
    }
}
