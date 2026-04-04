package net.ooder.skill.auth.controller;

import net.ooder.skill.auth.dto.LoginDTO;
import net.ooder.skill.auth.dto.SessionDTO;
import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResultModel<SessionDTO> login(@RequestBody LoginDTO loginDTO) {
        log.info("[AuthController] Login request - username: {}", loginDTO.getUsername());
        
        SessionDTO session = new SessionDTO();
        session.setToken("mock-token-" + System.currentTimeMillis());
        session.setUserId("user-001");
        session.setUsername(loginDTO.getUsername());
        session.setRole("admin");
        session.setExpireTime(System.currentTimeMillis() + 3600000);
        
        return ResultModel.success(session);
    }

    @GetMapping("/session")
    public ResultModel<SessionDTO> getSession() {
        log.info("[AuthController] Get session");
        
        SessionDTO session = new SessionDTO();
        session.setToken("mock-token");
        session.setUserId("user-001");
        session.setUsername("admin");
        session.setRole("admin");
        
        return ResultModel.success(session);
    }

    @PostMapping("/logout")
    public ResultModel<Boolean> logout() {
        log.info("[AuthController] Logout");
        return ResultModel.success(true);
    }
}
