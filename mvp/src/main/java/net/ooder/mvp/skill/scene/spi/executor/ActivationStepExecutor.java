package net.ooder.mvp.skill.scene.spi.executor;

import net.ooder.mvp.skill.scene.template.ActivationStepConfig;
import net.ooder.mvp.skill.scene.capability.activation.ActivationProcess;

import java.util.Map;

public interface ActivationStepExecutor {
    
    boolean canExecute(ActivationStepConfig stepConfig);
    
    StepResult execute(ActivationStepConfig stepConfig, 
                       ActivationProcess process, 
                       Map<String, Object> context);
}
