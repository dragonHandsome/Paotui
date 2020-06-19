package org.dragon.paotui.security;

import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.pojo.Role;
import org.dragon.paotui.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

@Component
public class CustomFilterInvocationSecurityMetaDataFilter implements FilterInvocationSecurityMetadataSource {
    @Autowired
    private PermissionService permissionService;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取用户请求url
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        //过滤白名单
        if(antMatcherSome(requestUrl)) {
            return SecurityConfig.createList("ROLE_ALL");
        }
        List<Permission> permissions = permissionService.getAll();
        for (Permission permission : permissions) {
            //如果路径匹配
            if(antPathMatcher.match(permission.getPath(), requestUrl)){
                List<String> roles = permission.getRoles();
                //构造该url所需的角色数据
                String[] rolesStr = new String[roles.size()];
                for (int i = 0; i < roles.size(); i++) {
                    rolesStr[i] = roles.get(i);
//                    rolesStr[i] = "ROLE_" + roles.get(i).getRoleName();
                }
                return SecurityConfig.createList(rolesStr);
            }
        }
        //没角色匹配 就默认角色
        return SecurityConfig.createList("ROLE_UN_EXIST");
    }

    private boolean antMatcherSome(String requestUrl) {
        //测试
        String[] permitAlls = new String[]{
                "/",
                "/static/**",
                "/signup",
                "/signin",
                "/auth/signin",
                "/enableUser",
                "/favicon.ico",
                "/uploads/**",
                "/templates/**",
                "/**/*.jpg",
                "/**/*.png",
                "/**/*.js",
                "/**/*.css",
                "/**/*.html",
                "/auth**"
        };
        for (String permitAll : permitAlls) {
            if(antPathMatcher.match(permitAll, requestUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
