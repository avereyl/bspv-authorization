package org.bspv.authorization.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.bspv.authorization.BspvAuthorizationApplication;
import org.bspv.authorization.rest.controller.UserController;
import org.bspv.commons.config.listener.HsqldbServletContextListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@ContextConfiguration(classes = { BspvAuthorizationApplication.class })
public class UserControllerIntegrationTest extends AbstractTestNGSpringContextTests
        implements InitializingBean, DisposableBean {

    private static final String AUTH_COOKIE_NAME = "AuthorizationCookie";
    /**
     * The {@link MockServletContext} does not support listener registration, so the
     * HSQLDB cannot be started automatically using the
     * {@link HsqldbServletContextListener}.
     * 
     * Then we inject this listener and call start and stop in the method
     * implemented from {@link InitializingBean} and {@link DisposableBean} as they
     * are called before/after Junit annotation with same name ;-).
     */
    @Autowired
    private HsqldbServletContextListener hsqldbServletContextListener;

    @Autowired
    private WebApplicationContext wac;

    @Value("${bspv.security.fallback.username:admin}")
    private String fallbackAdminUsername;
    @Value("${bspv.security.fallback.password}")
    private String fallbackAdminPassword;
    
    private MockMvc mockMvc;
    
    /**
     * Cookie used for authentication of the admin.
     */
    private Cookie adminAuthCookie = new Cookie(AUTH_COOKIE_NAME, "");

    @Override
    public void afterPropertiesSet() throws Exception {
        hsqldbServletContextListener.contextInitialized(null);
    }
    
    @Override
    public void destroy() throws Exception {
        hsqldbServletContextListener.contextDestroyed(null);
    }
    
    @BeforeClass
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }

    @Test(priority=0)
    public void givenWac_whenServletContext_thenItProvidesUserController() {
        ServletContext servletContext = wac.getServletContext();
        assertThat(servletContext).isNotNull();
        assertThat(servletContext).isInstanceOf(MockServletContext.class);
        assertThat(wac.getBean(UserController.class)).isNotNull();
    }
    
    @Test(groups="anonymous")
    public void givenUsersURI_whenMockMVC_thenVerifyResponseForbidden() throws Exception {
        this.mockMvc.perform(get("/users/")).andExpect(status().isForbidden());
    }
    
    @Test(groups="adminLogon", dependsOnGroups="anonymous")
    public void givenLoginURIadmin_whenMockMVC_thenResponseOkAndJwtCookie() throws Exception {
        // @formatter:off
        MvcResult result = this.mockMvc.perform(post("/login")
                .content("{\"username\":\"" + fallbackAdminUsername + "\", \"password\":\""+fallbackAdminPassword+"\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(AUTH_COOKIE_NAME))
                .andExpect(cookie().secure(AUTH_COOKIE_NAME, true))
                .andReturn();
        // @formatter:on
        adminAuthCookie.setValue(result.getResponse().getCookie(AUTH_COOKIE_NAME).getValue());
    }
    
    @Test(groups="loggedAdmin", dependsOnGroups="adminLogon")
    public void givenUsersURI_whenMockMVC_thenVerifyResponseOK() throws Exception {
        this.mockMvc.perform(get("/users/").cookie(adminAuthCookie)).andExpect(status().isOk());
    }
}
