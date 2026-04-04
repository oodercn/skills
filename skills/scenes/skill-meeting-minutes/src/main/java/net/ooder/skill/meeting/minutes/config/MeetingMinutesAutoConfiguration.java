package net.ooder.skill.meeting.minutes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.meeting.minutes")
@ConditionalOnProperty(name = "skill.meeting.minutes.enabled", havingValue = "true", matchIfMissing = true)
public class MeetingMinutesAutoConfiguration {
}
