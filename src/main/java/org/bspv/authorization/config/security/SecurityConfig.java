package org.bspv.authorization.config.security;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.security.jwt.mapper.TokenToUserDetailsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {
    
    
    @Value("${bspv.security.service.name}")
    private String serviceName;
    @Value("${bspv.security.fallback.username:admin}")
    private String fallbackAdminUsername;
    @Value("${bspv.security.fallback.password}")
    private String fallbackAdminPassword;
    
    /**
     * Default fallback admin user.
     * Please define also a compliant {@link TokenToUserDetailsMapper}.
     * 
     * @return
     */
    @Bean
    public User fallbackAdmin() {
        String password = fallbackAdminPassword;
        if (StringUtils.isEmpty(fallbackAdminPassword)) {
            password = KeyGenerators.string().generateKey();
            log.warn("Fallback admin password not set, using {}.", password);
        }
        return User.builder()
                .enable(true)
                .username(fallbackAdminUsername)
                .password(new BCryptPasswordEncoder(11).encode(password))
                .authority(new ServiceGrantedAuthority(serviceName, new SimpleGrantedAuthority("ADMIN")))
                .build();
    }

    /**
     * 
     * @return 
     */
    @Bean
    public TokenToUserDetailsMapper<User> token2UserMapper() {
        return new TokenToUserMapper();
    }
    
    /**
     * Adding a custom {@link MethodSecurityExpressionHandler} to enhance security expressions.
     * @return a new {@link CustomMethodSecurityExpressionHandler}
     */
    @Bean
    protected CustomMethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(serviceName);
        expressionHandler.setPermissionEvaluator(expressionHandler.getPermissionEvaluator());
        return expressionHandler;
    }
}
