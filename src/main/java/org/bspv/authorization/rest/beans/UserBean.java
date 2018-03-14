package org.bspv.authorization.rest.beans;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class UserBean {

    public interface CreationValidation {
    }

    public interface UpdateValidation {
    }

    public interface PatchValidation {
    }

    /**
     * Username of this user.
     * Can be null (e.g. when partially updating a user for example)
     */
    @NotBlank(groups = { CreationValidation.class, UpdateValidation.class })
    private String username;

    /**
     * Password of the user.
     * Can be null (e.g. when partially updating a user for example)
     */
    @NotBlank(groups = { CreationValidation.class })
    private String password;

    /**
     * Status of the user.
     * Can be null (e.g. when partially updating a user for example)
     */
    private Boolean enabled;

    /**
     * Email of the user.
     */
    @NotBlank(groups = { CreationValidation.class, UpdateValidation.class })
    private String email;

    /**
     * Set of authorities.
     * Can be null (e.g. when partially updating a user for example)
     */
    private Set<ServiceGrantedAuthorityBean> authorithies;

}
