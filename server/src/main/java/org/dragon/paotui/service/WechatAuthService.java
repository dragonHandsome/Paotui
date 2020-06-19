package org.dragon.paotui.service;

import org.dragon.paotui.payload.SignUpRequest;
import org.dragon.paotui.pojo.WechatUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public interface WechatAuthService {
    UsernamePasswordAuthenticationToken loginOrRegister(String code);

    void register(SignUpRequest signUpRequest);

    Boolean nonExistsUser(SignUpRequest signUpRequest);

    void insertRelation(Long userId, WechatUser wechatUser);

    void generateAccount(long userId);
}
