package net.ooder.mvp.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SetupInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        if (isStaticResource(uri) || isSetupUri(uri)) {
            return true;
        }
        
        if (!isInstalled()) {
            response.sendRedirect("/setup/index.html");
            return false;
        }
        
        return true;
    }

    private boolean isStaticResource(String uri) {
        return uri.endsWith(".html") ||
               uri.endsWith(".css") ||
               uri.endsWith(".js") ||
               uri.endsWith(".svg") ||
               uri.endsWith(".ico") ||
               uri.endsWith(".png") ||
               uri.endsWith(".jpg") ||
               uri.endsWith(".woff") ||
               uri.endsWith(".woff2") ||
               uri.endsWith(".ttf") ||
               uri.endsWith(".eot") ||
               uri.startsWith("/console/") ||
               uri.startsWith("/setup/");
    }

    private boolean isSetupUri(String uri) {
        return uri.startsWith("/api/v1/setup") ||
               uri.startsWith("/api/v1/plugin") ||
               uri.startsWith("/actuator") ||
               uri.startsWith("/error");
    }

    private boolean isInstalled() {
        java.io.File markerFile = new java.io.File("data/.installed");
        return markerFile.exists();
    }
}
