package com.voxai.api.service.impl;

import com.voxai.api.exception.BusinessException;
import com.voxai.api.util.BcryptUtil;
import com.voxai.core.constant.Constant;
import com.voxai.core.entity.AdminUser;
import com.voxai.core.entity.AiEngine;
import com.voxai.core.entity.BlackPhone;
import com.voxai.core.entity.Station;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.*;
import com.voxai.core.po.AdminLoginResult;
import com.voxai.core.po.CompanyInfo;
import com.voxai.core.vo.AdminLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private AdminMenuMapper adminMenuMapper;
    @Mock
    private AdminRoleMapper adminRoleMapper;
    @Mock
    private AdminUserRoleMapper adminUserRoleMapper;
    @Mock
    private SipGatewayMapper sipGatewayMapper;
    @Mock
    private PhoneAreaMapper phoneAreaMapper;
    @Mock
    private BlackPhoneMapper blackPhoneMapper;
    @Mock
    private StationMapper stationMapper;
    @Mock
    private AiEngineMapper aiEngineMapper;
    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    private AdminServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminServiceImpl();
        ReflectionTestUtils.setField(service, "adminUserMapper", adminUserMapper);
        ReflectionTestUtils.setField(service, "adminMenuMapper", adminMenuMapper);
        ReflectionTestUtils.setField(service, "adminRoleMapper", adminRoleMapper);
        ReflectionTestUtils.setField(service, "adminUserRoleMapper", adminUserRoleMapper);
        ReflectionTestUtils.setField(service, "sipGatewayMapper", sipGatewayMapper);
        ReflectionTestUtils.setField(service, "phoneAreaMapper", phoneAreaMapper);
        ReflectionTestUtils.setField(service, "blackPhoneMapper", blackPhoneMapper);
        ReflectionTestUtils.setField(service, "stationMapper", stationMapper);
        ReflectionTestUtils.setField(service, "aiEngineMapper", aiEngineMapper);
        ReflectionTestUtils.setField(service, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(service, "companyMapper", companyMapper);
        ReflectionTestUtils.setField(service, "ossServer", "http://oss.example.com");
    }

    // ---- login ----

    @Test
    void shouldFailLoginWhenUserNotFound() {
        when(adminUserMapper.adminLogin("nobody")).thenReturn(null);

        AdminLogin login = new AdminLogin();
        login.setUsername("nobody");
        login.setPasswd("any");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(login));
        assertEquals(ErrorCode.ACCOUNT_ERROR.getCode(), ex.getErrorCode());
    }

    @Test
    void shouldFailLoginWhenAccountDisabled() {
        AdminUser user = new AdminUser();
        user.setId(1L);
        user.setUsername("disabled");
        user.setStatus(0);
        when(adminUserMapper.adminLogin("disabled")).thenReturn(user);

        AdminLogin login = new AdminLogin();
        login.setUsername("disabled");
        login.setPasswd("any");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(login));
        assertEquals(ErrorCode.ACCOUNT_DISABLED.getCode(), ex.getErrorCode());
    }

    @Test
    void shouldFailLoginWhenPasswordWrong() {
        String correctHash = BcryptUtil.encrypt("correct-password");
        AdminUser user = new AdminUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswd(correctHash);
        user.setStatus(1);
        when(adminUserMapper.adminLogin("admin")).thenReturn(user);

        AdminLogin login = new AdminLogin();
        login.setUsername("admin");
        login.setPasswd("wrong-password");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(login));
        assertEquals(ErrorCode.ACCOUNT_ERROR.getCode(), ex.getErrorCode());
    }

    @Test
    void shouldFailLoginWhenCompanyNotAvailable() {
        String hash = BcryptUtil.encrypt("password");
        AdminUser user = new AdminUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswd(hash);
        user.setStatus(1);
        user.setCompanyId(999L);
        when(adminUserMapper.adminLogin("admin")).thenReturn(user);
        when(companyMapper.selectById(999L)).thenReturn(null);

        AdminLogin login = new AdminLogin();
        login.setUsername("admin");
        login.setPasswd("password");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(login));
        assertEquals(ErrorCode.COMPANY_NOT_AVALIABLE.getCode(), ex.getErrorCode());
    }

    @Test
    void shouldLoginSuccessfully() {
        String hash = BcryptUtil.encrypt("password");
        AdminUser user = new AdminUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswd(hash);
        user.setStatus(1);
        user.setCompanyId(100L);
        user.setAvatar("/avatars/admin.png");
        when(adminUserMapper.adminLogin("admin")).thenReturn(user);

        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setId(100L);
        companyInfo.setSecretKey("secret-key-for-test");
        companyInfo.setName("Test Company");
        companyInfo.setCompanyCode("TC001");
        companyInfo.setGmt(8);
        when(companyMapper.selectById(100L)).thenReturn(companyInfo);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(adminMenuMapper.selectUserMenus(anyMap())).thenReturn(new ArrayList<>());

        AdminLogin login = new AdminLogin();
        login.setUsername("admin");
        login.setPasswd("password");
        login.setClientIp("192.168.1.1");

        AdminLoginResult result = service.login(login);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getCts());
        assertNotNull(result.getUserInfo());
        assertEquals("admin", result.getUserInfo().getUsername());
        assertEquals("Test Company", result.getUserInfo().getCompanyName());
        assertEquals("TC001", result.getUserInfo().getCompanyCode());
        assertEquals(Integer.valueOf(8), result.getUserInfo().getGmt());
        assertEquals("http://oss.example.com", result.getUserInfo().getOssServer());
        assertEquals("192.168.1.1", result.getUserInfo().getClientIp());
        assertEquals("/avatars/admin.png", result.getUserInfo().getAvatar());

        verify(redisTemplate.opsForValue()).set(startsWith(Constant.ADMIN_TOKEN), eq(result));
    }

    // ---- logout ----

    @Test
    void shouldLogoutSuccessfully() {
        when(redisTemplate.delete("token:my-token")).thenReturn(true);

        Boolean result = service.logout("my-token");
        assertTrue(result);
    }

    // ---- saveBlackPhone ----

    @Test
    void shouldInsertNewBlackPhone() {
        BlackPhone phone = new BlackPhone();
        phone.setNumPrefix("13800138000");

        service.saveBlackPhone(phone);

        assertNotNull(phone.getCts());
        assertNotNull(phone.getUts());
        assertEquals(Integer.valueOf(2), phone.getStatus());
        verify(blackPhoneMapper).insertSelective(phone);
    }

    @Test
    void shouldUpdateExistingBlackPhone() {
        BlackPhone phone = new BlackPhone();
        phone.setId(1L);
        phone.setNumPrefix("13800138001");

        service.saveBlackPhone(phone);

        assertNotNull(phone.getUts());
        verify(blackPhoneMapper).updateByPrimaryKeySelective(phone);
        verify(blackPhoneMapper, never()).insertSelective(any());
    }

    // ---- deleteBlackPhone ----

    @Test
    void shouldDeleteBlackPhone() {
        service.deleteBlackPhone(1L);
        verify(blackPhoneMapper).deleteByPrimaryKey(1L);
    }

    // ---- saveStation ----

    @Test
    void shouldInsertNewStation() {
        Station station = new Station();
        station.setAppName("fs-node-1");

        service.saveStation(station);

        assertNotNull(station.getCts());
        assertEquals(Integer.valueOf(2), station.getStatus());
        verify(stationMapper).insertSelective(station);
    }

    @Test
    void shouldUpdateExistingStation() {
        Station station = new Station();
        station.setId(5L);
        station.setAppName("fs-node-2");

        service.saveStation(station);

        verify(stationMapper).updateByPrimaryKeySelective(station);
    }

    // ---- deleteStation ----

    @Test
    void shouldDeleteStation() {
        service.deleteStation(5L);
        verify(stationMapper).deleteByPrimaryKey(5L);
    }

    // ---- saveEngine ----

    @Test
    void shouldInsertNewEngine() {
        AiEngine engine = new AiEngine();
        engine.setName("asr-engine");

        service.saveEngine(engine);

        assertNotNull(engine.getCts());
        assertEquals(Integer.valueOf(2), engine.getStatus());
        verify(aiEngineMapper).insertSelective(engine);
    }

    // ---- deleteEngine ----

    @Test
    void shouldDeleteEngine() {
        service.deleteEngine(3L);
        verify(aiEngineMapper).deleteByPrimaryKey(3L);
    }
}
