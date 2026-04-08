package net.ooder.skill.todo.controller;

import net.ooder.skill.todo.dto.TaskDTO;
import net.ooder.skill.todo.dto.HistorySceneDTO;
import net.ooder.skill.todo.model.ResultModel;
import net.ooder.skill.todo.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/my")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class MyTaskController {

    private static final Logger log = LoggerFactory.getLogger(MyTaskController.class);

    private final Map<String, TaskDTO> taskStore = new HashMap<>();

    @GetMapping("/tasks")
    public ResultModel<PageResult<TaskDTO>> listMyTasks(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[MyTaskController] List my tasks - status: {}", status);
        
        List<TaskDTO> allTasks = new ArrayList<>(taskStore.values());
        
        if (status != null) {
            allTasks = allTasks.stream()
                    .filter(t -> status.equals(t.getStatus()))
                    .toList();
        }
        
        PageResult<TaskDTO> result = new PageResult<>();
        result.setList(allTasks);
        result.setTotal(allTasks.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return ResultModel.success(result);
    }

    @PostMapping("/tasks/{id}/complete")
    public ResultModel<TaskDTO> completeTask(@PathVariable String id) {
        log.info("[MyTaskController] Complete task: {}", id);
        
        TaskDTO task = taskStore.get(id);
        if (task == null) {
            task = new TaskDTO();
            task.setId(id);
            taskStore.put(id, task);
        }
        
        task.setStatus("completed");
        task.setCompletedTime(new Date().toString());
        
        return ResultModel.success(task);
    }

    @GetMapping("/history/scenes")
    public ResultModel<List<HistorySceneDTO>> getHistoryScenes() {
        log.info("[MyTaskController] Get history scenes");
        List<HistorySceneDTO> history = new ArrayList<>();
        return ResultModel.success(history);
    }
}
