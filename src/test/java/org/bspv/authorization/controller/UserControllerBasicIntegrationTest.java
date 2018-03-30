package org.bspv.authorization.controller;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.ServletContext;

import org.bspv.authorization.rest.controller.UserController;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.testng.annotations.Test;

@SpringBootTest
public class UserControllerBasicIntegrationTest extends AbstractControllerIntegrationTest {

    @Test(priority=0)
    public void givenWac_whenServletContext_thenItProvidesUserController() {
        ServletContext servletContext = wac.getServletContext();
        assertThat(servletContext).isNotNull();
        assertThat(servletContext).isInstanceOf(MockServletContext.class);
        assertThat(wac.getBean(UserController.class)).isNotNull();
    }
    
}
