package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.model.Scene;
import net.ooder.skill.scene.service.SceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SceneServiceImpl implements SceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    private final Map<String, Scene> scenes = new ConcurrentHashMap<>();

    public SceneServiceImpl() {
        initDefaultScenes();
    }

    private void initDefaultScenes() {
        Scene dailyReport = new Scene();
        dailyReport.setSceneId("scene-daily-report");
        dailyReport.setName("日报场景");
        dailyReport.setDescription("自动生成和发送日报");
        dailyReport.setType("automation");
        dailyReport.setStatus(Scene.SceneStatus.ACTIVE);
        dailyReport.setCapabilities(Arrays.asList("llm-chat", "email-send", "document-generate"));
        scenes.put(dailyReport.getSceneId(), dailyReport);

        Scene meeting = new Scene();
        meeting.setSceneId("scene-meeting");
        meeting.setName("会议记录");
        meeting.setDescription("自动记录会议内容并生成纪要");
        meeting.setType("automation");
        meeting.setStatus(Scene.SceneStatus.ACTIVE);
        meeting.setCapabilities(Arrays.asList("llm-chat", "document-generate"));
        scenes.put(meeting.getSceneId(), meeting);
    }

    @Override
    public List<Scene> findAll() {
        return new ArrayList<>(scenes.values());
    }

    @Override
    public Scene findById(String sceneId) {
        return scenes.get(sceneId);
    }

    @Override
    public Scene create(Scene scene) {
        if (scene.getSceneId() == null) {
            scene.setSceneId("scene-" + UUID.randomUUID().toString().substring(0, 8));
        }
        scene.setCreatedAt(new Date());
        scene.setUpdatedAt(new Date());
        scenes.put(scene.getSceneId(), scene);
        log.info("Created scene: {}", scene.getSceneId());
        return scene;
    }

    @Override
    public Scene update(Scene scene) {
        scene.setUpdatedAt(new Date());
        scenes.put(scene.getSceneId(), scene);
        log.info("Updated scene: {}", scene.getSceneId());
        return scene;
    }

    @Override
    public void delete(String sceneId) {
        scenes.remove(sceneId);
        log.info("Deleted scene: {}", sceneId);
    }

    @Override
    public Scene start(String sceneId) {
        Scene scene = scenes.get(sceneId);
        if (scene != null) {
            scene.setStatus(Scene.SceneStatus.ACTIVE);
            scene.setUpdatedAt(new Date());
            log.info("Started scene: {}", sceneId);
        }
        return scene;
    }

    @Override
    public Scene stop(String sceneId) {
        Scene scene = scenes.get(sceneId);
        if (scene != null) {
            scene.setStatus(Scene.SceneStatus.PAUSED);
            scene.setUpdatedAt(new Date());
            log.info("Stopped scene: {}", sceneId);
        }
        return scene;
    }

    @Override
    public boolean validate(String sceneId) {
        Scene scene = scenes.get(sceneId);
        if (scene == null) {
            return false;
        }
        log.info("Validated scene: {}", sceneId);
        return true;
    }
}
