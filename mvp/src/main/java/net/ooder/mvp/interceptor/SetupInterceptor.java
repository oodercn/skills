package net.ooder.mvp.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;

@Component
public class SetupInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        if (isInstalled()) {
            return true;
        }
        
        if (requestURI.startsWith("/setup") || 
            requestURI.startsWith("/api/v1/setup") ||
            requestURI.startsWith("/api/v1/plugin") ||
            requestURI.startsWith("/console") ||
            requestURI.equals("/favicon.svg") ||
            requestURI.startsWith("/css/") ||
            requestURI.startsWith("/js/") ||
            requestURI.startsWith("/images/") ||
            requestURI.equals("/error")) {
            return true;
        }
        
        response.sendRedirect("/setup/index.html");
        return false;
    }
    
    private boolean isInstalled() {
        File markerFile = new File("data/.installed");
        return markerFile.exists();
    }
}
