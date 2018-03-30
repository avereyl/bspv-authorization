package org.bspv.authorization.rest.controller;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bspv.authorization.business.exception.UserNotFoundException;
import org.bspv.authorization.business.exception.UsernameAlreadyExistingException;
import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.authorization.model.wrapper.UserSearchWrapper;
import org.bspv.authorization.process.AuthoritiesProcessService;
import org.bspv.authorization.process.UserProcessService;
import org.bspv.authorization.rest.beans.ServiceGrantedAuthorityBean;
import org.bspv.authorization.rest.beans.UserBean;
import org.bspv.commons.rest.controller.exception.BadRequestException;
import org.bspv.commons.rest.controller.exception.NotFoundException;
import org.bspv.commons.rest.controller.support.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController("/")
public class UserController {

    @Autowired
    private UserProcessService userProcessService;

    @Autowired
    private AuthoritiesProcessService authoritiesProcessService;

    /**
     */
    @GetMapping("")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Return a list of a page of users according search parameters.
     * <ul>
     * <li>GET /users/?enabled
     * <li>GET /users/?disabled
     * <li>GET /users/?username=avereyl
     * <li>GET /users/?username=avereyl&enabled
     * <li>GET
     * /users/?ids=4a336911-a45a-4130-9ecd-f18592740c45,cd175357-aa26-464b-8ca0-a4e4588c2c9f
     * </ul>
     * 
     * @param range
     * @param queryMap
     * @param principal
     * @return Page of users.
     */
    @GetMapping(value = "users/")
    public ResponseEntity<Iterable<User>> findUsers(@RequestParam(value = "range", required = false) String range,
            @RequestParam MultiValueMap<String, String> queryMap,
            @AuthenticationPrincipal(errorOnInvalidType = true) User principal) {
        UserSearchWrapper userSearchWrapper = new UserSearchWrapper(queryMap);
        if (StringUtils.isEmpty(range)) {
            Set<User> set = this.userProcessService.findUsers(userSearchWrapper);
            return ResponseEntity.ok(set);
        } else {
            PageRequest pageRequest = PaginationHelper.rangeToPageRequest(range);
            Page<User> page = this.userProcessService.findUsers(userSearchWrapper, pageRequest);
            return ResponseEntity.ok(page);
        }

    }

    /**
     * Return the user with the given uuid.
     * 
     * @param uuid
     *            id of the targeted user
     * @return The user
     */
    @GetMapping("users/{uuid}")
    public ResponseEntity<User> findUser(@PathVariable("uuid") UUID uuid) {
        User user = this.userProcessService.findUser(uuid);
        return (user == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().eTag("\"" + user.getVersion() + "\"").body(user);
    }

    /**
     * Return the list of user authorities for the given user uuid.
     * 
     * @param uuid
     *            id of the targeted user
     * @param service
     * @param authority
     * @param principal
     * @return
     */
    @GetMapping("users/{uuid}/authorities/")
    public ResponseEntity<Set<ServiceGrantedAuthority>> findUserAuthorities(@PathVariable(value = "uuid") UUID uuid,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "authority", required = false) String authority,
            @AuthenticationPrincipal User principal) {
        try {
            Set<ServiceGrantedAuthority> authorities = this.authoritiesProcessService.findUserAuthorities(uuid);
            authorities = authorities.stream()
                    .filter(a -> StringUtils.isEmpty(service) || service.equals(a.getService()))
                    .filter(a -> StringUtils.isEmpty(authority) || authority.equals(a.getAuthority()))
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(authorities);
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Grant the given authority to the user. (Duplicate authority is ignored)
     * 
     * @param uuid
     * @param authorityBean
     * @param principal
     * @return
     */
    @PostMapping("users/{uuid}/authorities/")
    public ResponseEntity<ServiceGrantedAuthority> grantAuthority(@PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated ServiceGrantedAuthorityBean authorityBean,
            @AuthenticationPrincipal User principal) {

        ServiceGrantedAuthority authority = ServiceGrantedAuthority.builder().service(authorityBean.getService())
                .grantedAuthority(new SimpleGrantedAuthority(authorityBean.getAuthority())).build();
        try {
            authoritiesProcessService.grantAuthority(authority, uuid);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("?service={s}&authority={a}")
                    .buildAndExpand(authority.getService(), authority.getAuthority()).toUri();
            return ResponseEntity.created(location).build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * 
     * @param uuid
     * @param authorityBeans
     * @param principal
     * @return
     */
    @PutMapping("users/{uuid}/authorities/")
    public ResponseEntity<List<ServiceGrantedAuthority>> grantAuthorities(@PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated List<ServiceGrantedAuthorityBean> authorityBeans,
            @AuthenticationPrincipal User principal) {
        Set<ServiceGrantedAuthority> authorities = authorityBeans.stream()
                .map(b -> ServiceGrantedAuthority.builder()
                        .service(b.getService())
                        .grantedAuthority(new SimpleGrantedAuthority(b.getAuthority()))
                        .build())
                .collect(Collectors.toSet());
        try {
            authoritiesProcessService.replaceAuthorities(authorities, uuid);
            return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * 
     * @param uuid
     * @param service
     * @param authority
     * @param principal
     * @return
     */
    @DeleteMapping("users/{uuid}/authorities/")
    public ResponseEntity<List<ServiceGrantedAuthority>> revokeAuthorities(@PathVariable(value = "uuid") UUID uuid,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "authority", required = false) String authority,
            @AuthenticationPrincipal User principal) {
        try {
            authoritiesProcessService.revokeAuthority(service, authority, uuid);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Save the full user. No content sent back.
     * 
     * @param user
     *            The user to save.
     * @param principal
     *            The principal responsible for the request.
     * @throws BadRequestException 
     */
    @PostMapping(value="users/")
    public ResponseEntity<User> createUser(
            @RequestBody @Validated({ UserBean.CreationValidation.class }) UserBean userBean,
            @AuthenticationPrincipal User principal) throws BadRequestException {
//      @formatter:off
        Set<ServiceGrantedAuthorityBean> authoritiesBeans = userBean.getAuthorithies() == null ? Collections.emptySet() : userBean.getAuthorithies();
        Set<ServiceGrantedAuthority> authorities = authoritiesBeans
                .stream()
                .map(u -> ServiceGrantedAuthority
                        .builder()
                        .service(u.getService())
                        .grantedAuthority(new SimpleGrantedAuthority(u.getAuthority()))
                        .build()
                        )
                .collect(Collectors.toSet());
        User user = User.builder()
                .id(UUID.randomUUID())
                .version(0L)
                .username(userBean.getUsername())
                .password(userBean.getPassword())
                .email(userBean.getEmail())
                .enable(userBean.getEnabled() == null || userBean.getEnabled())
                .authorities(authorities)
                .build();
//      @formatter:on
        // save the user with password and authorities
        User savedUser;
        try {
            savedUser = userProcessService.saveUser(user);
        } catch (UsernameAlreadyExistingException e) {
            throw new BadRequestException(e);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).eTag("\"" + savedUser.getVersion() + "\"").build();
    }

    /**
     * Save the full user. No content sent back.
     * 
     * @param uuid
     *            The uuid of the user to save.
     * @param headers
     *            Http headers
     * @param userBean
     * @param principal
     *            The principal responsible for the request.
     * @throws BadRequestException 
     */
    @PutMapping("users/{uuid}")
    public ResponseEntity<User> fullySaveUser(@PathVariable(value = "uuid") UUID uuid,
            @RequestHeader HttpHeaders headers,
            @RequestBody @Validated({ UserBean.UpdateValidation.class }) UserBean userBean,
            @AuthenticationPrincipal User principal) throws BadRequestException {

        Long version = headers.getIfMatch().isEmpty() ? 0L : Long.valueOf(headers.getIfMatch().get(0));

//      @formatter:off
        Set<ServiceGrantedAuthorityBean> authoritiesBeans = userBean.getAuthorithies() == null ? Collections.emptySet() : userBean.getAuthorithies();
        Set<ServiceGrantedAuthority> authorities = authoritiesBeans
                .stream()
                .map(u -> ServiceGrantedAuthority
                        .builder()
                        .service(u.getService())
                        .grantedAuthority(new SimpleGrantedAuthority(u.getAuthority()))
                        .build()
                        )
                .collect(Collectors.toSet());
        
        User user = User.builder()
                .id(uuid)
                .version(version)
                .username(userBean.getUsername())
                .password(userBean.getPassword())
                .email(userBean.getEmail())
                .enable(userBean.getEnabled() == null || userBean.getEnabled())
                .authorities(authorities)
                .build();
//      @formatter:on
        // save the user with password and authorities
        try {
            User savedUser = userProcessService.saveUser(user);
            if (version == 0) {
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                return ResponseEntity.created(location).eTag("\"" + savedUser.getVersion() + "\"").build();
            }
        } catch (UsernameAlreadyExistingException e) {
            throw new BadRequestException(e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Save the user username. No content sent back.
     * 
     * @param uuid
     *            The uuid of the user to save.
     * @param userBean
     *            The bean containing the payload (the new username)
     * @return OK HTTP status if username is saved.
     */
    @PutMapping("users/{uuid}/username/")
    public ResponseEntity<User> saveUserUsername(@PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated(UserBean.PasswordChangeValidation.class) UserBean userBean) {
        try {
            userProcessService.saveUserUsername(uuid, userBean.getUsername());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Save the user password. No content sent back.
     * 
     * @param uuid
     *            The uuid of the user to save.
     * @param userBean
     *            The bean containing the payload (the new password)
     * @return OK HTTP status if password is saved.
     */
    @PutMapping("users/{uuid}/password/")
    public ResponseEntity<User> saveUserPassword(@PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated(UserBean.PasswordChangeValidation.class) UserBean userBean) {
        try {
            userProcessService.saveUserPassword(uuid, userBean.getPassword());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Save the user email. No content sent back.
     * 
     * @param uuid
     *            The uuid of the user to save.
     * @param userBean
     *            The bean containing the payload (the new email)
     * @return OK HTTP status if email is saved.
     */
    @PutMapping("users/{uuid}/email/")
    public ResponseEntity<User> saveUserEmail(@PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated(UserBean.EmailChangeValidation.class) UserBean userBean) {
        try {
            userProcessService.saveUserEmail(uuid, userBean.getEmail());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
    
    /**
     * Return the user with the given uuid.
     * 
     * @param uuid
     *            id of the targeted user
     * @return The user
     */
    @DeleteMapping("users/{uuid}")
    public ResponseEntity<User> deleteUser(@PathVariable("uuid") UUID uuid) {
        userProcessService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }

}
