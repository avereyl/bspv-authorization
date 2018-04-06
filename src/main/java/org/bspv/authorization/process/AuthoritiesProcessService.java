package org.bspv.authorization.process;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bspv.authorization.business.ServiceGrantedAuthorityBusinessService;
import org.bspv.authorization.business.UserBusinessService;
import org.bspv.authorization.business.exception.UserNotFoundException;
import org.bspv.security.model.ServiceGrantedAuthority;
import org.bspv.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class AuthoritiesProcessService {

    @Autowired
    private UserBusinessService userBusinessService;
    
    @Autowired
    private ServiceGrantedAuthorityBusinessService authoritiesBusinessService;

    /**
     * 
     * @param uuid
     * @return
     * @throws UserNotFoundException 
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.uuid == uuid")
    public Set<ServiceGrantedAuthority> findUserAuthorities(UUID uuid) throws UserNotFoundException {
        User user = userBusinessService.loadUser(uuid);
        return authoritiesBusinessService.findAuthorithies(user);
    }

    /**
     * 
     * @param authority
     * @param user
     * @param principal
     * @throws UserNotFoundException 
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void grantAuthority(ServiceGrantedAuthority authority, UUID uuid) throws UserNotFoundException {
        User user = userBusinessService.loadUser(uuid);
        authoritiesBusinessService.grantAuthorithies(user, Collections.singleton(authority));
    }

    /**
     * 
     * @param user
     * @param principal
     * @throws UserNotFoundException 
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resetAuthorities(UUID uuid) throws UserNotFoundException {
        User user = userBusinessService.loadUser(uuid);
        authoritiesBusinessService.revokeAllAuthorities(user);
    }

    /**
     * 
     * @param authorities
     * @param user
     * @param principal
     * @throws UserNotFoundException 
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void replaceAuthorities(Set<ServiceGrantedAuthority> authorities, UUID uuid) throws UserNotFoundException {
        User user = userBusinessService.loadUser(uuid);
        authoritiesBusinessService.replaceAuthorities(user, authorities);
    }

    /**
     * 
     * @param user
     * @param authority
     * @return
     * @throws UserNotFoundException 
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void revokeAuthority(String service, String authority, UUID uuid) throws UserNotFoundException {
        User user = userBusinessService.loadUser(uuid);
        user.getAuthorities().stream()
        .filter(a -> StringUtils.isEmpty(service) || service.equals(a.getService()))
        .filter(a -> StringUtils.isEmpty(authority) || authority.equals(a.getAuthority()))
        .forEach(a -> {authoritiesBusinessService.revokeAuthority(user, service, authority);});
    }


}
