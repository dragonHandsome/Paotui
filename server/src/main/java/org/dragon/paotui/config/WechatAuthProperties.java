package org.dragon.paotui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
/**
 * wechat:
 *   appid: wx6084428066b6d098
 *   secret: 63b1eee6db22c240baa62323b13d7db9
 *   jsCode: _JSCODE
 *   url: https://api.weixin.qq.com/sns/jscode2session?appid=${wechat.appid}&secret=${wechat.secret}&js_code=${wechat.jsCode}&grant_type=authorization_code
 */
@ConfigurationProperties("wechat")
public class WechatAuthProperties {
    private String appid;
    private String secret;
    //需要替换url的jsCode内容为真实内容
    private String jsCode;

    private String url;

    public String getUrl(String code){
        return getUrl().replace(jsCode, code);
    }
}
