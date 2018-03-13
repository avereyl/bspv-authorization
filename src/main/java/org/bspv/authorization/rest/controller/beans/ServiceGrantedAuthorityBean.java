package org.bspv.authorization.rest.controller.beans;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ServiceGrantedAuthorityBean {

    @NotNull
    private String service;
    @NotNull
    private String authority;

}
