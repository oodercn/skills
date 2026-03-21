package net.ooder.mvp.skill.scene.audit;

import net.ooder.mvp.skill.scene.dto.audit.AuditEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    
    AuditEventType eventType();
    
    String action();
    
    String resourceType() default "";
    
    boolean logResult() default true;
    
    boolean logParams() default false;
}
