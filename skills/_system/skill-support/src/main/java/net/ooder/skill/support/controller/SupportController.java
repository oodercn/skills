package net.ooder.skill.support.controller;

import net.ooder.skill.support.dto.*;
import net.ooder.skill.support.model.ResultModel;
import net.ooder.skill.support.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/support")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class SupportController {

    private static final Logger log = LoggerFactory.getLogger(SupportController.class);

    private final Map<String, SupportRequestDTO> requestStore = new HashMap<>();

    @GetMapping("/requests")
    public ResultModel<PageResult<SupportRequestDTO>> listRequests(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        
        log.info("[SupportController] List support requests - pageNum: {}, pageSize: {}", pageNum, pageSize);
        
        List<SupportRequestDTO> allRequests = new ArrayList<>(requestStore.values());
        
        if (status != null && !status.isEmpty()) {
            allRequests.removeIf(r -> !status.equals(r.getStatus()));
        }
        
        int total = allRequests.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<SupportRequestDTO> pageData = start < total ? allRequests.subList(start, end) : new ArrayList<>();
        
        PageResult<SupportRequestDTO> result = new PageResult<>();
        result.setData(pageData);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        
        return ResultModel.success(result);
    }

    @PostMapping("/requests")
    public ResultModel<SupportRequestDTO> createRequest(@RequestBody SupportRequestDTO request) {
        log.info("[SupportController] Create support request: {}", request.getSubject());
        
        String id = "req-" + UUID.randomUUID().toString().substring(0, 8);
        request.setId(id);
        request.setStatus("open");
        request.setCreateTime(System.currentTimeMillis());
        requestStore.put(id, request);
        
        return ResultModel.success(request);
    }

    @GetMapping("/requests/{id}")
    public ResultModel<SupportRequestDTO> getRequest(@PathVariable String id) {
        log.info("[SupportController] Get request: {}", id);
        
        SupportRequestDTO request = requestStore.get(id);
        if (request == null) {
            return ResultModel.notFound("Request not found: " + id);
        }
        
        return ResultModel.success(request);
    }

    @PutMapping("/requests/{id}")
    public ResultModel<SupportRequestDTO> updateRequest(@PathVariable String id, @RequestBody SupportRequestDTO request) {
        log.info("[SupportController] Update request: {}", id);
        
        if (!requestStore.containsKey(id)) {
            return ResultModel.notFound("Request not found: " + id);
        }
        
        SupportRequestDTO existing = requestStore.get(id);
        if (request.getSubject() != null) existing.setSubject(request.getSubject());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getPriority() != null) existing.setPriority(request.getPriority());
        existing.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(existing);
    }

    @DeleteMapping("/requests/{id}")
    public ResultModel<Boolean> deleteRequest(@PathVariable String id) {
        log.info("[SupportController] Delete request: {}", id);
        
        if (!requestStore.containsKey(id)) {
            return ResultModel.notFound("Request not found: " + id);
        }
        
        requestStore.remove(id);
        return ResultModel.success(true);
    }

    @PostMapping("/requests/{id}/close")
    public ResultModel<SupportRequestDTO> closeRequest(@PathVariable String id) {
        log.info("[SupportController] Close request: {}", id);
        
        SupportRequestDTO request = requestStore.get(id);
        if (request == null) {
            return ResultModel.notFound("Request not found: " + id);
        }
        
        request.setStatus("closed");
        request.setCloseTime(System.currentTimeMillis());
        
        return ResultModel.success(request);
    }
}
