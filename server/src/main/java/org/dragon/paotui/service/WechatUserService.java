package org.dragon.paotui.service;

import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface WechatUserService extends UserDetailsService, BaseService<WechatUser>{
    @Override
    WechatUser loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException;


    void updateWechatUser(WechatUser wechatUser);

    WechatUserDetail findWechatUserDetailByUserId(Long userId);

    void updateUserDetail(WechatUserDetail wechatUserDetail);

    void updateUserBackground(WechatUserDetail wechatUserDetail);

    WechatUser findWechatUserByUserId(Long userId);

    WechatUser enableUser(String username);

    void toggleLike(Long curUserId, Long toUserId);

    Boolean isLike(Long curUserId, Long toUserId);

    List<UserDetailResp> findLikedUserList(Long id);

    UserDetailResp findUserDetail(Long fromUserId);

    void reduceHiddenPoint(long userId);

    void reduceHiddenPoint(long userId, Integer point);
}
