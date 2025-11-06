package com.bank.workflow.adapter.web;

import com.bank.workflow.app.auth.AuthAppService;
import com.bank.workflow.app.auth.command.LoginCmd;
import com.bank.workflow.app.auth.dto.LoginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证授权控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthAppService authAppService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginCmd cmd) {
        log.info("接收登录请求: username={}", cmd.getUsername());
        
        try {
            LoginResult loginResult = authAppService.login(cmd);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", loginResult);
            return result;
        } catch (IllegalArgumentException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("登录失败: username={}, error={}", cmd.getUsername(), e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", e.getMessage());
            return result;
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        log.info("接收登出请求");
        
        // TODO: 实现Token黑名单机制
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登出成功");
        return result;
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("接收获取当前用户请求");
        
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            
            String token = authorization.substring(7);
            String username = authAppService.getUsernameFromToken(token);
            
            if (username == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 401);
                result.put("message", "Token无效");
                return result;
            }
            
            // TODO: 获取完整的用户信息
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", userData);
            return result;
        } catch (Exception e) {
            log.error("获取当前用户失败: error={}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "系统错误");
            return result;
        }
    }
}

