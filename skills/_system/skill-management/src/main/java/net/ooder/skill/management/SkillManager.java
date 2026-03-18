package net.ooder.skill.management;

import net.ooder.skill.management.lifecycle.SkillLifecycleListener;
import net.ooder.skill.management.lifecycle.SkillLifecycleManager;
import net.ooder.skill.management.model.SkillContext;
import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillDefinition.SkillStatus;
import net.ooder.skill.management.model.SkillException;
import net.ooder.skill.management.model.SkillResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkillManager {
    private static final Logger logger = LoggerFactory.getLogger(SkillManager.class);

    private static Map<String, SkillManager> instances = new ConcurrentHashMap<>();
    
    private Map<String, SkillDefinition> skillMap;
    private Map<String, List<SkillDefinition>> categoryMap;
    private ExecutorService executorService;
    private SkillLifecycleManager lifecycleManager;

    private SkillManager() {
        this.skillMap = new ConcurrentHashMap<>();
        this.categoryMap = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(10);
        this.lifecycleManager = new SkillLifecycleManager();
        this.loadBuiltInSkills();
    }

    public static synchronized SkillManager getInstance() {
        return getInstance("default");
    }

    public static synchronized SkillManager getInstance(String name) {
        if (!instances.containsKey(name)) {
            instances.put(name, new SkillManager());
        }
        return instances.get(name);
    }

    public synchronized void registerSkill(SkillDefinition skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }

        String skillId = skill.getSkillId();
        if (skillId == null || skillId.isEmpty()) {
            throw new IllegalArgumentException("Skill ID cannot be null or empty");
        }

        String category = skill.getCategory() != null ? skill.getCategory() : "general";
        
        skillMap.put(skillId, skill);
        
        categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(skill);
        
        lifecycleManager.notifySkillDiscovered(skillId, skill);
        
        logger.info("Registered skill: {} in category: {}", skillId, category);
    }

    public synchronized void unregisterSkill(String skillId) {
        if (skillId == null || skillId.isEmpty()) {
            throw new IllegalArgumentException("Skill ID cannot be null or empty");
        }

        SkillDefinition skill = skillMap.remove(skillId);
        if (skill != null) {
            String category = skill.getCategory();
            List<SkillDefinition> skills = categoryMap.get(category);
            if (skills != null) {
                skills.remove(skill);
                if (skills.isEmpty()) {
                    categoryMap.remove(category);
                }
            }
            
            lifecycleManager.notifySkillUnloading(skillId);
            lifecycleManager.notifySkillUnloaded(skillId);
            
            logger.info("Unregistered skill: {}", skillId);
        }
    }

    public SkillDefinition getSkill(String skillId) {
        if (skillId == null || skillId.isEmpty()) {
            throw new IllegalArgumentException("Skill ID cannot be null or empty");
        }
        return skillMap.get(skillId);
    }

    public List<SkillDefinition> getAllSkills() {
        return new ArrayList<>(skillMap.values());
    }

    public List<SkillDefinition> getSkillsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(categoryMap.getOrDefault(category, Collections.emptyList()));
    }

    public List<SkillDefinition> findSkills(SkillCondition condition) {
        if (condition == null) {
            return getAllSkills();
        }

        List<SkillDefinition> result = new ArrayList<>();
        for (SkillDefinition skill : skillMap.values()) {
            if (condition.match(skill)) {
                result.add(skill);
            }
        }
        return result;
    }

    public SkillResult executeSkill(String skillId, SkillContext context) throws SkillException {
        SkillDefinition skill = getSkill(skillId);
        if (skill == null) {
            throw new SkillException(skillId, "Skill not found: " + skillId, 
                                     SkillException.ErrorCode.SKILL_NOT_FOUND);
        }

        if (!skill.isAvailable()) {
            throw new SkillException(skillId, "Skill is not available: " + skillId, 
                                     SkillException.ErrorCode.STATE_ERROR);
        }

        long startTime = System.currentTimeMillis();
        
        try {
            lifecycleManager.notifySkillRunning(skillId);
            
            SkillStatus oldStatus = skill.getStatus();
            skill.setStatus(SkillStatus.RUNNING);
            lifecycleManager.notifySkillStatusChanged(skillId, oldStatus, SkillStatus.RUNNING);
            
            SkillResult result = doExecuteSkill(skill, context);
            
            skill.incrementRunCount();
            
            SkillStatus newStatus = result.isSuccess() ? SkillStatus.IDLE : SkillStatus.ERROR;
            skill.setStatus(newStatus);
            lifecycleManager.notifySkillStatusChanged(skillId, SkillStatus.RUNNING, newStatus);
            
            if (result.isSuccess()) {
                lifecycleManager.notifySkillIdle(skillId);
            } else {
                lifecycleManager.notifySkillError(skillId, 
                    new SkillException(skillId, result.getMessage(), SkillException.ErrorCode.EXECUTION_EXCEPTION));
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTime(executionTime);
            result.setSkillId(skillId);
            
            return result;
            
        } catch (Exception e) {
            skill.setStatus(SkillStatus.ERROR);
            lifecycleManager.notifySkillError(skillId, e);
            
            throw new SkillException(skillId, "Skill execution failed: " + e.getMessage(), 
                                     SkillException.ErrorCode.EXECUTION_EXCEPTION, e);
        }
    }

    protected SkillResult doExecuteSkill(SkillDefinition skill, SkillContext context) {
        return SkillResult.success("Skill executed successfully: " + skill.getName());
    }

    public void executeSkillAsync(String skillId, SkillContext context, SkillCallback callback) {
        executorService.submit(() -> {
            try {
                SkillResult result = executeSkill(skillId, context);
                callback.onSuccess(result);
            } catch (SkillException e) {
                callback.onFailure(e);
            }
        });
    }

    public synchronized SkillDefinition addSkill(SkillDefinition skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }
        
        if (skill.getSkillId() == null || skill.getSkillId().isEmpty()) {
            skill.setSkillId("skill-" + System.currentTimeMillis());
        }
        
        if (skill.getId() == null || skill.getId().isEmpty()) {
            skill.setId(skill.getSkillId());
        }
        
        if (skill.getInstallTime() == null) {
            skill.setInstallTime(new Date());
        }
        
        if (skill.getStatus() == null) {
            skill.setStatus(SkillStatus.INSTALLED);
        }
        
        registerSkill(skill);
        return skill;
    }

    public synchronized SkillDefinition updateSkill(SkillDefinition skill) {
        if (skill == null || skill.getSkillId() == null) {
            throw new IllegalArgumentException("Skill or skill ID cannot be null");
        }
        
        if (!skillMap.containsKey(skill.getSkillId())) {
            throw new IllegalArgumentException("Skill not found: " + skill.getSkillId());
        }
        
        unregisterSkill(skill.getSkillId());
        registerSkill(skill);
        
        logger.info("Updated skill: {}", skill.getSkillId());
        return skill;
    }

    public synchronized boolean deleteSkill(String skillId) {
        if (skillId == null || skillId.isEmpty()) {
            throw new IllegalArgumentException("Skill ID cannot be null or empty");
        }
        
        if (!skillMap.containsKey(skillId)) {
            return false;
        }
        
        unregisterSkill(skillId);
        return true;
    }

    public synchronized boolean startSkill(String skillId) {
        SkillDefinition skill = getSkill(skillId);
        if (skill == null) {
            return false;
        }
        
        if (!skill.canRun()) {
            return false;
        }
        
        SkillStatus oldStatus = skill.getStatus();
        skill.setStatus(SkillStatus.STARTING);
        lifecycleManager.notifySkillStarting(skillId);
        
        try {
            skill.setStatus(SkillStatus.RUNNING);
            lifecycleManager.notifySkillStatusChanged(skillId, oldStatus, SkillStatus.RUNNING);
            lifecycleManager.notifySkillStarted(skillId);
            return true;
        } catch (Exception e) {
            skill.setStatus(SkillStatus.ERROR);
            lifecycleManager.notifySkillStartFailed(skillId, e);
            return false;
        }
    }

    public synchronized boolean stopSkill(String skillId) {
        SkillDefinition skill = getSkill(skillId);
        if (skill == null) {
            return false;
        }
        
        if (!skill.isRunning()) {
            return true;
        }
        
        SkillStatus oldStatus = skill.getStatus();
        skill.setStatus(SkillStatus.STOPPING);
        lifecycleManager.notifySkillStopping(skillId);
        
        try {
            skill.setStatus(SkillStatus.STOPPED);
            lifecycleManager.notifySkillStatusChanged(skillId, oldStatus, SkillStatus.STOPPED);
            lifecycleManager.notifySkillStopped(skillId);
            return true;
        } catch (Exception e) {
            skill.setStatus(SkillStatus.ERROR);
            lifecycleManager.notifySkillError(skillId, e);
            return false;
        }
    }

    public SkillLifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }

    public void addLifecycleListener(SkillLifecycleListener listener) {
        lifecycleManager.addListener(listener);
    }

    public void removeLifecycleListener(SkillLifecycleListener listener) {
        lifecycleManager.removeListener(listener);
    }

    private void loadBuiltInSkills() {
        String[][] defaultSkills = {
            {"skill-weather", "天气查询技能", "查询实时天气信息", "utilities"},
            {"skill-stock", "股票查询技能", "查询股票实时行情", "finance"},
            {"skill-translate", "翻译技能", "多语言翻译服务", "utilities"},
            {"skill-calculator", "计算器技能", "数学计算工具", "utilities"},
            {"skill-notes", "笔记技能", "创建和管理笔记", "productivity"},
            {"skill-reminder", "提醒技能", "设置和管理提醒", "productivity"},
            {"skill-calendar", "日历技能", "管理日程安排", "productivity"},
            {"skill-email", "邮件技能", "发送和管理邮件", "communication"},
            {"skill-chat", "聊天技能", "实时聊天功能", "communication"},
            {"skill-image", "图像处理技能", "图像处理和编辑", "media"}
        };
        
        for (String[] skillData : defaultSkills) {
            SkillDefinition skill = new SkillDefinition();
            skill.setSkillId(skillData[0]);
            skill.setId(skillData[0]);
            skill.setName(skillData[1]);
            skill.setDescription(skillData[2]);
            skill.setCategory(skillData[3]);
            skill.setStatus(SkillStatus.INSTALLED);
            skill.setSource(SkillDefinition.SkillSource.LOCAL);
            skill.setInstallTime(new Date());
            registerSkill(skill);
        }
        
        logger.info("Loaded {} built-in skills", skillMap.size());
    }

    public void shutdown() {
        executorService.shutdown();
        
        for (String skillId : skillMap.keySet()) {
            stopSkill(skillId);
        }
        
        skillMap.clear();
        categoryMap.clear();
        
        logger.info("SkillManager shutdown completed");
    }

    public interface SkillCondition {
        boolean match(SkillDefinition skill);
    }

    public interface SkillCallback {
        void onSuccess(SkillResult result);
        void onFailure(SkillException exception);
    }
}
