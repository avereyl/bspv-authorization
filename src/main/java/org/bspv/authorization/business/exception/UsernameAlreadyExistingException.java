package org.bspv.authorization.business.exception;

public class UsernameAlreadyExistingException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2557650247272384474L;

    public UsernameAlreadyExistingException() {
    }

    public UsernameAlreadyExistingException(String message) {
        super(message);
    }

    public UsernameAlreadyExistingException(Throwable cause) {
        super(cause);
    }

    public UsernameAlreadyExistingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyExistingException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
