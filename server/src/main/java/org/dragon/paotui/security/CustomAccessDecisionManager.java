package org.dragon.paotui.security;

import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.pojo.Role;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : configAttributes) {
            if(authentication.getPrincipal() instanceof Admin) {
                Admin admin = (Admin)authentication.getPrincipal();
                //假如是我或者最高权限管理员 直接放行
                if("long".equals(admin.getUsername()) || "admin".equals(admin.getUsername())) {
                    return;
                }
            }
            if( "ROLE_ALL".equals(configAttribute.getAttribute()) ){
                return;
            }
            //判断该URL是否需要角色 在customFilter中过滤得到全部url可访问角色
            if("ROLE_UN_EXIST".equals(configAttribute.getAttribute())) {
                //如果是匿名用户
                if(authentication instanceof AnonymousAuthenticationToken) {
                    throw new AccessDeniedException("未登录");
                }
                //URL只需认证就可访问
                else {
                    return;
                }
            }
            //判断当前用户是否具有 URL 所需角色 没有则抛出异常
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if(configAttribute.getAttribute().equals(authority.getAuthority())){
                    //只要有对应角色就放行
                    return;
                }
            }
        }
        //否则没权限
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
