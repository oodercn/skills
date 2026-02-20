package net.ooder.skill.traesolo.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/capability/project-navigation")
public class ProjectNavigationCapability {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectNavigationCapability.class);
    private static final String GITHUB_ROOT = "E:\\github";
    
    @PostMapping
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        log.info("Project navigation request: {}", request);
        
        String operation = (String) request.getOrDefault("operation", "list");
        
        try {
            Object result;
            switch (operation) {
                case "list":
                    result = listProjects();
                    break;
                case "tree":
                    String projectCode = (String) request.get("projectCode");
                    result = getProjectTree(projectCode);
                    break;
                case "find":
                    String pattern = (String) request.get("pattern");
                    result = findFiles(pattern);
                    break;
                case "info":
                    projectCode = (String) request.get("projectCode");
                    result = getProjectInfo(projectCode);
                    break;
                default:
                    return error("INVALID_OPERATION", "Unknown operation: " + operation);
            }
            return success(result);
        } catch (Exception e) {
            return error("EXECUTION_ERROR", e.getMessage());
        }
    }
    
    private List<Map<String, Object>> listProjects() {
        List<Map<String, Object>> projects = new ArrayList<Map<String, Object>>();
        File root = new File(GITHUB_ROOT);
        
        if (root.exists() && root.isDirectory()) {
            for (File dir : root.listFiles()) {
                if (dir.isDirectory() && !dir.getName().startsWith(".")) {
                    Map<String, Object> project = new LinkedHashMap<String, Object>();
                    project.put("name", dir.getName());
                    project.put("path", dir.getAbsolutePath());
                    project.put("hasReadme", new File(dir, "README.md").exists());
                    project.put("hasPom", new File(dir, "pom.xml").exists());
                    projects.add(project);
                }
            }
        }
        
        return projects;
    }
    
    private Map<String, Object> getProjectTree(String projectCode) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        String projectPath = getProjectPath(projectCode);
        
        if (projectPath == null) {
            result.put("error", "Project not found: " + projectCode);
            return result;
        }
        
        File projectDir = new File(projectPath);
        result.put("name", projectDir.getName());
        result.put("path", projectDir.getAbsolutePath());
        result.put("children", buildTree(projectDir, 0, 3));
        
        return result;
    }
    
    private List<Map<String, Object>> buildTree(File dir, int depth, int maxDepth) {
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        
        if (depth >= maxDepth) {
            return children;
        }
        
        File[] files = dir.listFiles();
        if (files == null) return children;
        
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                if (a.isDirectory() && !b.isDirectory()) return -1;
                if (!a.isDirectory() && b.isDirectory()) return 1;
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        
        for (File file : files) {
            if (file.getName().startsWith(".") || 
                file.getName().equals("target") ||
                file.getName().equals("node_modules")) {
                continue;
            }
            
            Map<String, Object> node = new LinkedHashMap<String, Object>();
            node.put("name", file.getName());
            node.put("type", file.isDirectory() ? "directory" : "file");
            
            if (file.isDirectory()) {
                node.put("children", buildTree(file, depth + 1, maxDepth));
            }
            
            children.add(node);
        }
        
        return children;
    }
    
    private List<Map<String, Object>> findFiles(String pattern) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        File root = new File(GITHUB_ROOT);
        
        if (pattern == null || pattern.isEmpty()) {
            return results;
        }
        
        searchFiles(root, pattern.toLowerCase(), results, 0, 100);
        
        return results;
    }
    
    private void searchFiles(File dir, String pattern, List<Map<String, Object>> results, int count, int maxResults) {
        if (count >= maxResults) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.getName().startsWith(".") || 
                file.getName().equals("target") ||
                file.getName().equals("node_modules")) {
                continue;
            }
            
            if (file.getName().toLowerCase().contains(pattern)) {
                Map<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("name", file.getName());
                result.put("path", file.getAbsolutePath());
                result.put("type", file.isDirectory() ? "directory" : "file");
                results.add(result);
                
                if (results.size() >= maxResults) return;
            }
            
            if (file.isDirectory()) {
                searchFiles(file, pattern, results, results.size(), maxResults);
            }
        }
    }
    
    private Map<String, Object> getProjectInfo(String projectCode) {
        Map<String, Object> info = new LinkedHashMap<String, Object>();
        String projectPath = getProjectPath(projectCode);
        
        if (projectPath == null) {
            info.put("error", "Project not found: " + projectCode);
            return info;
        }
        
        File projectDir = new File(projectPath);
        info.put("code", projectCode);
        info.put("name", projectDir.getName());
        info.put("path", projectDir.getAbsolutePath());
        
        File readme = new File(projectDir, "README.md");
        if (readme.exists()) {
            info.put("hasReadme", true);
            info.put("readmePath", readme.getAbsolutePath());
        }
        
        File pom = new File(projectDir, "pom.xml");
        if (pom.exists()) {
            info.put("hasPom", true);
            info.put("pomPath", pom.getAbsolutePath());
        }
        
        File changelog = new File(projectDir, "CHANGELOG.md");
        if (changelog.exists()) {
            info.put("hasChangelog", true);
        }
        
        return info;
    }
    
    private String getProjectPath(String projectCode) {
        Map<String, String> projectPaths = new HashMap<String, String>();
        projectPaths.put("COMMON", "a2ui/ooder-common");
        projectPaths.put("SDK", "guper-Agent/agent-sdk");
        projectPaths.put("NEXUS", "ooder-Nexus");
        projectPaths.put("NEXUS-ENT", "ooder-Nexus-Enterprise");
        projectPaths.put("NORTH", "northbound-services");
        projectPaths.put("SKILLS", "ooder-skills");
        projectPaths.put("A2UI", "a2ui");
        projectPaths.put("OVERALL", "overall-design");
        
        String relativePath = projectPaths.get(projectCode);
        if (relativePath != null) {
            return GITHUB_ROOT + "\\" + relativePath.replace("/", "\\");
        }
        
        return null;
    }
    
    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }
    
    private Map<String, Object> error(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", false);
        result.put("errorCode", code);
        result.put("errorMessage", message);
        return result;
    }
}
