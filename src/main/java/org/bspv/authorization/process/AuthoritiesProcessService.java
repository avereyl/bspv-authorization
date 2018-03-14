package org.bspv.authorization.process;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AuthoritiesProcessService {


    /**
     * 
     * @param uuid
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.uuid == uuid")
    public List<ServiceGrantedAuthority> findUserAuthorities(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * @param authority
     * @param user
     * @param principal
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void grantAuthority(ServiceGrantedAuthority authority, User user) {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     * @param user
     * @param principal
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resetAuthorities(User user) {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     * @param authorities
     * @param user
     * @param principal
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void replaceAuthorities(Set<ServiceGrantedAuthority> authorities, User user) {
        // TODO Auto-generated method stub
        
    }


}
