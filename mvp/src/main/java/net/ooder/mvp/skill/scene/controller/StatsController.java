package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.capability.service.CapabilityService;
import net.ooder.mvp.skill.scene.service.SceneService;
import net.ooder.mvp.skill.scene.model.ResultModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StatsController {

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private SceneService sceneService;

    @GetMapping("/users")
    public ResultModel<Integer> getUserCount() {
        int count = 1;
        return ResultModel.success(count);
    }

    @GetMapping("/overview")
    public ResultModel<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<String, Object>();
        
        int capabilityCount = capabilityService.findAll().size();
        overview.put("capabilities", capabilityCount);
        
        overview.put("users", 1);
        overview.put("scenes", 0);
        
        return ResultModel.success(overview);
    }
}
