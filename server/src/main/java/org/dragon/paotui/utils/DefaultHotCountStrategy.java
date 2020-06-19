package org.dragon.paotui.utils;

import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultHotCountStrategy implements HotCountStrategy{
    @Autowired
    WechatUserService userService;
    @Override
    public Integer getOutCome(Integer score, Integer count, Long userId) {
        WechatUser byId = userService.findById(userId);
        return score * count * byId.getHiddenPoint() / 1000;
    }
}
