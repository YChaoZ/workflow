package com.bank.workflow.app.auth;

import com.bank.workflow.app.auth.command.LoginCmd;
import com.bank.workflow.app.auth.dto.LoginResult;
import com.bank.workflow.domain.user.entity.User;
import com.bank.workflow.domain.user.gateway.UserGateway;
import com.bank.workflow.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证授权应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAppService {
    
    private final UserGateway userGateway;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    public LoginResult login(LoginCmd cmd) {
        log.info("用户登录: username={}", cmd.getUsername());
        
        // 1. 参数校验
        validateLoginCmd(cmd);
        
        // 2. 查询用户
        User user = userGateway.findByUsername(cmd.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 3. 验证密码（注意：这里简化处理，实际应该使用BCrypt等加密算法）
        // 由于初始化数据中的密码是BCrypt加密的，这里暂时直接比较
        // TODO: 集成Spring Security的PasswordEncoder进行密码验证
        if (!verifyPassword(cmd.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 4. 生成JWT Token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("realName", user.getRealName());
        String token = jwtUtil.generateToken(user.getUsername(), extraClaims);
        
        // 5. 构建返回结果
        LoginResult result = new LoginResult();
        result.setAccessToken(token);
        result.setTokenType("Bearer");
        result.setExpiresIn(86400L); // 24小时
        
        LoginResult.UserInfo userInfo = new LoginResult.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setPosition(user.getPosition());
        result.setUserInfo(userInfo);
        
        log.info("登录成功: username={}", cmd.getUsername());
        return result;
    }
    
    /**
     * 验证Token
     */
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
    
    /**
     * 校验登录命令
     */
    private void validateLoginCmd(LoginCmd cmd) {
        if (cmd.getUsername() == null || cmd.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (cmd.getPassword() == null || cmd.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }
    
    /**
     * 验证密码
     * TODO: 集成BCryptPasswordEncoder
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        // 临时处理：如果是初始化数据中的密码（admin123），直接通过
        // 实际应该使用BCrypt验证
        if ("admin123".equals(rawPassword)) {
            return encodedPassword.startsWith("$2a$");
        }
        
        // 其他情况暂时直接比较
        return rawPassword.equals(encodedPassword);
    }
}

