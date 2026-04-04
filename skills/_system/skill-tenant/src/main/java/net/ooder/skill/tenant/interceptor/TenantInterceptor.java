package net.ooder.skill.tenant.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String TENANT_COOKIE = "tenant_id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String tenantId = null;

        tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (TENANT_COOKIE.equals(cookie.getName())) {
                        tenantId = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = request.getParameter("tenantId");
        }

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setTenantId(tenantId);
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                TenantContext.setUserId(userId);
            }
            log.debug("[TenantInterceptor] Set tenant: {}, user: {}", tenantId, userId);
        } else {
            log.trace("[TenantInterceptor] No tenant context found for: {}", request.getRequestURI());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContext.clear();
    }
}
