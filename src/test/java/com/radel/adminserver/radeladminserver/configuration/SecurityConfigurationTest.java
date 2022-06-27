package com.radel.adminserver.radeladminserver.configuration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("default")
class SecurityConfigurationTest {

    public SecurityConfigurationTest(WebApplicationContext wac,
                                     @Value("${spring.security.user.name}") String adminUsername,
                                     @Value("${spring.security.user.password}") String adminPassword) {
        this.wac = wac;
        this.adminUserName = adminUsername;
        this.adminPassword = adminPassword;
    }

    private final String adminUserName;

    private final String adminPassword;

    WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void adminIsSupposedToHaveAccessToApp() throws Exception {

        mockMvc.perform(get("/admin").with(httpBasic(this.adminUserName, this.adminPassword)))
                .andExpect(status().isOk());
    }

    @Test
    void notAdminIsNotSupposedToHaveAccessToApp() throws Exception {
        mockMvc.perform(get("/admin").with(httpBasic(this.adminUserName + "123", this.adminPassword + "123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userWithoutBasicAuthIsNotSupposedToHaveAccess() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userWithBasicAuthIsSupposedToHaveAccessToLoginPage() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk());
    }

}
