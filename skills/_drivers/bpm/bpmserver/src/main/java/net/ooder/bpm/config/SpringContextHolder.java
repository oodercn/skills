package net.ooder.bpm.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) return null;
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            return null;
        }
    }

    public static JdbcTemplate getJdbcTemplate() {
        return getBean(JdbcTemplate.class);
    }
}
