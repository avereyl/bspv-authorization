package org.bspv.authorization.process;

import java.util.List;
import java.util.UUID;

import org.bspv.authorization.business.UserBusinessService;
import org.bspv.authorization.model.User;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProcessService {
    
    @Value("${bspv.security.service.name}")
    private String serviceName;
    
    @Autowired
    private UserBusinessService userBusinessService;

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
     * @return A {@link Page} of users.
     */
    @PreAuthorize("isAdmin()")
    public List<User> findUsers(UserSearchWrapper userSearchWrapper) {
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
        return userBusinessService.findUser(uuid);
    }

    /**
     * This method save a user (fully or partially according principal rights).
     * @param user User to be saved
     * @return
     */
    @PreAuthorize("isAdmin() or isMyself(user.id)")
    public User saveUser(User user) {
        // Even if the principal can access this method, it is not allowed to change anything on its account if not admin.
        boolean iAmAdmin = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getAuthorities()
                .stream()
                .anyMatch( a -> this.serviceName.equals(a.getService()) && "ADMIN".equals(a.getGrantedAuthority().getAuthority()));
        boolean isNewUser = user.getVersion() == 0L;
        User savedUser = null;
        if (!iAmAdmin) {
            if (isNewUser) {
                throw new AccessDeniedException("You are not allowed to create a user.");
            }
            savedUser = userBusinessService.saveUserPartially(user);
        } else {
            savedUser = userBusinessService.saveUser(user);
        }
        return savedUser;
    }

}
