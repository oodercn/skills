package net.ooder.mvp.controller;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.model.SkillPackage;
import net.ooder.skill.hotplug.model.PluginInstallResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
public class SetupController {

    @Autowired
    private PluginManager pluginManager;

    @GetMapping("/setup")
    public String setup() {
        return "redirect:/setup/index.html";
    }

    @GetMapping("/api/v1/setup/status")
    @ResponseBody
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("installed", isInstalled());
        result.put("data", data);
        return result;
    }

    @PostMapping("/api/v1/setup/admin")
    @ResponseBody
    public Map<String, Object> createAdmin(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || username.isEmpty()) {
            result.put("status", "error");
            result.put("message", "用户名不能为空");
            return result;
        }
        
        if (password == null || password.length() < 6) {
            result.put("status", "error");
            result.put("message", "密码长度不能少于6位");
            return result;
        }
        
        try {
            saveAdminUser(username, password);
            markAsInstalled();
            result.put("status", "success");
            result.put("message", "管理员账户创建成功");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "创建失败: " + e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/api/v1/plugin/install")
    @ResponseBody
    public Map<String, Object> installSkill(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        String skillId = request.get("skillId");
        
        if (skillId == null || skillId.isEmpty()) {
            result.put("status", "error");
            result.put("message", "skillId 不能为空");
            return result;
        }
        
        try {
            boolean success = doInstallSkill(skillId);
            if (success) {
                result.put("status", "success");
                result.put("message", skillId + " 安装成功");
            } else {
                result.put("status", "error");
                result.put("message", skillId + " 安装失败");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "安装失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/api/v1/plugin/status")
    @ResponseBody
    public Map<String, Object> getPluginStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", pluginManager.getInstalledSkills());
        return result;
    }

    @GetMapping("/api/v1/plugin/loaded")
    @ResponseBody
    public Map<String, Object> getLoadedPlugins() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", pluginManager.getInstalledSkills());
        return result;
    }

    @PostMapping("/api/v1/setup/reinstall")
    @ResponseBody
    public Map<String, Object> reinstall() {
        Map<String, Object> result = new HashMap<>();
        try {
            File markerFile = new File("data/.installed");
            if (markerFile.exists()) {
                markerFile.delete();
            }
            File registryFile = new File("data/installed-skills/registry.properties");
            if (registryFile.exists()) {
                registryFile.delete();
            }
            result.put("status", "success");
            result.put("message", "Ready for reinstallation");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed: " + e.getMessage());
        }
        return result;
    }

    private boolean isInstalled() {
        File markerFile = new File("data/.installed");
        return markerFile.exists();
    }

    private void markAsInstalled() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File markerFile = new File("data/.installed");
            markerFile.createNewFile();
            java.io.FileWriter writer = new java.io.FileWriter(markerFile);
            writer.write("installed=" + System.currentTimeMillis());
            writer.close();
            
            registerSkillCapability();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void registerSkillCapability() {
        try {
            File registryDir = new File("data/installed-skills");
            if (!registryDir.exists()) {
                registryDir.mkdirs();
            }
            
            File registryFile = new File("data/installed-skills/registry.properties");
            Properties props = new Properties();
            
            if (registryFile.exists()) {
                try (FileInputStream fis = new FileInputStream(registryFile)) {
                    props.load(fis);
                }
            }
            
            if (!props.containsKey("skill-capability.id")) {
                String capabilityPath = findSkillPath("skill-capability");
                if (capabilityPath != null) {
                    props.setProperty("skill-capability.id", "skill-capability");
                    props.setProperty("skill-capability.path", capabilityPath);
                    props.setProperty("skill-capability.installedAt", String.valueOf(System.currentTimeMillis()));
                    
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(registryFile)) {
                        props.store(fos, "Installed Skills Registry");
                    }
                    System.out.println("[SetupController] Registered skill-capability in registry");
                }
            }
        } catch (Exception e) {
            System.err.println("[SetupController] Failed to register skill-capability: " + e.getMessage());
        }
    }

    private void saveAdminUser(String username, String password) {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File userFile = new File("data/admin.properties");
            Properties props = new Properties();
            props.setProperty("admin.username", username);
            props.setProperty("admin.password", password);
            props.setProperty("admin.role", "admin");
            props.store(new java.io.FileOutputStream(userFile), "Admin User");
        } catch (Exception e) {
            throw new RuntimeException("保存管理员信息失败", e);
        }
    }

    private boolean doInstallSkill(String skillId) {
        System.out.println("========================================");
        System.out.println("[SetupController] Installing skill: " + skillId);
        System.out.println("========================================");
        
        File jarFile = findSkillJar(skillId);
        if (jarFile != null && jarFile.exists()) {
            System.out.println("[SetupController] Found skill JAR: " + jarFile.getAbsolutePath());
            return installSkillFromJar(jarFile, skillId);
        }
        
        String skillPath = findSkillPath(skillId);
        if (skillPath == null) {
            System.err.println("[SetupController] Skill not found: " + skillId);
            return false;
        }
        
        System.out.println("[SetupController] Found skill at: " + skillPath);
        
        File packagedJar = packageSkill(skillPath, skillId);
        if (packagedJar != null && packagedJar.exists()) {
            System.out.println("[SetupController] Skill packaged to JAR: " + packagedJar.getAbsolutePath());
            return installSkillFromJar(packagedJar, skillId);
        }
        
        System.err.println("[SetupController] Failed to package skill: " + skillId);
        return false;
    }
    
    private File findSkillJar(String skillId) {
        String[] jarPaths = {
            "../skills/_system/" + skillId + "/target/" + skillId + "-2.3.1.jar",
            "../skills/" + skillId + "/target/" + skillId + "-2.3.1.jar",
            "skills/_system/" + skillId + "/target/" + skillId + "-2.3.1.jar",
            "skills/" + skillId + "/target/" + skillId + "-2.3.1.jar"
        };
        
        for (String jarPath : jarPaths) {
            File jarFile = new File(jarPath);
            if (jarFile.exists()) {
                System.out.println("[SetupController] Found skill JAR: " + jarFile.getAbsolutePath());
                return jarFile;
            }
        }
        
        File mavenJar = new File(System.getProperty("user.home") + "/.m2/repository/net/ooder/" + skillId + "/2.3.1/" + skillId + "-2.3.1.jar");
        if (mavenJar.exists()) {
            System.out.println("[SetupController] Found skill JAR in Maven repo: " + mavenJar.getAbsolutePath());
            return mavenJar;
        }
        
        return null;
    }
    
    private File packageSkill(String skillPath, String skillId) {
        try {
            System.out.println("[SetupController] Packaging skill: " + skillId);
            
            String mvnCmd = findMavenCommand();
            if (mvnCmd == null) {
                System.err.println("[SetupController] Maven not found, skipping package step");
                return null;
            }
            
            ProcessBuilder pb = new ProcessBuilder(mvnCmd, "package", "-DskipTests", "-q");
            pb.directory(new File(skillPath));
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                System.err.println("[SetupController] Maven package failed with exit code: " + exitCode);
                return null;
            }
            
            File jarFile = new File(skillPath + "/target/" + skillId + "-2.3.1.jar");
            if (jarFile.exists()) {
                return jarFile;
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("[SetupController] Failed to package skill: " + e.getMessage());
            return null;
        }
    }
    
    private String findMavenCommand() {
        String[] possiblePaths = {
            "mvn",
            "mvn.cmd",
            System.getenv("MAVEN_HOME") != null ? System.getenv("MAVEN_HOME") + "/bin/mvn.cmd" : null,
            "D:/maven/apache-maven-3.6.0-bin/bin/mvn.cmd"
        };
        
        for (String path : possiblePaths) {
            if (path == null) continue;
            try {
                ProcessBuilder pb = new ProcessBuilder(path, "-version");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("[SetupController] Found Maven at: " + path);
                    return path;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
    
    private boolean installSkillFromJar(File jarFile, String skillId) {
        try {
            System.out.println("[SetupController] Installing skill from JAR: " + jarFile.getAbsolutePath());
            
            File pluginsDir = new File("plugins");
            if (!pluginsDir.exists()) {
                pluginsDir.mkdirs();
                System.out.println("[SetupController] Created plugins directory: " + pluginsDir.getAbsolutePath());
            }
            
            File destJar = new File(pluginsDir, jarFile.getName());
            if (!destJar.exists()) {
                Files.copy(jarFile.toPath(), destJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[SetupController] Copied JAR to plugins: " + destJar.getAbsolutePath());
            }
            
            SkillPackage skillPackage = SkillPackage.fromFile(destJar);
            PluginInstallResult result = pluginManager.installSkill(skillPackage);
            
            if (result.isSuccess()) {
                System.out.println("[SetupController] Skill installed successfully via PluginManager");
                File skillDir = jarFile.getParentFile() != null ? jarFile.getParentFile().getParentFile() : null;
                String skillPath = skillDir != null ? skillDir.getAbsolutePath() : jarFile.getParent();
                registerSkillInRegistry(skillId, skillPath);
                return true;
            } else {
                System.err.println("[SetupController] PluginManager install failed: " + result.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("[SetupController] Failed to install skill from JAR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void registerSkillInRegistry(String skillId, String skillPath) {
        try {
            File registryDir = new File("data/installed-skills");
            if (!registryDir.exists()) {
                registryDir.mkdirs();
            }
            
            File registryFile = new File("data/installed-skills/registry.properties");
            Properties props = new Properties();
            
            if (registryFile.exists()) {
                try (FileInputStream fis = new FileInputStream(registryFile)) {
                    props.load(fis);
                }
            }
            
            props.setProperty(skillId + ".id", skillId);
            props.setProperty(skillId + ".path", skillPath);
            props.setProperty(skillId + ".installedAt", String.valueOf(System.currentTimeMillis()));
            
            java.io.FileOutputStream fos = new java.io.FileOutputStream(registryFile);
            props.store(fos, "Installed Skills Registry");
            fos.close();
            
            System.out.println("[SetupController] Registered skill in registry: " + skillId + " -> " + skillPath);
        } catch (Exception e) {
            System.err.println("[SetupController] Failed to register skill: " + e.getMessage());
        }
    }
    
    private String findSkillPath(String skillId) {
        String[] possiblePaths = {
            "../skills/" + skillId,
            "skills/" + skillId,
            "../skills/_system/" + skillId,
            "skills/_system/" + skillId,
            "../skills/_drivers/llm/" + skillId,
            "skills/_drivers/llm/" + skillId,
            "../" + skillId,
            "./" + skillId
        };
        
        for (String path : possiblePaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                return dir.getAbsolutePath();
            }
        }
        
        String skillsDir = System.getProperty("skills.dir", "../skills");
        File skillDir = new File(skillsDir, skillId);
        if (skillDir.exists()) {
            return skillDir.getAbsolutePath();
        }
        
        File systemDir = new File(skillsDir, "_system/" + skillId);
        if (systemDir.exists()) {
            return systemDir.getAbsolutePath();
        }
        
        return null;
    }
}
