package org.bspv.authorization.config.security;

public class ServiceAuthoritiesWrapper {

    private final String service;
    private final String[] authorities;
    
    public ServiceAuthoritiesWrapper(String service, String[] authorities) {
        super();
        this.service = service;
        this.authorities = authorities;
    }

    public String getService() {
        return service;
    }

    public String[] getAuthorities() {
        return authorities;
    }
    
}
