package org.bspv.authorization.config;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.security.jwt.mapper.TokenToUserDetailsMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TokenToUserMapper implements TokenToUserDetailsMapper<User> {
    
    public static final String USER_ID_CLAIM_NAME = "id";
    public static final String VERSION_CLAIM_NAME = "version";
    public static final String EMAIL_CLAIM_NAME = "email";
    public static final String AUTHORITIES_CLAIM_NAME = "scopes";

    /*
     * (non-Javadoc)
     * @see org.bspv.security.jwt.mapper.TokenToUserDetailsMapper#toUserDetails(io.jsonwebtoken.Claims)
     */
    @Override
    public User toUserDetails(Claims claims) {
        List<String> scopes = claims.get(AUTHORITIES_CLAIM_NAME, List.class);
//        List<ServiceGrantedAuthority> authorities = scopes
//                .stream()
//                .map(ServiceGrantedAuthority::new)
//                .collect(Collectors.toList());
        return User.builder()
                .id(UUID.fromString(claims.get(USER_ID_CLAIM_NAME, String.class)))
                .username(claims.getSubject())
                .email(claims.get(EMAIL_CLAIM_NAME, String.class))
//                .authorities(authorities)
                .version(claims.get(VERSION_CLAIM_NAME, Integer.class).longValue()).build();
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.security.jwt.mapper.TokenToUserDetailsMapper#toClaims(org.springframework.security.core.userdetails.UserDetails)
     */
    @Override
    public Claims toClaims(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put(USER_ID_CLAIM_NAME, user.getId().toString());
        claims.put(VERSION_CLAIM_NAME, user.getVersion());
        claims.put(EMAIL_CLAIM_NAME, user.getEmail());
        claims.put(AUTHORITIES_CLAIM_NAME, user.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        return claims;
    }

}
