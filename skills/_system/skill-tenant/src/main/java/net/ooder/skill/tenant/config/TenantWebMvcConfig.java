package net.ooder.skill.tenant.config;

import net.ooder.skill.tenant.interceptor.TenantInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TenantWebMvcConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(TenantWebMvcConfig.class);

    private final TenantInterceptor tenantInterceptor;

    public TenantWebMvcConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/v1/auth/**",
                        "/api/v1/tenants",
                        "/api/v1/health"
                );
        log.info("[TenantWebMvcConfig] Registered TenantInterceptor for /api/**");
    }
}
