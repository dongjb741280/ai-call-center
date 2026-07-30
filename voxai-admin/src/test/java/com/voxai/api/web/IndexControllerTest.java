package com.voxai.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxai.api.service.AdminService;
import com.voxai.core.po.AdminLoginResult;
import com.voxai.core.po.UserInfo;
import com.voxai.core.vo.AdminLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        IndexController controller = new IndexController();
        ReflectionTestUtils.setField(controller, "adminService", adminService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AdminLoginResult result = new AdminLoginResult();
        result.setToken("test-token-123");
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("admin");
        result.setUserInfo(userInfo);
        when(adminService.login(any(AdminLogin.class))).thenReturn(result);

        AdminLogin login = new AdminLogin();
        login.setUsername("admin");
        login.setPasswd("password");

        mockMvc.perform(post("/index/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("test-token-123"))
                .andExpect(jsonPath("$.data.userInfo.username").value("admin"));
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        when(adminService.logout(eq("token-abc"))).thenReturn(true);

        mockMvc.perform(get("/index/logout").param("token", "token-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void shouldReturnHealthUp() throws Exception {
        mockMvc.perform(get("/index/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldReturnEchoForIndexPost() throws Exception {
        mockMvc.perform(post("/index/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"hello\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
