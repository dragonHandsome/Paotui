package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Id;
import java.io.Serializable;

;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatRole implements GrantedAuthority, Serializable {
    @Id
    private Long id;


    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
    public static WechatRole defaultRole(){
        return WechatRole.builder().roleName("CUSTOMER").build();
    }
}