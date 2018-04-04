package org.bspv.authorization.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class UserControllerAsAdminIntegrationTest extends AbstractControllerIntegrationTest {
    
    @Value("${bspv.security.fallback.username:admin}")
    protected String fallbackAdminUsername;
    @Value("${bspv.security.fallback.password}")
    protected String fallbackAdminPassword;
    
    /**
     * Cookie used for authentication of the admin.
     */
    protected Cookie adminAuthCookie = new Cookie(AUTH_COOKIE_NAME, "");

    @Test(priority=0)
    public void givenAdminCredentials_whenLogon_thenResponseOkAndJwtCookie() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> credentialsMap = new HashMap<>();
        credentialsMap.put("username", fallbackAdminUsername);
        credentialsMap.put("password", fallbackAdminPassword);
        String jsonifyCredentials = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentialsMap);
        // @formatter:off
        MvcResult result = this.mockMvc.perform(post("/login")
                .content(jsonifyCredentials))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(AUTH_COOKIE_NAME))
                .andExpect(cookie().secure(AUTH_COOKIE_NAME, true))
                .andReturn();
        // @formatter:on
        adminAuthCookie.setValue(result.getResponse().getCookie(AUTH_COOKIE_NAME).getValue());
    }
    
    @Test(priority=10)
    public void givenUsersURI_whenGet_thenVerifyResponseOK() throws Exception {
        this.mockMvc.perform(get("/users/")
                .cookie(adminAuthCookie)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
    }
    
    @Test(priority=20)
    public void givenUsersURI_whenPostGetDelete_thenVerifyResponseAreCreatedOkNoContent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> newUserMap = new HashMap<>();
        newUserMap.put("username", "user");
        newUserMap.put("password", "password");
        newUserMap.put("email", "user@bspv.org");
        String jsonifyUser = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newUserMap);
        
        MvcResult result = this.mockMvc.perform(post("/users/")
                .cookie(adminAuthCookie)
                .content(jsonifyUser)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",  notNullValue()))
                .andReturn();
        String newlyCreatedResourceURI = result.getResponse().getHeader("Location");
        String responseContent = this.mockMvc.perform(get(newlyCreatedResourceURI)
                .cookie(adminAuthCookie))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        JsonNode jsonUser = mapper.reader().readTree(responseContent);
        String username = jsonUser.findValuesAsText("username").get(0);
        assertThat(username).isEqualTo("user");
        String id = jsonUser.findValuesAsText("id").get(0);
        this.mockMvc.perform(delete("/users/"+id)
                .cookie(adminAuthCookie)
                )
                .andExpect(status().isNoContent());
    }
    
    
    
}
