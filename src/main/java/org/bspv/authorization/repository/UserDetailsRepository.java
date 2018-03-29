/**
 * 
 */
package org.bspv.authorization.repository;

import org.bspv.authorization.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author BILLAUDG
 *
 */
public interface UserDetailsRepository {

    /**
     * Load the user by its username.
     * @param username name of the user
     * @return The user
     * @throws UsernameNotFoundException
     */
    User loadByUsername(String username) throws UsernameNotFoundException;
    
    /**
     * Change the password for the user with given username.
     * @param username Name of the user
     * @param newPassword new password
     */
    void changePassword(String username, String newPassword);
    
    /**
     * Enable the user.
     * @param username
     */
    void enableUser(String username);
    
    /**
     * Disable the user.
     * @param username
     */
    void disableUser(String username);
}
