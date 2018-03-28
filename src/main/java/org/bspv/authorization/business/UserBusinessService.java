package org.bspv.authorization.business;

import java.util.List;
import java.util.UUID;

import org.bspv.authorization.model.User;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.bspv.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserBusinessService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Find any user (enabled or not) by its id.
     * @param uuid identifier of the user.
     * @return The user with given id if any, null otherwise.
     */
    public User findUser(UUID uuid) {
        return userRepository.findAnyUserById(uuid);
    }

    /**
     * Find all (active) users according given pagination request.
     * Users are loaded with their authorities but without his password.
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    public Page<User> findUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable);
    }
    
    public List<User> findUsers(UserSearchWrapper userSearchWrapper) {
        // TODO Auto-generated method stub
        return null;
    }

    public Page<User> findUsers(UserSearchWrapper userSearchWrapper, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    public User saveUser(User user) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * This method is responsible for saving following values for a user.
     * <ul>
     * <li>username
     * <li>password
     * <li>email
     * </ul>
     * @param user The user to be saved containing the new values.
     * @return The user saved.
     */
    public User saveUserPartially(User user) {
        // TODO Auto-generated method stub
        return null;
    }


}
