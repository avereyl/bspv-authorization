package org.bspv.authorization.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.bspv.authorization.BspvAuthorizationApplication;
import org.bspv.commons.config.listener.HsqldbServletContextListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;

@ContextConfiguration(classes = { BspvAuthorizationApplication.class })
public abstract class AbstractControllerIntegrationTest extends AbstractTestNGSpringContextTests
        implements InitializingBean, DisposableBean {

    protected static final String AUTH_COOKIE_NAME = "AuthorizationCookie";
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
    protected HsqldbServletContextListener hsqldbServletContextListener;

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

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

}
