package org.bspv.authorization.business;

import java.util.Collections;
import java.util.Set;

import org.bspv.authorization.repository.ServiceGrantedAuthorityRepository;
import org.bspv.security.model.ServiceGrantedAuthority;
import org.bspv.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceGrantedAuthorityBusinessService {

    @Autowired
    private ServiceGrantedAuthorityRepository authoritiesRepository;
    
    public Set<ServiceGrantedAuthority> findAuthorithies(User user) {
        return authoritiesRepository.findAuthorithies(user);
    }

    public void replaceAuthorities(User user, Set<ServiceGrantedAuthority> authorities) {
        authoritiesRepository.revokeAllAuthorities(user);
        authoritiesRepository.grantAuthorithies(user, authorities);
    }

    public void grantAuthorithies(User user, Set<ServiceGrantedAuthority> authorities) {
        authoritiesRepository.grantAuthorithies(user, authorities);
    }

    public void revokeAllAuthorities(User user) {
        authoritiesRepository.revokeAllAuthorities(user);
    }

    public void revokeAuthority(User user, String service, String authority) {
        authoritiesRepository.revokeAuthorities(user, service, Collections.singleton(authority));
    }

}
