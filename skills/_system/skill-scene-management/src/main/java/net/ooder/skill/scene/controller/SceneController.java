package net.ooder.skill.scene.controller;

import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.scene.model.Scene;
import net.ooder.skill.scene.service.SceneService;
import net.ooder.skill.scene.service.impl.SceneServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scenes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SceneController {

    private static final Logger log = LoggerFactory.getLogger(SceneController.class);

    private SceneService sceneService;

    public SceneController() {
        this.sceneService = new SceneServiceImpl();
    }

    public void setSceneService(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @GetMapping
    public ResultModel<List<Scene>> listScenes() {
        log.info("List all scenes");
        List<Scene> scenes = sceneService.findAll();
        return ResultModel.success(scenes);
    }

    @GetMapping("/{sceneId}")
    public ResultModel<Scene> getScene(@PathVariable String sceneId) {
        log.info("Get scene: {}", sceneId);
        Scene scene = sceneService.findById(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping
    public ResultModel<Scene> createScene(@RequestBody Scene scene) {
        log.info("Create scene: {}", scene.getName());
        Scene created = sceneService.create(scene);
        return ResultModel.success(created);
    }

    @PutMapping("/{sceneId}")
    public ResultModel<Scene> updateScene(
            @PathVariable String sceneId,
            @RequestBody Scene scene) {
        log.info("Update scene: {}", sceneId);
        scene.setSceneId(sceneId);
        Scene updated = sceneService.update(scene);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{sceneId}")
    public ResultModel<Boolean> deleteScene(@PathVariable String sceneId) {
        log.info("Delete scene: {}", sceneId);
        sceneService.delete(sceneId);
        return ResultModel.success(true);
    }

    @PostMapping("/{sceneId}/start")
    public ResultModel<Scene> startScene(@PathVariable String sceneId) {
        log.info("Start scene: {}", sceneId);
        Scene scene = sceneService.start(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping("/{sceneId}/stop")
    public ResultModel<Scene> stopScene(@PathVariable String sceneId) {
        log.info("Stop scene: {}", sceneId);
        Scene scene = sceneService.stop(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping("/{sceneId}/validate")
    public ResultModel<Boolean> validateScene(@PathVariable String sceneId) {
        log.info("Validate scene: {}", sceneId);
        boolean valid = sceneService.validate(sceneId);
        return ResultModel.success(valid);
    }
}
