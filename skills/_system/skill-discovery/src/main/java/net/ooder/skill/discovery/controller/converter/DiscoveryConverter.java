package net.ooder.skill.discovery.controller.converter;

import net.ooder.skill.discovery.dto.discovery.CapabilityDTO;
import net.ooder.skill.discovery.model.CapabilityCategory;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillForm;
import net.ooder.skills.api.SkillPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 发现服务转换器
 * 
 * 注意：SDK 3.0.1+ 已经提供自动推断功能：
 * - CategoryResolver: 自动推断业务分类
 * - SkillFormResolver: 自动推断技能形态
 * - DefaultConverters: 标准 DTO 转换器
 * 
 * OS 层只需简单调用 SDK 方法即可，无需再维护推断逻辑
 */
public final class DiscoveryConverter {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryConverter.class);

    private DiscoveryConverter() {
    }

    private static SkillForm mapToSkillForm(String formValue) {
        if (formValue == null || formValue.trim().isEmpty()) {
            return null;
        }
        
        String normalized = formValue.toUpperCase().trim();
        
        try {
            return SkillForm.valueOf(normalized);
        } catch (IllegalArgumentException e) {
        }
        
        switch (normalized) {
            case "SCENE-SKILL":
            case "SCENE_SKILL":
            case "SCENE-SK":
            case "SCENE_SK":
                return SkillForm.SCENE;
            case "PROVIDER-SKILL":
            case "PROVIDER_SKILL":
            case "PROVIDER-SK":
            case "PROVIDER_SK":
            case "SERVICE-SKILL":
            case "SERVICE_SKILL":
                return SkillForm.PROVIDER;
            case "DRIVER-SKILL":
            case "DRIVER_SKILL":
            case "DRIVER-SK":
            case "DRIVER_SK":
            case "ENTERPRISE-SKILL":
            case "ENTERPRISE_SKILL":
            case "ADAPTER-SKILL":
            case "ADAPTER_SKILL":
            case "CONNECTOR-SKILL":
            case "CONNECTOR_SKILL":
                return SkillForm.DRIVER;
            default:
                log.warn("Unknown skillForm value: {}, defaulting to PROVIDER", formValue);
                return SkillForm.PROVIDER;
        }
    }

    /**
     * 将 SkillPackage 转换为 CapabilityDTO
     * 
     * SDK 3.0.1+ 自动处理：
     * - category 字段推断（通过 CategoryResolver）
     * - skillForm 字段推断（通过 SkillFormResolver）
     * - 标准字段映射
     */
    public static CapabilityDTO toCapabilityDTO(SkillPackage pkg) {
        if (pkg == null) {
            return null;
        }

        // 调试日志：检查 SDK 返回的原始数据
        String skillId = pkg.getSkillId();
        if (skillId != null && skillId.contains("approval")) {
            log.info("[DEBUG] skill-approval-form found, checking raw data:");
            log.info("[DEBUG]   getSkillId() = {}", skillId);
            log.info("[DEBUG]   getCategory() = {}", pkg.getCategory());
            log.info("[DEBUG]   getForm() = {}", pkg.getForm());
            log.info("[DEBUG]   getMetadata() = {}", pkg.getMetadata());
            if (pkg.getMetadata() != null) {
                log.info("[DEBUG]   metadata.getCategory() = {}", pkg.getMetadata().get("category"));
                log.info("[DEBUG]   metadata.getForm() = {}", pkg.getMetadata().get("form"));
            }
        }

        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(skillId);
        dto.setName(pkg.getName());
        dto.setVersion(pkg.getVersion());
        dto.setSkillId(skillId);
        dto.setSource(pkg.getSource());
        
        // SDK 3.0.1+ 自动推断 category，如果 metadata.category 为空则从 skillId 推断
        String category = pkg.getCategory();
        dto.setCategory(category);
        
        // 映射到标准分类代码
        if (category != null) {
            CapabilityCategory mappedCategory = CapabilityCategory.fromCode(category);
            dto.setBusinessCategory(mappedCategory.getCode());
            dto.setCapabilityCategory(mappedCategory.getCode());
        }

        // SDK 3.0.1+ 自动推断 skillForm，如果 spec.skillForm 为空则从 skillId 推断
        // 添加兼容性映射：支持 scene-skill, provider-skill, driver-skill, enterprise-skill 等旧格式
        SkillForm form = pkg.getForm();
        String skillForm;
        if (form != null) {
            skillForm = form.name();
        } else {
            // 尝试从 metadata.type 获取旧格式的类型值
            Map<String, Object> metadata = pkg.getMetadata();
            if (metadata != null) {
                Object typeValue = metadata.get("type");
                if (typeValue != null) {
                    SkillForm mappedForm = mapToSkillForm(typeValue.toString());
                    skillForm = mappedForm != null ? mappedForm.name() : null;
                    if (skillForm != null) {
                        log.debug("Mapped skillForm from metadata.type: {} -> {}", typeValue, skillForm);
                    }
                } else {
                    skillForm = null;
                }
            } else {
                skillForm = null;
            }
        }
        dto.setSkillForm(skillForm);
        
        if (skillId != null && skillId.contains("approval")) {
            log.info("[DEBUG] skill-approval-form final values: category={}, skillForm={}", category, skillForm);
        }
        
        // 根据 skillForm 设置 sceneCapability 和 type
        boolean isScene = "SCENE".equals(skillForm);
        dto.setSceneCapability(isScene);
        dto.setType(isScene ? "SCENE" : "SKILL");

        convertTags(pkg.getTags(), dto);
        convertDependencies(pkg.getDependencies(), dto);
        convertMetadata(pkg.getMetadata(), dto);
        convertCapabilities(pkg.getCapabilities(), dto);

        return dto;
    }

    /**
     * 将 InstalledSkill 转换为 CapabilityDTO
     * 
     * SDK 3.0.1+ 自动处理字段推断
     */
    public static CapabilityDTO toCapabilityDTO(InstalledSkill skill) {
        if (skill == null) {
            return null;
        }

        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(skill.getSkillId());
        dto.setName(skill.getName());
        dto.setVersion(skill.getVersion());
        dto.setSkillId(skill.getSkillId());
        dto.setSceneType(skill.getSceneId());
        dto.setStatus(skill.getStatus());
        dto.setDependencies(skill.getDependencies());
        dto.setInstalled(true);

        // SDK 3.0.1+ 自动推断
        String category = skill.getCategory();
        String skillForm = skill.getForm() != null ? skill.getForm().name() : null;
        
        dto.setCategory(category);
        dto.setCapabilityCategory(category);
        dto.setBusinessCategory(category);
        dto.setSkillForm(skillForm);
        
        boolean isScene = "SCENE".equals(skillForm);
        dto.setSceneCapability(isScene);
        dto.setType(isScene ? "SCENE" : "SKILL");

        return dto;
    }

    public static Map<String, Object> toMap(SkillPackage pkg) {
        if (pkg == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pkg.getSkillId());
        map.put("name", pkg.getName());
        map.put("version", pkg.getVersion());
        map.put("skillId", pkg.getSkillId());
        map.put("source", pkg.getSource());
        map.put("downloadUrl", pkg.getDownloadUrl());
        map.put("category", pkg.getCategory());
        map.put("skillForm", pkg.getForm() != null ? pkg.getForm().name() : null);
        map.put("tags", pkg.getTags());
        map.put("dependencies", pkg.getDependencies());

        Map<String, Object> metadata = pkg.getMetadata();
        if (metadata != null) {
            map.put("description", metadata.get("description"));
        }

        List<?> caps = pkg.getCapabilities();
        if (caps != null) {
            List<String> capNames = new ArrayList<>();
            for (Object cap : caps) {
                if (cap != null) {
                    capNames.add(cap.toString());
                }
            }
            map.put("capabilities", capNames);
        }

        return map;
    }

    public static Map<String, Object> toMap(InstalledSkill skill) {
        if (skill == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", skill.getSkillId());
        map.put("name", skill.getName());
        map.put("version", skill.getVersion());
        map.put("skillId", skill.getSkillId());
        map.put("sceneId", skill.getSceneId());
        map.put("installPath", skill.getInstallPath());
        map.put("status", skill.getStatus());
        map.put("installTime", skill.getInstallTime());
        map.put("dependencies", skill.getDependencies());
        map.put("category", skill.getCategory());
        map.put("skillForm", skill.getSkillForm());
        map.put("installed", true);

        return map;
    }

    public static Map<String, Object> toMap(CapabilityDTO cap) {
        if (cap == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", cap.getId());
        map.put("name", cap.getName());
        map.put("description", cap.getDescription());
        map.put("version", cap.getVersion());
        map.put("source", cap.getSource());
        map.put("status", cap.getStatus());
        map.put("type", cap.getType());
        map.put("sceneCapability", cap.isSceneCapability());
        map.put("skillForm", cap.getSkillForm());
        map.put("sceneType", cap.getSceneType());
        map.put("category", cap.getCategory());
        map.put("capabilityCategory", cap.getCapabilityCategory());
        map.put("businessCategory", cap.getBusinessCategory());
        map.put("visibility", cap.getVisibility());
        map.put("installed", cap.isInstalled());
        map.put("capabilities", cap.getCapabilities());
        map.put("dependencies", cap.getDependencies());
        map.put("tags", cap.getTags());
        map.put("driverConditions", cap.getDriverConditions());
        map.put("participants", cap.getParticipants());

        return map;
    }

    public static List<Map<String, Object>> toMapList(List<CapabilityDTO> caps) {
        if (caps == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (CapabilityDTO cap : caps) {
            result.add(toMap(cap));
        }
        return result;
    }

    private static void convertTags(List<?> tags, CapabilityDTO dto) {
        if (tags == null) {
            return;
        }
        List<String> tagStrings = new ArrayList<>();
        for (Object tag : tags) {
            if (tag != null) {
                tagStrings.add(tag.toString());
            }
        }
        dto.setTags(tagStrings);
    }

    private static void convertDependencies(List<?> deps, CapabilityDTO dto) {
        if (deps == null) {
            return;
        }
        List<String> depStrings = new ArrayList<>();
        for (Object dep : deps) {
            if (dep != null) {
                depStrings.add(dep.toString());
            }
        }
        dto.setDependencies(depStrings);
    }

    private static void convertMetadata(Map<String, Object> metadata, CapabilityDTO dto) {
        if (metadata != null) {
            dto.setDescription((String) metadata.get("description"));

            Object sceneTypeObj = metadata.get("sceneType");
            if (sceneTypeObj != null) {
                dto.setSceneType(String.valueOf(sceneTypeObj));
            }

            Object visibilityObj = metadata.get("visibility");
            if (visibilityObj != null) {
                dto.setVisibility(String.valueOf(visibilityObj));
            }

            Object rolesObj = metadata.get("roles");
            if (rolesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> roles = (List<Map<String, Object>>) rolesObj;
                dto.setParticipants(roles);
            }

            Object driverConditionsObj = metadata.get("driverConditions");
            if (driverConditionsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> driverConditions = (List<Map<String, Object>>) driverConditionsObj;
                dto.setDriverConditions(driverConditions);
            }
        }
    }

    private static void convertCapabilities(List<?> caps, CapabilityDTO dto) {
        if (caps == null) {
            return;
        }
        List<String> capNames = new ArrayList<>();
        for (Object cap : caps) {
            if (cap != null) {
                if (cap instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> capMap = (Map<String, Object>) cap;
                    Object name = capMap.get("name");
                    if (name != null) {
                        capNames.add(name.toString());
                    } else {
                        Object id = capMap.get("id");
                        if (id != null) {
                            capNames.add(id.toString());
                        }
                    }
                } else {
                    capNames.add(cap.toString());
                }
            }
        }
        dto.setCapabilities(capNames);
    }
}
