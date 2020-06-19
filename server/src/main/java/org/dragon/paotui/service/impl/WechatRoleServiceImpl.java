package org.dragon.paotui.service.impl;

import org.dragon.paotui.mapper.WechatRoleMapper;
import org.dragon.paotui.pojo.WechatRole;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.WechatRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WechatRoleServiceImpl extends AbstractService<WechatRole>  implements WechatRoleService {
    @Autowired
    WechatRoleMapper wechatRoleMapper;
    @Override
    public Integer insertRelationWechatUserAndRole(WechatUser wechatUser, WechatRole wechatRole) {
        //判断用户是否有该角色

        if(wechatUser.getWechatRoles().stream() != null &&
                !wechatUser.getWechatRoles().stream().reduce(true,(pre, after)->{
            return pre && after.getRoleName() != wechatRole.getRoleName();
        },(a,b)-> true)
        ){
            return 0 ;
        }else {
            wechatUser.getWechatRoles().add(wechatRole);
            return wechatRoleMapper.insertRelationWechatUserAndRole(wechatUser.getId(), wechatRole.getId());
        }

    }

    @Override
    public Optional<WechatRole> findWechatRoleByRoleName(String roleName) {
        return wechatRoleMapper.findWechatRoleByRoleName(roleName);
    }
}
