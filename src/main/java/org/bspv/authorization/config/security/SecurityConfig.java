package org.bspv.authorization.config.security;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.security.jwt.mapper.TokenToUserDetailsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {
    
    @Value("${bspv.security.service.name}")
    private String serviceName;
    @Value("${bspv.security.fallback.username:admin}")
    private String fallbackAdminUsername;
    @Value("${bspv.security.fallback.password:admin}")
    private String fallbackAdminPassword;
    
    /**
     * Default fallback admin user.
     * Please define also a compliant {@link TokenToUserDetailsMapper}.
     * 
     * @return
     */
    @Bean
    public User fallbackAdmin() {
        return User.builder()
                .enable(true)
                .username(fallbackAdminUsername)
                .password(new BCryptPasswordEncoder(11).encode(fallbackAdminPassword))
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
}
