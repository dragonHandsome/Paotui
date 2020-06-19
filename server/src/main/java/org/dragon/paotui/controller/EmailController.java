package org.dragon.paotui.controller;

import org.dragon.paotui.security.JwtTokenProvider;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmailController {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    WechatUserService userService;
    @GetMapping("/enableUser")
    public ModelAndView validateUserToEnabled(String validate, ModelAndView modelAndView){
        String viewName = null;
        try{
            if(jwtTokenProvider.validateToken(validate)){
                String username = jwtTokenProvider.getIdentificationFromJwt(validate);
                userService.enableUser(username);
                viewName = "email";
                modelAndView.addObject("msg", "验证成功，快去登录吧");
            }
            else MyLogUtil.throwError("验证出错");
        }catch (Exception e){
            e.printStackTrace();
            viewName = "error";
        }
        modelAndView.setViewName(viewName);
        return modelAndView;
    }
}
