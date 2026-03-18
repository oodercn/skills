package net.ooder.skill.scene.capability.config;

import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.skill.scene.capability.service.CapabilityStateService;
import net.ooder.skill.scene.capability.service.impl.CapabilityServiceImpl;
import net.ooder.skill.scene.capability.service.impl.CapabilityBindingServiceImpl;
import net.ooder.skill.scene.capability.service.impl.CapabilityStateServiceImpl;
import net.ooder.skill.scene.storage.JsonStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CapabilityConfig {

    @Autowired
    private JsonStorageService jsonStorageService;

    @Bean
    public CapabilityStateService capabilityStateService() {
        return new CapabilityStateServiceImpl();
    }

    @Bean
    public CapabilityService capabilityService() {
        return new CapabilityServiceImpl(jsonStorageService, capabilityStateService());
    }

    @Bean
    public CapabilityBindingService capabilityBindingService() {
        return new CapabilityBindingServiceImpl(jsonStorageService);
    }
}
