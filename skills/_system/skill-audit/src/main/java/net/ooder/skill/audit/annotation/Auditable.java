package net.ooder.skill.audit.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {

    String action() default "";

    String module() default "";

    String resourceType() default "";

    boolean logParams() default false;

    boolean logResult() default false;

    boolean logException() default true;
}
