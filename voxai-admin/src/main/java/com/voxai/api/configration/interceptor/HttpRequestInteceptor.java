package com.voxai.api.configration.interceptor;

import com.alibaba.fastjson.JSON;
import com.voxai.api.util.BcryptUtil;
import com.voxai.core.constant.Constant;
import com.voxai.core.entity.AdminUser;
import com.voxai.core.mapper.AdminUserMapper;
import com.voxai.core.mapper.CompanyMapper;
import com.voxai.core.po.AdminAccountInfo;
import com.voxai.core.po.AdminLoginResult;
import com.voxai.core.po.CompanyInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

/** Credentials are validated on every request; no development identity fallback. */
public class HttpRequestInteceptor implements HandlerInterceptor {
    private final AdminUserMapper users;
    private final CompanyMapper companies;
    private final RedisTemplate redis;

    public HttpRequestInteceptor(AdminUserMapper users, CompanyMapper companies, RedisTemplate redis) {
        this.users = users; this.companies = companies; this.redis = redis;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContextHolder.clearContext();
        AdminUser user = null;
        try {
            String authorization = request.getHeader("Authorization");
            String token = request.getHeader("token");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7).trim();
            }
            if (token != null && !token.trim().isEmpty()) {
                Object cached = redis.opsForValue().get(Constant.ADMIN_TOKEN + token);
                if (cached != null) {
                    AdminLoginResult session = cached instanceof AdminLoginResult ? (AdminLoginResult) cached
                            : JSON.parseObject(JSON.toJSONString(cached), AdminLoginResult.class);
                    if (session.getUserInfo() != null) {
                        user = users.adminLogin(session.getUserInfo().getUsername());
                        // The session belongs to the user and tenant that originally logged in.
                        Object identity = redis.opsForValue().get(Constant.ADMIN_INFO + token);
                        if (identity == null || user == null || !identity.toString().equals(user.getId() + ":" + user.getCompanyId())) {
                            user = null;
                        }
                    }
                }
            } else if (authorization != null && authorization.startsWith("Basic ")) {
                String credentials = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8);
                int separator = credentials.indexOf(':');
                if (separator > 0) {
                    user = users.adminLogin(credentials.substring(0, separator));
                    if (user != null && !BcryptUtil.checkPwd(credentials.substring(separator + 1), user.getPasswd())) { user = null; }
                }
            }
            if (user != null && Integer.valueOf(1).equals(user.getStatus()) && user.getCompanyId() != null) {
                CompanyInfo company = companies.selectById(user.getCompanyId());
                if (company != null && Integer.valueOf(1).equals(company.getStatus())) {
                    AdminAccountInfo account = new AdminAccountInfo();
                    BeanUtils.copyProperties(user, account);
                    account.setPasswd(null);
                    account.setBindCompanyId(user.getCompanyId());
                    SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(account, null, Collections.emptyList()));
                    return true;
                }
            }
        } catch (RuntimeException ignored) {
            // Malformed credentials and unavailable session stores fail closed; never log credentials.
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(java.util.Collections.singletonMap("code", 401)));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        SecurityContextHolder.clearContext();
    }
}
