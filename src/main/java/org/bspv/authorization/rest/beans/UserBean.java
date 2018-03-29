package org.bspv.authorization.rest.beans;

import java.util.Set;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class UserBean {

    /**
     * Validation group interface for user creation.
     *
     */
    public interface CreationValidation {
    }

    /**
     * Validation group interface for user update.
     *
     */
    public interface UpdateValidation {
    }

    /**
     * Validation group interface for user's password change.
     *
     */
    public interface PasswordChangeValidation {
    }

    /**
     * Validation group interface for user's email change.
     *
     */
    public interface EmailChangeValidation {
    }

    /**
     * Validation group interface for user's name change.
     *
     */
    public interface UsernameChangeValidation {
    }

    /**
     * Username of this user. Can be null (e.g. when partially updating a user for
     * example)
     */
    @NotBlank(groups = { CreationValidation.class, UpdateValidation.class, UsernameChangeValidation.class })
    private String username;

    /**
     * Password of the user. Can be null (e.g. when partially updating a user for
     * example)
     */
    @NotBlank(groups = { CreationValidation.class, PasswordChangeValidation.class })
    @Length(min = 8, groups = { CreationValidation.class, PasswordChangeValidation.class })
    private String password;

    /**
     * Status of the user. Can be null (e.g. when partially updating a user for
     * example)
     */
    private Boolean enabled;

    /**
     * Email of the user.
     */
    @NotBlank(groups = { CreationValidation.class, UpdateValidation.class, EmailChangeValidation.class })
    @Email(groups = { CreationValidation.class, UpdateValidation.class, EmailChangeValidation.class })
    private String email;

    /**
     * Set of authorities. Can be null (e.g. when partially updating a user for
     * example)
     */
    private Set<ServiceGrantedAuthorityBean> authorithies;

}
