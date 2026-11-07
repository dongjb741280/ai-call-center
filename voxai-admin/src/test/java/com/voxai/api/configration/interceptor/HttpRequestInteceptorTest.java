package com.voxai.api.configration.interceptor;

import com.voxai.core.constant.Constant;
import com.voxai.core.entity.AdminUser;
import com.voxai.core.mapper.AdminUserMapper;
import com.voxai.core.mapper.CompanyMapper;
import com.voxai.core.po.AdminLoginResult;
import com.voxai.core.po.CompanyInfo;
import com.voxai.core.po.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpRequestInteceptorTest {
    @AfterEach void clearContext(){ SecurityContextHolder.clearContext(); }

    @Test void rejectsRequestWithoutCredentials() throws Exception {
        AdminUserMapper users=mock(AdminUserMapper.class); CompanyMapper companies=mock(CompanyMapper.class);
        RedisTemplate redis=mock(RedisTemplate.class);
        HttpRequestInteceptor interceptor=new HttpRequestInteceptor(users,companies,redis);
        MockHttpServletResponse response=new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(new MockHttpServletRequest(),response,new Object()));
        assertEquals(401,response.getStatus());
        verifyNoInteractions(users,companies,redis);
    }

    @Test void rejectsInvalidBasicPasswordAndClearsPreviousAuthentication() throws Exception {
        AdminUserMapper users=mock(AdminUserMapper.class); CompanyMapper companies=mock(CompanyMapper.class);
        HttpRequestInteceptor interceptor=new HttpRequestInteceptor(users,companies,mock(RedisTemplate.class));
        AdminUser user=new AdminUser(); user.setUsername("tenant"); user.setPasswd("$2a$10$invalidhash"); user.setStatus(1); user.setCompanyId(8L);
        when(users.adminLogin("tenant")).thenReturn(user);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader("Authorization","Basic "+Base64.getEncoder().encodeToString("tenant:wrong".getBytes("UTF-8")));
        MockHttpServletResponse response=new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request,response,new Object()));
        assertEquals(401,response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(companies);
    }

    @Test void acceptsOnlyLiveRedisSessionBoundToSameTenant() throws Exception {
        AdminUserMapper users=mock(AdminUserMapper.class); CompanyMapper companies=mock(CompanyMapper.class);
        RedisTemplate redis=mock(RedisTemplate.class); ValueOperations operations=mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(operations);
        AdminLoginResult session=new AdminLoginResult(); UserInfo info=new UserInfo(); info.setUsername("tenant"); session.setUserInfo(info);
        when(operations.get(Constant.ADMIN_TOKEN+"token")).thenReturn(session);
        when(operations.get(Constant.ADMIN_INFO+"token")).thenReturn("12:8");
        AdminUser user=new AdminUser(); user.setId(12L); user.setUsername("tenant"); user.setStatus(1); user.setCompanyId(8L);
        when(users.adminLogin("tenant")).thenReturn(user);
        CompanyInfo company=new CompanyInfo(); company.setId(8L); company.setStatus(1);
        when(companies.selectById(8L)).thenReturn(company);
        HttpRequestInteceptor interceptor=new HttpRequestInteceptor(users,companies,redis);
        MockHttpServletRequest request=new MockHttpServletRequest(); request.addHeader("token","token");
        assertTrue(interceptor.preHandle(request,new MockHttpServletResponse(),new Object()));
        assertEquals(8L,((com.voxai.core.po.AdminAccountInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getBindCompanyId());
    }
}
