package net.ooder.skill.install.scene.controller;

import net.ooder.skill.install.scene.dto.InstallSkillDTO;
import net.ooder.skill.install.scene.dto.InstallStepDTO;
import net.ooder.skill.install.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/api/v1/install-scene")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class InstallSceneController {

    private static final Logger log = LoggerFactory.getLogger(InstallSceneController.class);

    private final ConcurrentMap<String, InstallProgress> progressMap = new ConcurrentHashMap<>();

    @GetMapping("/steps")
    public ResultModel<List<InstallStepDTO>> getSteps(@RequestParam String profile) {
        log.info("[InstallSceneController] Get steps for profile: {}", profile);

        List<InstallStepDTO> steps = new ArrayList<>();

        InstallStepDTO step1 = new InstallStepDTO();
        step1.setStep(1);
        step1.setName("Initialize");
        step1.setDescription("Initialize installation environment");
        step1.setStatus("pending");
        step1.setProgress(0);
        steps.add(step1);

        InstallStepDTO step2 = new InstallStepDTO();
        step2.setStep(2);
        step2.setName("Load Skills");
        step2.setDescription("Load required skills");
        step2.setStatus("pending");
        step2.setProgress(0);
        steps.add(step2);

        InstallStepDTO step3 = new InstallStepDTO();
        step3.setStep(3);
        step3.setName("Configure");
        step3.setDescription("Configure scene settings");
        step3.setStatus("pending");
        step3.setProgress(0);
        steps.add(step3);

        InstallStepDTO step4 = new InstallStepDTO();
        step4.setStep(4);
        step4.setName("Complete");
        step4.setDescription("Complete installation");
        step4.setStatus("pending");
        step4.setProgress(0);
        steps.add(step4);

        return ResultModel.success(steps);
    }

    @GetMapping("/skills")
    public ResultModel<List<InstallSkillDTO>> getSkills(@RequestParam String profile) {
        log.info("[InstallSceneController] Get skills for profile: {}", profile);

        List<InstallSkillDTO> skills = new ArrayList<>();

        InstallSkillDTO skill1 = new InstallSkillDTO();
        skill1.setSkillId("skill-llm-config");
        skill1.setName("LLM Config");
        skill1.setVersion("1.0.0");
        skill1.setDescription("LLM configuration management");
        skill1.setSelected(true);
        skills.add(skill1);

        InstallSkillDTO skill2 = new InstallSkillDTO();
        skill2.setSkillId("skill-context");
        skill2.setName("Context Management");
        skill2.setVersion("1.0.0");
        skill2.setDescription("Context management for scenes");
        skill2.setSelected(true);
        skills.add(skill2);

        return ResultModel.success(skills);
    }

    @GetMapping("/start")
    public SseEmitter startInstall(@RequestParam String profile) {
        log.info("[InstallSceneController] Start installation for profile: {}", profile);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        InstallProgress progress = new InstallProgress();
        progress.setProfile(profile);
        progress.setCurrentStep(0);
        progressMap.put(profile, progress);

        new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    Thread.sleep(1000);
                    progress.setCurrentStep(i);
                    emitter.send(SseEmitter.event()
                            .name("progress")
                            .data("{\"step\":" + i + ",\"status\":\"in_progress\"}"));
                }
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("{\"status\":\"completed\"}"));
                emitter.complete();
            } catch (Exception e) {
                log.error("[InstallSceneController] SSE error", e);
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    private static class InstallProgress {
        private String profile;
        private int currentStep;

        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
        public int getCurrentStep() { return currentStep; }
        public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
    }
}