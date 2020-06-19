package org.dragon.paotui.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.WechatAccount;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;

@RestController
@RequestMapping("account")
public class AccountController {
    @Autowired
    AccountServiceImpl accountService;
    @Setter
    @Getter
    @NoArgsConstructor
    static public class Credit{
        Integer money;
    }
    @RequestMapping(method = RequestMethod.POST)
    public ViewData<?> addMoneyToNative(@RequestBody Credit credit, @CurrentUser WechatUser user){
        try{
            Integer money = accountService.addMoneyToNative(credit.getMoney(), user.getId());
            return ViewData.ok(money);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.ADD_MONEY_ERROR);
    }
    @GetMapping
    public ViewData<?> getNativeAccount(@CurrentUser WechatUser user){
        final Long id = user.getId();
        try{
            final WechatAccount account = accountService.findBy("userId", id);
            return ViewData.ok(account.getCredit());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("获取账户失败");
    }

}
