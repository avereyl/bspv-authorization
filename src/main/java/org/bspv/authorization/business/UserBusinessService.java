package org.bspv.authorization.business;

import java.util.Set;
import java.util.UUID;

import org.bspv.authorization.business.exception.UserNotFoundException;
import org.bspv.authorization.model.User;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.bspv.authorization.repository.UserDetailsRepository;
import org.bspv.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserBusinessService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    
    /**
     * Load a user (enabled) by its id.
     * @param uuid identifier of the user.
     * @return The user with given id
     * @throws UserNotFoundException if the user is not found
     */
    public User loadUser(UUID uuid) throws UserNotFoundException {
        User user = userRepository.findUserById(uuid);
        if (user == null) {
            throw new UserNotFoundException("User not found for id "+ uuid);
        }
        return user;
    }
    
    /**
     * Find a user (enabled) by its id.
     * @param uuid identifier of the user.
     * @return The user with given id if any, null otherwise.
     */
    public User findUser(UUID uuid) {
        return userRepository.findUserById(uuid);
    }
    
    /**
     * Find any user (enabled or not) by its id.
     * @param uuid identifier of the user.
     * @return The user with given id if any, null otherwise.
     */
    public User findAnyUser(UUID uuid) {
        return userRepository.findAnyUserById(uuid);
    }

    /**
     * Find all (enabled) users according given pagination request.
     * Users are loaded with their authorities but without his password.
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    public Page<User> findUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable);
    }
    
    /**
     * Find all (enabled and disabled) users according given search criterion wrapper.
     * Users are loaded with their authorities but without their password.
     * @param userSearchWrapper
     *            user search criterion wrapper
     * @return A {@link Set} of users.
     */
    public Set<User> findUsers(UserSearchWrapper userSearchWrapper) {
        return userRepository.findAnyUsers(userSearchWrapper);
    }

    /**
     * Find all (enabled and disabled) users according given search criterion wrapper and page request.
     * Users are loaded with their authorities but without their password.
     * @param userSearchWrapper
     *            user search criterion wrapper
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    public Page<User> findUsers(UserSearchWrapper userSearchWrapper, Pageable pageable) {
        return userRepository.findAnyUsers(userSearchWrapper, pageable);
    }

    public User saveUser(User user) {
        return (user.getVersion() == 0L) ? userRepository.insert(user) : userRepository.update(user);
    }

    public void saveUsername(UUID uuid, String username) throws UserNotFoundException {
        User user = this.loadUser(uuid);
        userRepository.update(user.toBuilder().username(username).build());
    }

    public void saveUserEmail(UUID uuid, String email) throws UserNotFoundException {
        User user = this.loadUser(uuid);
        userRepository.update(user.toBuilder().email(email).build());
    }

    public void saveUserPassword(UUID uuid, String password) throws UserNotFoundException {
        User user = this.loadUser(uuid);
        userDetailsRepository.changePassword(user.getUsername(), password);
    }
    
}
