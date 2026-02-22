package net.ooder.skill.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消息服务技能启动类
 * 
 * <p>提供消息推送、Topic管理和P2P通信功能。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
@SpringBootApplication
public class MsgServiceSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MsgServiceSkillApplication.class, args);
    }
}
