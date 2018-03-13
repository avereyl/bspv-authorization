package org.bspv.authorization.rest.controller.beans;

import java.util.HashSet;
import java.util.Set;

import org.bspv.commons.rest.controller.beans.validation.ValidUUID;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class UserBean {

    /**
     * UUID of this user.
     */
    @ValidUUID
    private String uuid;
    
    /**
     * Username of this user.
     */
    @NotBlank
    private String username;
    
    /**
     * Version of the user.
     */
    private Long version = 0L;
    
    /**
     * Password of the user.
     */
    private String password;
   
    /**
     * Status of the user.
     */
    private boolean enabled = true;
    
    /**
     * Email of the user.
     */
    @NotBlank
    private String email;
    
    /**
     * Set of authorities.
     */
    private Set<ServiceGrantedAuthorityBean> authorithies = new HashSet<>();

}
