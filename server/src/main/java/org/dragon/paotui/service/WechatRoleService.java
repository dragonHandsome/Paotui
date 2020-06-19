package org.dragon.paotui.service;

import org.dragon.paotui.pojo.WechatRole;
import org.dragon.paotui.pojo.WechatUser;

import java.util.Optional;

public interface WechatRoleService {
    Integer insertRelationWechatUserAndRole(WechatUser wechatUser, WechatRole wechatRole);

    Optional<WechatRole> findWechatRoleByRoleName(String roleName);
}
