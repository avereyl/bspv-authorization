package org.bspv.authorization.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.authorization.process.UserProcessService;
import org.bspv.authorization.rest.controller.beans.ServiceGrantedAuthorityBean;
import org.bspv.authorization.rest.controller.beans.UserBean;
import org.bspv.commons.rest.controller.support.PaginationHelper;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController("/")
@RequestMapping(produces = "application/json")
public class UserController {
    
    @Autowired
    private UserProcessService userProcessService;

    /**
     */
    @GetMapping("")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * Return a page of Users.
     * <ul>
     * <li>GET /users/?enabled
     * <li>GET /users/?disabled
     * <li>GET /users/?username=avereyl
     * <li>GET /users/?username=avereyl&enabled
     * </ul>
     * 
     * @return Page of users.
     * TODO handle parameters (username, state...)
     */
    @GetMapping(value = "users/")
    public ResponseEntity<Page<User>> readUsers(
            @RequestParam(value = "range", required = false) String range,
            @RequestParam Map<String, String> queryMap,
            @AuthenticationPrincipal(errorOnInvalidType = true) User principal) {
        PageRequest pageRequest = PaginationHelper.rangeToPageRequest(range);
        Page<User> page = this.userProcessService.findUsers(pageRequest);
        return ResponseEntity.ok(page);
    }
    
    /**
     * Return the user with the given uuid.
     * 
     * @param uuid
     *            id of the targeted user
     * @return The user
     */
    @GetMapping("users/{uuid}")
    public ResponseEntity<User> readUser(@PathVariable("uuid") UUID uuid) {
        User user = this.userProcessService.findUser(uuid);
        return (user == null) ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Return the list of user authorities for the given user uuid.
     * 
     * @param uuid
     *            id of the targeted user
     * @return The list of authorities
     */
    @GetMapping("users/{uuid}/authorities/")
    public ResponseEntity<List<ServiceGrantedAuthority>> readUserAuthorities(
            @PathVariable(value = "uuid") UUID uuid,
            @RequestParam(value="service", required = false) String service,
            @RequestParam(value="authority", required = false) String authority,
            @AuthenticationPrincipal User principal
            ) {
        List<ServiceGrantedAuthority> authorities = this.userProcessService.findUserAuthorities(uuid);
        authorities = authorities
            .stream()
            .filter(a -> StringUtils.isBlank(service) || service.equals(a.getService()))
            .filter(a -> StringUtils.isBlank(authority) || authority.equals(a.getAuthority()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(authorities);
    }

    /**
     * Grant the given authority to the user.
     * (Duplicate authority is ignored)
     */
    @PostMapping("users/{uuid}/authorities/")
    public ResponseEntity<ServiceGrantedAuthority> grantAuthority(
            @PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated ServiceGrantedAuthorityBean authorityBean,
            @AuthenticationPrincipal User principal) {
        //TODO handle 201/200
        return ResponseEntity.created(URI.create("FIXME")).build();
    }
    /**
     *
     */
    @PutMapping("users/{uuid}/authorities/")
    public ResponseEntity<List<ServiceGrantedAuthority>> grantAuthorities(
            @PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated List<ServiceGrantedAuthorityBean> authorityBeans,
            @AuthenticationPrincipal User principal) {
        //TODO
        return ResponseEntity.created(URI.create("FIXME")).build();
    }
    
    /**
     *
     */
    @DeleteMapping("users/{uuid}/authorities/")
    public ResponseEntity<List<ServiceGrantedAuthority>> revokeAuthorities(
            @PathVariable(value = "uuid") UUID uuid,
            @RequestParam(value="service", required = false) String service,
            @RequestParam(value="authority", required = false) String authority,
            @AuthenticationPrincipal User principal) {
      //TODO
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Save the full user. No content sent back.
     * 
     * @param user
     *            The user to save.
     * @param principal
     *            The principal responsible for the request.
     */
    @PutMapping("users/{uuid}")
    public ResponseEntity<User> saveUser(
            @PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated UserBean userBean,
            @AuthenticationPrincipal User principal) {
        boolean isNew = userBean.getVersion() == null || userBean.getVersion() == 0L;
        if (!uuid.equals(UUID.fromString(userBean.getUuid()))) {
            return  ResponseEntity.badRequest().build();
        }
//      @formatter:off
        Set<ServiceGrantedAuthority> authorities = userBean
                .getAuthorithies()
                .stream()
                .map(u -> ServiceGrantedAuthority
                        .builder()
                        .service(u.getService())
                        .grantedAuthority(new SimpleGrantedAuthority(u.getAuthority()))
                        .build()
                        )
                .collect(Collectors.toSet());
        
        User user = User.builder()
                .id(UUID.fromString(userBean.getUuid()))
                .version(userBean.getVersion())
                .username(userBean.getUsername())
                .password(userBean.getPassword())
                .email(userBean.getEmail())
                .enable(userBean.isEnabled())
                .authorities(authorities)
                .build();
//      @formatter:on
        // save the user with password and authorities
        userProcessService.saveUser(user, principal);
        if (isNew) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.ok().build();
    }
    /**
     * Save the full user. No content sent back.
     * 
     * @param user
     *            The user to save.
     * @param principal
     *            The principal responsible for the request.
     */
    @PostMapping("users/")
    public ResponseEntity<User> createUser(
            @RequestBody @Validated UserBean userBean,
            @AuthenticationPrincipal User principal) {
        boolean isNew = userBean.getVersion() == null || userBean.getVersion() == 0L;
        if (!isNew) {
            return  ResponseEntity.badRequest().build();
        }
//      @formatter:off
        Set<ServiceGrantedAuthority> authorities = userBean
                .getAuthorithies()
                .stream()
                .map(u -> ServiceGrantedAuthority
                        .builder()
                        .service(u.getService())
                        .grantedAuthority(new SimpleGrantedAuthority(u.getAuthority()))
                        .build()
                        )
                .collect(Collectors.toSet());
        
        User user = User.builder()
                .id(UUID.fromString(userBean.getUuid()))
                .version(userBean.getVersion())
                .username(userBean.getUsername())
                .password(userBean.getPassword())
                .email(userBean.getEmail())
                .enable(userBean.isEnabled())
                .authorities(authorities)
                .build();
//      @formatter:on
        // save the user with password and authorities
        userProcessService.saveUser(user, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(userBean.getUuid()).toUri();
        return ResponseEntity.created(location).build();
    }


    @PatchMapping("users/{uuid}")
    public ResponseEntity<User> partiallySaveUser( 
            @PathVariable(value = "uuid") UUID uuid,
            @RequestBody @Validated UserBean userBean,
            @AuthenticationPrincipal User principal) {
        //TODO
        return ResponseEntity.ok().build();
    }
}
