package net.ooder.skill.onboarding.assistant.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.onboarding.assistant")
@ConditionalOnProperty(name = "skill.onboarding.assistant.enabled", havingValue = "true", matchIfMissing = true)
public class OnboardingAssistantAutoConfiguration {
}
