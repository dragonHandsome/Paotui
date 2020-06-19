package org.dragon.paotui.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
public class WechatLoginRequest {
    @NotBlank
    private String code;

}
