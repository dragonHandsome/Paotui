package org.dragon.paotui.controller;

import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.*;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.security.JwtTokenProvider;
import org.dragon.paotui.service.AdminService;
import org.dragon.paotui.service.WechatAuthService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    WechatAuthService wechatAuthService;
    @Autowired
    AdminService adminService;

    @PostMapping("/auth/signin")
    public ViewData<?> signin(@RequestBody Admin a){
        try{
            Admin admin = adminService.loadUserByUsername(a.getUsername());
            if(admin != null && passwordEncoder.matches(a.getPassword(),admin.getPassword())){
                String token = jwtTokenProvider.generateToken(a.getUsername());
                return ViewData.ok(JwtAuthenticationResponse.take(token));
            }else MyLogUtil.error("用户名密码错误");
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
        return ViewData.error("用户名密码错误");
    }
    @RequestMapping(value = "/auth")
    public ViewData<?> login(@RequestBody  WechatLoginRequest wechatLoginRequest){
        String code = wechatLoginRequest.getCode();
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    wechatAuthService.loginOrRegister(code);
            return ViewData.ok(setAuthenticationInfoAndReturnToken(authenticationToken));
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(ErrorResp.SIGN_UP_ERROR);
        }
    }

    @RequestMapping("/signin")
    public ViewData<?> login(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            return ViewData.error("用户名密码错误");
        }
        return ViewData.ok(setAuthenticationInfoAndReturnToken(authentication));
    }

    private JwtAuthenticationResponse setAuthenticationInfoAndReturnToken(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        WechatUser principal = (WechatUser) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(principal.getUsername());
        return JwtAuthenticationResponse.take(token);
    }

    @RequestMapping("/signup")
    //authentication.principal = anonymousUser
    @Transactional
    public ViewData<?> register(@RequestBody SignUpRequest signUpRequest){
        Authentication authentication;
        //Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        try {
            wechatAuthService.register(signUpRequest);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error("用户名或邮箱已存在");
        }
        return ViewData.ok("请留意验证您的邮箱。");
    }
}
