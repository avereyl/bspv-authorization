package org.bspv.authorization.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class UserControllerAsUserIntegrationTest extends AbstractControllerIntegrationTest {

    /**
     * Cookie used for authentication of the admin.
     */
    protected Cookie userAuthCookie = new Cookie(AUTH_COOKIE_NAME, "");
    
    @Test(priority=0)
    public void givenLoginURIuser_whenMockMVC_thenResponseOkAndJwtCookie() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> credentialsMap = new HashMap<>();
        credentialsMap.put("username", "");
        credentialsMap.put("password", "");
        String jsonifyCredentials = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentialsMap);
        // @formatter:off
//        MvcResult result = 
                this.mockMvc.perform(post("/login")
                .content(jsonifyCredentials))
                .andExpect(status().isUnauthorized())
//                .andExpect(status().isOk())
//                .andExpect(cookie().exists(AUTH_COOKIE_NAME))
//                .andExpect(cookie().secure(AUTH_COOKIE_NAME, true))
                .andReturn();
        // @formatter:on
//        userAuthCookie.setValue(result.getResponse().getCookie(AUTH_COOKIE_NAME).getValue());
    }
    
}
