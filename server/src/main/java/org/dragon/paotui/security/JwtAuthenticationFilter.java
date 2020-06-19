package org.dragon.paotui.security;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.AdminService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WechatUserService userService;

    @Autowired
    AdminService adminService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            //提取jwt
            String jwt = getJwtFromRequest(request);
            //验证jwt
            if (StringUtils.hasText(jwt) ){
                if(jwtTokenProvider.validateToken(jwt)){
                    //设置为已认证 给上下文设置身份认证信息
                    UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request, jwt);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else {
                    new ExceptionHandlerResp().send(ErrorResp.TOKEN_ERROR, response);
                    return;
                }
            }
        }catch (RuntimeException e){
            WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
            log.warn("ip: {} 验证失败", details.getRemoteAddress());
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request, String jwt){
        UsernamePasswordAuthenticationToken authenticationToken = null;
        //如果是管理后台
        String username = jwtTokenProvider.getIdentificationFromJwt(jwt);
        try{
            if(
                    "admin".equals(request.getParameter("userType")) ||
                    request.getRequestURL().toString().contains("admin")){
                Admin admin = adminService.loadUserByUsername(username);
                authenticationToken =
                        new UsernamePasswordAuthenticationToken(admin, null,
                                admin.getRoles());
            }else{
                WechatUser wechatUser = userService.loadUserByUsername(username);
                //生成认证信息
                authenticationToken =
                        new UsernamePasswordAuthenticationToken(wechatUser, null,
                                wechatUser.getWechatRoles());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.warn("loadUserByUsername失败");
            throw new RuntimeException("loadUserByUsername失败");
        }
        //配置用户ip信息等
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)){
            return request.getParameter("token");
        }
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
