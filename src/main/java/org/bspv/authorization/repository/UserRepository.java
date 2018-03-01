/**
 * 
 */
package org.bspv.authorization.repository;

import java.util.Set;
import java.util.UUID;

import org.bspv.authorization.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 */
public interface UserRepository {

    /**
     * Insert a new user, raises an exception if a user with same identifier already
     * exists. This method IGNORE users authorities please @see {@link ServiceGrantedAuthorityRepository}.
     * 
     * @param user
     *            The user to be inserted
     * @return The newly created user
     */
    User insert(User user);

    /**
     * Insert a new user, if a user with same identifier already exists then
     * increment its version.
     * This method IGNORE users authorities please @see {@link ServiceGrantedAuthorityRepository}.
     * 
     * @param user
     *            The user to be inserted
     * @return The newly created user
     */
    User insertWithSilentError(User user);

    /**
     * Update user's values, NOR its authorities neither his password !
     * @see {@link ServiceGrantedAuthorityRepository}.
     *  
     * @param user
     * @return the updated user
     */
    User update(User user);

    /**
     * Find all (active) users according given pagination request.
     * Users are loaded with their authorities but without his password.
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    Page<User> findAllUsers(Pageable pageable);

    /**
     * Find all (enabled and disabled) users according given pagination request.
     * Users are loaded with their authorities but without his password.
     * @param pageable
     *            Pagination request
     * @return A {@link Page} of users.
     */
    Page<User> findAnyUsers(Pageable pageable);

    /**
     * Find the user (enabled) with the given id.
     * User is loaded with their authorities but without his password.
     * @param uuid
     *            id of the user
     * @return The user
     */
    User findUserById(UUID uuid);

    /**
     * Find the user (enabled and disabled) with the given id.
     * User is loaded with their authorities but without his password.
     * @param uuid
     *            id of the user
     * @return The user
     */
    User findAnyUserById(UUID uuid);
    
    /**
     * Find the users (enabled) with the given ids.
     * Users are loaded with their authorities but without his password.
     * @param uuids
     *            ids of the users
     * @return The users set
     */
    Set<User> findUsersByIds(Set<UUID> uuids);
    
    /**
     * Find the users (enabled and disabled) with the given ids.
     * Users are loaded with their authorities but without his password.
     * @param uuids
     *            ids of the users
     * @return The users set
     */
    Set<User> findAnyUsersByIds(Set<UUID> uuids);

}
