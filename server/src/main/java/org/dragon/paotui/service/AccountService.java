package org.dragon.paotui.service;

import org.dragon.paotui.pojo.WechatAccount;

public interface AccountService extends BaseService<WechatAccount> {
    Integer addMoneyToNative(Integer money, Long id);

    void transferSystemNative(Long userId, Integer money);

    void transferUserNative(Long userId, Integer money);
}
