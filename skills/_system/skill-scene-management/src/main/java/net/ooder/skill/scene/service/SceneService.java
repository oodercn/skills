package net.ooder.skill.scene.service;

import net.ooder.skill.scene.model.Scene;

import java.util.List;

public interface SceneService {

    List<Scene> findAll();

    Scene findById(String sceneId);

    Scene create(Scene scene);

    Scene update(Scene scene);

    void delete(String sceneId);

    Scene start(String sceneId);

    Scene stop(String sceneId);

    boolean validate(String sceneId);
}
