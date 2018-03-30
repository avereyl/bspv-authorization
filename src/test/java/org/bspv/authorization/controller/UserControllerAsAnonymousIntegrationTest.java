package org.bspv.authorization.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

@SpringBootTest
public class UserControllerAsAnonymousIntegrationTest extends AbstractControllerIntegrationTest {

    @Test
    public void givenUsersURI_whenMockMVC_thenVerifyResponseForbidden() throws Exception {
        this.mockMvc.perform(get("/users/")).andExpect(status().isForbidden());
    }
   
}
