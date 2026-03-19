package net.ooder.skill.share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Skill Share 搴旂敤绋嬪簭
 * SDK 2.3 杩佺Щ鐗堟湰
 */
@SpringBootApplication(scanBasePackages = {
    "net.ooder.skill.share",
    "net.ooder.sdk"
})
public class SkillShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillShareApplication.class, args);
    }
}
