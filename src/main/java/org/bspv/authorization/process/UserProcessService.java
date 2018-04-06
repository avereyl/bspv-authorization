package org.bspv.authorization.process;

import java.util.Set;
import java.util.UUID;

import org.bspv.authorization.business.ServiceGrantedAuthorityBusinessService;
import org.bspv.authorization.business.UserBusinessService;
import org.bspv.authorization.business.exception.UserNotFoundException;
import org.bspv.authorization.business.exception.UsernameAlreadyExistingException;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.bspv.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProcessService {
    
    @Value("${bspv.security.service.name}")
    private String serviceName;
    
    @Autowired
    private UserBusinessService userBusinessService;
    
    @Autowired 
    private ServiceGrantedAuthorityBusinessService authoritiesService;

    /**
     * Find all (active) users according given pagination request.
     * Users are loaded with their authorities but without their password.
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    @PreAuthorize("isAdmin()")
    public Page<User> findUsers(Pageable pageable) {
        return userBusinessService.findUsers(pageable);
    }
    
    /**
     * Find all (enabled or disabled) users according given search parameters.
     * Users are loaded with their authorities but without their password.
     * @param userSearchWrapper
     *            Wrapper object containing search criterion
     * @return A {@link Set} of users.
     */
    @PreAuthorize("isAdmin()")
    public Set<User> findUsers(UserSearchWrapper userSearchWrapper) {
        return userBusinessService.findUsers(userSearchWrapper);
    }

    /**
     * Find all (enabled or disabled) users according given search parameters and pagination request.
     * Users are loaded with their authorities but without their password.
     * @param userSearchWrapper
     *            Wrapper object containing search criterion
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    @PreAuthorize("isAdmin()")
    public Page<User> findUsers(UserSearchWrapper userSearchWrapper, Pageable pageable) {
        return userBusinessService.findUsers(userSearchWrapper, pageable);
    }
    
    /**
     * Find a (enabled or disabled**) user according by its id.
     * User is loaded with its authorities but without its password.
     * ** only a user with 'ADMIN' authority can get a disabled user.
     * @param uuid
     *            User id
     * @return A {@link Page} of users.
     */
    @PreAuthorize("isAdmin() or isMyself(uuid)")
    public User findUser(UUID uuid) {
        return userBusinessService.findAnyUser(uuid);
    }

    /**
     * This method save a user.
     * @param user User to be saved
     * @return The saved user.
     * @throws UsernameAlreadyExistingException 
     */
    @PreAuthorize("isAdmin()")
    public User saveUser(final User user) throws UsernameAlreadyExistingException {
        //save user, password and authorities
        User savedUser = userBusinessService.saveUser(user);
        authoritiesService.replaceAuthorities(savedUser, user.getAuthorities());
        try {
            userBusinessService.saveUserPassword(user.getId(), user.getPassword());
        } catch (UserNotFoundException e) {
            // transaction is handled by process layer and so its very unlikely to reach this block !
        }
        return userBusinessService.findUser(user.getId());
        
    }

    @PreAuthorize("isAdmin()")
    public void saveUserUsername(UUID uuid, String username) throws UserNotFoundException {
        userBusinessService.saveUsername(uuid, username);
    }
    @PreAuthorize("isAdmin() or isMyself(uuid)")
    public void saveUserEmail(UUID uuid, String email) throws UserNotFoundException {
        userBusinessService.saveUserEmail(uuid, email);
        
    }
    @PreAuthorize("isAdmin() or isMyself(uuid)")
    public void saveUserPassword(UUID uuid, String password) throws UserNotFoundException {
        userBusinessService.saveUserPassword(uuid, password);
    }

    @PreAuthorize("isAdmin()")
    public void deleteUser(UUID uuid) {
        User user = userBusinessService.findAnyUser(uuid);
        if (user != null) {
            authoritiesService.revokeAllAuthorities(user);
            userBusinessService.deleteUser(uuid);
        }
    }

}
