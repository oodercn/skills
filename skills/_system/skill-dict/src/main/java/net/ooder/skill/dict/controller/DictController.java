package net.ooder.skill.dict.controller;

import net.ooder.skill.dict.dto.DictDTO;
import net.ooder.skill.dict.dto.DictItemDTO;
import net.ooder.skill.dict.dto.ResultModel;
import net.ooder.skill.dict.service.DictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/dicts")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DictController {

    private static final Logger log = LoggerFactory.getLogger(DictController.class);

    private DictService dictService;

    private DictService getDictService() {
        if (dictService == null) {
            dictService = new DictService();
        }
        return dictService;
    }

    @GetMapping
    public ResultModel<List<DictDTO>> getAllDicts() {
        log.info("[getAllDicts] Getting all dicts");
        List<DictDTO> dicts = getDictService().getAllDicts();
        return ResultModel.success(dicts);
    }

    @GetMapping("/{code}")
    public ResultModel<DictDTO> getDict(@PathVariable String code) {
        log.info("[getDict] code: {}", code);
        DictDTO dict = getDictService().getDict(code);
        if (dict == null) {
            return ResultModel.error(404, "字典不存在: " + code);
        }
        return ResultModel.success(dict);
    }

    @GetMapping("/{code}/items")
    public ResultModel<List<DictItemDTO>> getDictItems(@PathVariable String code) {
        log.info("[getDictItems] code: {}", code);
        List<DictItemDTO> items = getDictService().getDictItems(code);
        return ResultModel.success(items);
    }

    @GetMapping("/{code}/items/{itemCode}")
    public ResultModel<DictItemDTO> getDictItem(
            @PathVariable String code,
            @PathVariable String itemCode) {
        log.info("[getDictItem] code: {}, itemCode: {}", code, itemCode);
        DictItemDTO item = getDictService().getDictItem(code, itemCode);
        if (item == null) {
            return ResultModel.error(404, "字典项不存在: " + code + "/" + itemCode);
        }
        return ResultModel.success(item);
    }

    @GetMapping("/{code}/items/{itemCode}/name")
    public ResultModel<String> getDictItemName(
            @PathVariable String code,
            @PathVariable String itemCode) {
        log.info("[getDictItemName] code: {}, itemCode: {}", code, itemCode);
        String name = getDictService().getDictItemName(code, itemCode);
        return ResultModel.success(name);
    }

    @PostMapping("/refresh")
    public ResultModel<String> refreshCache() {
        log.info("[refreshCache] Refreshing dict cache");
        getDictService().refreshCache();
        return ResultModel.success("字典缓存已刷新");
    }
}
