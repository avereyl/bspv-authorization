/**
 * 
 */
package org.bspv.authorization.repository;

import java.util.Set;

import org.bspv.security.model.ServiceGrantedAuthority;
import org.bspv.security.model.User;

/**
 *
 */
public interface ServiceGrantedAuthorityRepository {

    /**
     * Find all authorithies granted to a user.
     * @param user The user
     * @return Set or {@link ServiceGrantedAuthority}
     */
    Set<ServiceGrantedAuthority> findAuthorithies(User user);
    /**
     * Find all authorithies granted to a user for a service.
     * @param user The user
     * @param service The service
     * @return Set or {@link ServiceGrantedAuthority}
     */
    Set<ServiceGrantedAuthority> findAuthorithies(User user, String service);
    /**
     * Grant the given authorities to the user for the service.
     * @param user The user
     * @param service The service
     * @param authorities The set of authorities to grant (SHOULD NOT BE EMPTY)
     */
    void grantAuthorithies(User user, String service, Set<String> authorities);
    /**
     * Grant the given authorities to the user for the service.
     * @param user The user
     * @param authorities The set of {@link ServiceGrantedAuthority} to grant (SHOULD NOT BE EMPTY)
     */
    void grantAuthorithies(User user, Set<ServiceGrantedAuthority> authorities);
    /**
     * Revoke all authorities of the user.
     * @param user The user
     */
    void revokeAllAuthorities(User user);
    /**
     * Revoke all authorities of the user for the given service.
     * @param user The user
     * @param service The service
     */
    void revokeAllAuthorities(User user, String service);
    /**
     * Revoke all given authorities of the user for a service.
     * @param user The user
     * @param service The service
     * @param authorities The set of authorities to revoke
     */
    void revokeAuthorities(User user, String service, Set<String> authorities);
    /**
     * Revoke the authority of the user.
     * @param user The user
     * @param authority The authority to revoke
     */
    void revokeAuthority(User user, ServiceGrantedAuthority authority);
    
}
