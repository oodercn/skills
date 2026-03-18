package net.ooder.skill.management.market;

import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkillMarketManager {
    private static final Logger logger = LoggerFactory.getLogger(SkillMarketManager.class);

    private static SkillMarketManager instance;
    
    private Map<String, SkillListing> skillListings;
    private Map<String, List<SkillListing>> categoryMap;
    private Map<String, SkillRatingInfo> skillRatings;

    private SkillMarketManager() {
        this.skillListings = new ConcurrentHashMap<>();
        this.categoryMap = new ConcurrentHashMap<>();
        this.skillRatings = new ConcurrentHashMap<>();
        initializeMarketData();
    }

    public static synchronized SkillMarketManager getInstance() {
        if (instance == null) {
            instance = new SkillMarketManager();
        }
        return instance;
    }

    private void initializeMarketData() {
        String[][] defaultSkills = {
            {"code-generation-skill", "代码生成技能", "生成各种编程语言的代码", "development", "1.0.0"},
            {"text-to-uppercase-skill", "文本转大写技能", "将文本转换为大写格式", "utilities", "1.0.0"},
            {"media-streaming-skill", "媒体流技能", "提供媒体流服务", "media", "1.0.0"},
            {"file-storage-skill", "文件存储技能", "提供文件存储服务", "storage", "1.0.0"},
            {"device-control-skill", "设备控制技能", "控制智能设备", "iot", "1.0.0"},
            {"data-analysis-skill", "数据分析技能", "数据分析和可视化", "analytics", "1.0.0"},
            {"security-scan-skill", "安全扫描技能", "安全漏洞扫描", "security", "1.0.0"},
            {"automation-skill", "自动化技能", "流程自动化", "automation", "1.0.0"}
        };
        
        for (String[] skillData : defaultSkills) {
            SkillListing listing = createSkillListing(
                skillData[0], skillData[1], skillData[2], skillData[3], skillData[4]
            );
            addSkillListing(listing);
        }
        
        logger.info("Initialized {} default skills in market", skillListings.size());
    }

    private SkillListing createSkillListing(String id, String name, String description, 
                                           String category, String version) {
        SkillListing listing = new SkillListing();
        listing.setSkillId(id);
        listing.setName(name);
        listing.setDescription(description);
        listing.setCategory(category);
        listing.setVersion(version);
        listing.setAuthor("Ooder Team");
        listing.setDownloadCount(1000);
        listing.setRating(4.5);
        listing.setReviewCount(50);
        listing.setLastUpdated(System.currentTimeMillis());
        listing.setType(inferSkillType(category));
        listing.setEndpoint("https://skillcenter.ooder.net/skills/" + id);
        listing.setHomepage("https://github.com/ooderCN/" + id);
        listing.setRepository("https://github.com/ooderCN/" + id + ".git");
        listing.setLicense("Apache-2.0");
        return listing;
    }

    private String inferSkillType(String category) {
        if ("development".equals(category) || "analytics".equals(category)) {
            return "tool-skill";
        } else if ("iot".equals(category) || "storage".equals(category)) {
            return "infrastructure-skill";
        } else if ("security".equals(category) || "automation".equals(category)) {
            return "enterprise-skill";
        }
        return "tool-skill";
    }

    public boolean publishSkill(SkillDefinition skill, SkillListing listing) throws SkillException {
        if (skill == null || listing == null) {
            throw new SkillException("unknown", "Skill and listing cannot be null", 
                                     SkillException.ErrorCode.PARAMETER_ERROR);
        }
        
        if (skillListings.containsKey(listing.getSkillId())) {
            throw new SkillException(listing.getSkillId(), "Skill already exists in market", 
                                     SkillException.ErrorCode.EXECUTION_EXCEPTION);
        }
        
        addSkillListing(listing);
        
        SkillRatingInfo ratingInfo = new SkillRatingInfo();
        ratingInfo.setSkillId(listing.getSkillId());
        skillRatings.put(listing.getSkillId(), ratingInfo);
        
        logger.info("Published skill: {}", listing.getSkillId());
        return true;
    }

    public boolean updateSkill(SkillListing listing) throws SkillException {
        if (listing == null) {
            throw new SkillException("unknown", "Listing cannot be null", 
                                     SkillException.ErrorCode.PARAMETER_ERROR);
        }
        
        if (!skillListings.containsKey(listing.getSkillId())) {
            throw new SkillException(listing.getSkillId(), "Skill not found in market", 
                                     SkillException.ErrorCode.SKILL_NOT_FOUND);
        }
        
        addSkillListing(listing);
        logger.info("Updated skill: {}", listing.getSkillId());
        return true;
    }

    public boolean removeSkill(String skillId) {
        if (skillId == null || skillId.isEmpty()) {
            return false;
        }
        
        SkillListing listing = skillListings.remove(skillId);
        if (listing != null) {
            List<SkillListing> listings = categoryMap.get(listing.getCategory());
            if (listings != null) {
                listings.remove(listing);
                if (listings.isEmpty()) {
                    categoryMap.remove(listing.getCategory());
                }
            }
            
            skillRatings.remove(skillId);
            logger.info("Removed skill: {}", skillId);
            return true;
        }
        
        return false;
    }

    private void addSkillListing(SkillListing listing) {
        skillListings.put(listing.getSkillId(), listing);
        categoryMap.computeIfAbsent(listing.getCategory(), k -> new ArrayList<>()).add(listing);
    }

    public List<SkillListing> searchSkills(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllSkills();
        }
        
        List<SkillListing> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (SkillListing listing : skillListings.values()) {
            if (listing.getName().toLowerCase().contains(lowerKeyword) ||
                listing.getDescription().toLowerCase().contains(lowerKeyword) ||
                listing.getCategory().toLowerCase().contains(lowerKeyword)) {
                results.add(listing);
            }
        }
        
        return results;
    }

    public List<SkillListing> getSkillsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(categoryMap.getOrDefault(category, Collections.emptyList()));
    }

    public List<SkillListing> getAllSkills() {
        return new ArrayList<>(skillListings.values());
    }

    public int getSkillCount() {
        return skillListings.size();
    }

    public SkillListing getSkillListing(String skillId) {
        return skillListings.get(skillId);
    }

    public boolean rateSkill(String skillId, double rating, String comment, String userId) throws SkillException {
        if (!skillListings.containsKey(skillId)) {
            throw new SkillException(skillId, "Skill not found in market", 
                                     SkillException.ErrorCode.SKILL_NOT_FOUND);
        }
        
        SkillRatingInfo ratingInfo = skillRatings.computeIfAbsent(skillId, k -> new SkillRatingInfo());
        ratingInfo.setSkillId(skillId);
        
        SkillReview review = new SkillReview();
        review.setSkillId(skillId);
        review.setUserId(userId);
        review.setRating((int) rating);
        review.setComment(comment);
        review.setTimestamp(System.currentTimeMillis());
        
        ratingInfo.addReview(review);
        
        SkillListing listing = skillListings.get(skillId);
        listing.setRating(ratingInfo.getAverageRating());
        listing.setReviewCount(ratingInfo.getReviewCount());
        
        return true;
    }

    public List<SkillReview> getSkillReviews(String skillId) {
        SkillRatingInfo ratingInfo = skillRatings.get(skillId);
        return ratingInfo != null ? ratingInfo.getReviews() : Collections.emptyList();
    }

    public List<String> getCategories() {
        return new ArrayList<>(categoryMap.keySet());
    }

    public List<SkillListing> getPopularSkills(int limit) {
        List<SkillListing> skills = new ArrayList<>(skillListings.values());
        skills.sort((a, b) -> Integer.compare(b.getDownloadCount(), a.getDownloadCount()));
        return skills.size() > limit ? skills.subList(0, limit) : skills;
    }

    public List<SkillListing> getLatestSkills(int limit) {
        List<SkillListing> skills = new ArrayList<>(skillListings.values());
        skills.sort((a, b) -> Long.compare(b.getLastUpdated(), a.getLastUpdated()));
        return skills.size() > limit ? skills.subList(0, limit) : skills;
    }

    public void clear() {
        skillListings.clear();
        categoryMap.clear();
        skillRatings.clear();
    }
}
