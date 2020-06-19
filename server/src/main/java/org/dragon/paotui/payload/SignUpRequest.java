package org.dragon.paotui.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Component
@Setter
@Getter
@ToString
public class SignUpRequest {

    @Pattern(regexp = "(\\w|[\\u4e00-\\u9fa5]){3,32}", message = "中文、数字、英文字母，长度3-32位")
    private String username;

    @Pattern(regexp = "\\w{3,32}", message = "数字、英文字母，长度3-32位")
    private String password;

    @Email(message = "邮箱格式错误")
    private String email;
}
