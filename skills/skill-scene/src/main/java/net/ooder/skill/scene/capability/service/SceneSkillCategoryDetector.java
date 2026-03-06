package net.ooder.skill.scene.capability.service;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.MainFirstConfig;
import net.ooder.skill.scene.capability.model.SceneSkillCategory;
import net.ooder.skill.scene.capability.model.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SceneSkillCategoryDetector {

    private static final Logger log = LoggerFactory.getLogger(SceneSkillCategoryDetector.class);

    public SceneSkillCategory detectCategory(Capability capability) {
        if (capability == null) {
            return SceneSkillCategory.INVALID;
        }

        if (capability.getType() != CapabilityType.SCENE) {
            return SceneSkillCategory.NOT_SCENE_SKILL;
        }

        List<String> capabilities = capability.getCapabilities();
        if (capabilities == null || capabilities.isEmpty()) {
            return SceneSkillCategory.NOT_SCENE_SKILL;
        }

        boolean hasMainFirst = capability.isMainFirst() && capability.getMainFirstConfig() != null;

        boolean hasBusinessSemantics = hasBusinessSemantics(capability);

        if (hasMainFirst && hasBusinessSemantics) {
            log.debug("[detectCategory] {} -> ABS (hasMainFirst={}, hasBusinessSemantics={})", 
                capability.getCapabilityId(), hasMainFirst, hasBusinessSemantics);
            return SceneSkillCategory.ABS;
        } else if (hasMainFirst && !hasBusinessSemantics) {
            log.debug("[detectCategory] {} -> ASS (hasMainFirst={}, hasBusinessSemantics={})", 
                capability.getCapabilityId(), hasMainFirst, hasBusinessSemantics);
            return SceneSkillCategory.ASS;
        } else if (!hasMainFirst && hasBusinessSemantics) {
            log.debug("[detectCategory] {} -> TBS (hasMainFirst={}, hasBusinessSemantics={})", 
                capability.getCapabilityId(), hasMainFirst, hasBusinessSemantics);
            return SceneSkillCategory.TBS;
        } else {
            log.debug("[detectCategory] {} -> INVALID (hasMainFirst={}, hasBusinessSemantics={})", 
                capability.getCapabilityId(), hasMainFirst, hasBusinessSemantics);
            return SceneSkillCategory.INVALID;
        }
    }

    public SceneSkillCategory detectCategory(Map<String, Object> skillData) {
        if (skillData == null) {
            return SceneSkillCategory.INVALID;
        }

        Object typeObj = skillData.get("type");
        String type = typeObj != null ? String.valueOf(typeObj) : null;
        
        if (type != null && !"scene-skill".equals(type) && !"SCENE".equals(type) && !"scene".equals(type)) {
            return SceneSkillCategory.NOT_SCENE_SKILL;
        }

        Object capsObj = skillData.get("capabilities");
        if (capsObj == null) {
            capsObj = skillData.get("sceneCapabilities");
        }
        if (capsObj == null) {
            capsObj = skillData.get("requiredCapabilities");
        }
        
        Object categoryObj = skillData.get("category");
        String legacyCategory = categoryObj != null ? String.valueOf(categoryObj) : null;
        if ("COLLABORATION".equals(legacyCategory) || "BUSINESS".equals(legacyCategory)) {
            boolean hasMainFirst = checkMainFirst(skillData);
            return hasMainFirst ? SceneSkillCategory.ABS : SceneSkillCategory.TBS;
        }
        if ("SYSTEM".equals(legacyCategory) || "TECHNICAL".equals(legacyCategory)) {
            return SceneSkillCategory.ASS;
        }
        
        if (capsObj == null || !(capsObj instanceof List)) {
            Object sceneId = skillData.get("sceneId");
            if (sceneId != null) {
                return SceneSkillCategory.TBS;
            }
            return SceneSkillCategory.NOT_SCENE_SKILL;
        }
        
        @SuppressWarnings("unchecked")
        List<Object> caps = (List<Object>) capsObj;
        if (caps.isEmpty()) {
            Object sceneId = skillData.get("sceneId");
            if (sceneId != null) {
                return SceneSkillCategory.TBS;
            }
            return SceneSkillCategory.NOT_SCENE_SKILL;
        }

        boolean hasMainFirst = checkMainFirst(skillData);

        boolean hasBusinessSemantics = checkBusinessSemantics(skillData);

        if (hasMainFirst && hasBusinessSemantics) {
            return SceneSkillCategory.ABS;
        } else if (hasMainFirst && !hasBusinessSemantics) {
            return SceneSkillCategory.ASS;
        } else if (!hasMainFirst && hasBusinessSemantics) {
            return SceneSkillCategory.TBS;
        } else {
            Object sceneId = skillData.get("sceneId");
            if (sceneId != null) {
                return SceneSkillCategory.TBS;
            }
            return SceneSkillCategory.INVALID;
        }
    }

    private boolean hasBusinessSemantics(Capability capability) {
        boolean hasDriverConditions = capability.getDriverConditions() != null 
            && !capability.getDriverConditions().isEmpty();
        
        boolean hasParticipants = capability.getParticipants() != null 
            && !capability.getParticipants().isEmpty();
        
        return hasDriverConditions && hasParticipants;
    }

    private boolean checkMainFirst(Map<String, Object> skillData) {
        Object mainFirstObj = skillData.get("mainFirst");
        if (mainFirstObj instanceof Boolean) {
            return (Boolean) mainFirstObj;
        }
        if (mainFirstObj instanceof String) {
            return Boolean.parseBoolean((String) mainFirstObj);
        }

        Object specObj = skillData.get("spec");
        if (specObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> spec = (Map<String, Object>) specObj;
            Object specMainFirst = spec.get("mainFirst");
            if (specMainFirst instanceof Boolean) {
                return (Boolean) specMainFirst;
            }
            if (specMainFirst instanceof String) {
                return Boolean.parseBoolean((String) specMainFirst);
            }

            Object sceneCapsObj = spec.get("sceneCapabilities");
            if (sceneCapsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> sceneCaps = (List<Map<String, Object>>) sceneCapsObj;
                if (!sceneCaps.isEmpty()) {
                    Map<String, Object> firstCap = sceneCaps.get(0);
                    Object capMainFirst = firstCap.get("mainFirst");
                    if (capMainFirst instanceof Boolean) {
                        return (Boolean) capMainFirst;
                    }
                    if (capMainFirst instanceof String) {
                        return Boolean.parseBoolean((String) capMainFirst);
                    }
                    Object mainFirstConfig = firstCap.get("mainFirstConfig");
                    return mainFirstConfig != null;
                }
            }
        }

        return false;
    }

    private boolean checkBusinessSemantics(Map<String, Object> skillData) {
        boolean hasDriverConditions = false;
        boolean hasParticipants = false;

        Object driverConditionsObj = skillData.get("driverConditions");
        if (driverConditionsObj instanceof List) {
            hasDriverConditions = !((List<?>) driverConditionsObj).isEmpty();
        }
        
        if (!hasDriverConditions) {
            Object driverCapabilitiesObj = skillData.get("driverCapabilities");
            if (driverCapabilitiesObj instanceof List) {
                hasDriverConditions = !((List<?>) driverCapabilitiesObj).isEmpty();
                log.debug("[checkBusinessSemantics] Found driverCapabilities: {}, hasDriverConditions={}", 
                    driverCapabilitiesObj, hasDriverConditions);
            }
        }
        
        if (!hasDriverConditions) {
            Object requiredCapabilitiesObj = skillData.get("requiredCapabilities");
            if (requiredCapabilitiesObj instanceof List) {
                hasDriverConditions = !((List<?>) requiredCapabilitiesObj).isEmpty();
                log.debug("[checkBusinessSemantics] Found requiredCapabilities: {}, hasDriverConditions={}", 
                    requiredCapabilitiesObj, hasDriverConditions);
            }
        }

        Object participantsObj = skillData.get("participants");
        if (participantsObj instanceof List) {
            hasParticipants = !((List<?>) participantsObj).isEmpty();
        }
        
        if (!hasParticipants) {
            Object maxMembersObj = skillData.get("maxMembers");
            if (maxMembersObj != null) {
                hasParticipants = true;
                log.debug("[checkBusinessSemantics] Found maxMembers: {}, hasParticipants=true", maxMembersObj);
            }
        }

        Object specObj = skillData.get("spec");
        if (specObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> spec = (Map<String, Object>) specObj;
            
            if (!hasDriverConditions) {
                Object specDriverConditions = spec.get("driverConditions");
                if (specDriverConditions instanceof List) {
                    hasDriverConditions = !((List<?>) specDriverConditions).isEmpty();
                }
            }

            if (!hasParticipants) {
                Object specParticipants = spec.get("participants");
                if (specParticipants instanceof List) {
                    hasParticipants = !((List<?>) specParticipants).isEmpty();
                }
            }

            if (!hasDriverConditions || !hasParticipants) {
                Object sceneCapsObj = spec.get("sceneCapabilities");
                if (sceneCapsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> sceneCaps = (List<Map<String, Object>>) sceneCapsObj;
                    if (!sceneCaps.isEmpty()) {
                        Map<String, Object> firstCap = sceneCaps.get(0);
                        
                        if (!hasDriverConditions) {
                            Object capDriverConditions = firstCap.get("driverConditions");
                            if (capDriverConditions instanceof List) {
                                hasDriverConditions = !((List<?>) capDriverConditions).isEmpty();
                            }
                        }
                        
                        if (!hasParticipants) {
                            Object capParticipants = firstCap.get("participants");
                            if (capParticipants instanceof List) {
                                hasParticipants = !((List<?>) capParticipants).isEmpty();
                            }
                        }
                    }
                }
            }
        }

        boolean result = hasDriverConditions && hasParticipants;
        log.debug("[checkBusinessSemantics] Result: hasDriverConditions={}, hasParticipants={}, result={}", 
            hasDriverConditions, hasParticipants, result);
        return result;
    }

    public String determineVisibility(Capability capability) {
        SceneSkillCategory category = detectCategory(capability);
        if (category == SceneSkillCategory.ASS) {
            return "internal";
        }
        return "public";
    }

    public boolean isValidSceneSkill(Capability capability) {
        SceneSkillCategory category = detectCategory(capability);
        return category == SceneSkillCategory.ABS 
            || category == SceneSkillCategory.ASS 
            || category == SceneSkillCategory.TBS;
    }
}
