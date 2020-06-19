package org.dragon.paotui.service.impl;

import org.dragon.paotui.enumeration.AccountType;
import org.dragon.paotui.mapper.AccountMapper;
import org.dragon.paotui.pojo.WechatAccount;
import org.dragon.paotui.service.AccountService;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl extends AbstractService<WechatAccount> implements AccountService {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    NotifyFactory notifyFactory;
    public Integer addMoneyToNative(Integer money, Long id) {
        final WechatAccount condition = WechatAccount.builder()
                .type(AccountType.NATIVE)
                .userId(id)
                .build();
        final WechatAccount account = findOneByEntity(condition);
        money += account.getCredit();
        final WechatAccount build = WechatAccount.builder()
                .id(account.getId())
                .credit(money)
                .build();
        update(build);
        createAddMoneyNotify(money, id);
        return money;
    }

    private void createAddMoneyNotify(Integer money, Long id) {
        notifyFactory.init(0l, id);
        notifyFactory.createAddMoneyNotify(money.longValue());
    }

    WechatAccount generateUserNativeAccountCondition(Long userId){
        return WechatAccount.builder()
                .type(AccountType.NATIVE)
                .userId(userId)
                .build();
    }
    WechatAccount getSystemNativeAccountCondition(){
        return generateUserNativeAccountCondition(0l);
    }

    @Override
    @Transactional
    public void transferSystemNative(Long userId, Integer money) {
        if(money <= 0) return;;
        final WechatAccount userCondition = generateUserNativeAccountCondition(userId);
        final WechatAccount userAccount = findOneByEntity(userCondition);
        if(userAccount.getCredit() < money)
            MyLogUtil.error("钱不够");
        final WechatAccount systemNativeAccountCondition = getSystemNativeAccountCondition();
        final WechatAccount sysAccount = findOneByEntity(systemNativeAccountCondition);
        userAccount.setCredit(userAccount.getCredit() - money);
        sysAccount.setCredit(sysAccount.getCredit() + money);
        updateByEntity(userAccount, userCondition);
        updateByEntity(sysAccount, systemNativeAccountCondition);
    }

    @Override
    public void transferUserNative(Long userId, Integer money) {
        if(money <= 0) return;;
        final WechatAccount systemNativeAccountCondition = getSystemNativeAccountCondition();
        final WechatAccount sysAccount = findOneByEntity(systemNativeAccountCondition);
        if(sysAccount.getCredit() < money)
            MyLogUtil.error("系统金额异常！！系统金额不可能不足");
        final WechatAccount userCondition = generateUserNativeAccountCondition(userId);
        final WechatAccount userAccount = findOneByEntity(userCondition);
        userAccount.setCredit(userAccount.getCredit() + money);
        sysAccount.setCredit(sysAccount.getCredit() - money);
        updateByEntity(userAccount, userCondition);
        updateByEntity(sysAccount, systemNativeAccountCondition);
    }
}
