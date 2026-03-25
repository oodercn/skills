package net.ooder.mvp;

import net.ooder.mvp.skill.scene.config.GiteeDiscoveryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration.class,
    org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class,
    org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration.class,
    org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration.class,
    org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration.class
})
@EnableConfigurationProperties(GiteeDiscoveryProperties.class)
@ComponentScan(
    basePackages = {
        "net.ooder.mvp",
        "net.ooder.mvp.skill.scene",
        "net.ooder.skill.common",
        "net.ooder.skill.capability",
        "net.ooder.skill.llm",
        "net.ooder.skill.hotplug",
        "net.ooder.skill.org"
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.skill\\.capability\\.controller\\.CapabilityStatsController"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.mvp\\.skill\\.scene\\.SceneSkillApplication"
        )
    }
)
public class MvpCoreApplication {
    
    public static void main(String[] args) {
        System.setProperty("io.netty.resolver.dns.preferNative", "false");
        System.setProperty("reactor.netty.resolver.transport", "jdk");
        SpringApplication.run(MvpCoreApplication.class, args);
    }
    
    @Component
    public static class BrowserLauncher {
        
        @Value("${server.port:8084}")
        private int serverPort;
        
        @Value("${ooder.browser.auto-open:true}")
        private boolean autoOpenBrowser;
        
        @Value("${ooder.browser.install-page:/console/pages/config-system.html}")
        private String installPage;
        
        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {
            if (autoOpenBrowser) {
                try {
                    String url = "http://localhost:" + serverPort + installPage;
                    System.out.println("[MvpCore] Opening browser: " + url);
                    
                    String os = System.getProperty("os.name").toLowerCase();
                    ProcessBuilder pb;
                    
                    if (os.contains("win")) {
                        pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
                    } else if (os.contains("mac")) {
                        pb = new ProcessBuilder("open", url);
                    } else if (os.contains("nix") || os.contains("nux")) {
                        pb = new ProcessBuilder("xdg-open", url);
                    } else {
                        System.err.println("[MvpCore] Unsupported OS for browser launch: " + os);
                        return;
                    }
                    
                    pb.start();
                    System.out.println("[MvpCore] Browser launched successfully");
                    
                } catch (Exception e) {
                    System.err.println("[MvpCore] Failed to open browser: " + e.getMessage());
                }
            }
        }
    }
}
