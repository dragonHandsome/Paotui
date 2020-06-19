package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatUserTemp  implements Serializable {

    private Long id;

    private String openid;

    private String nickName;

    private String username;

    private String password;

    private String email;

    private Boolean enabled;

    private String gender;

    private String language;

    private String city;

    private String province;

    private String avatarUrl;

    private String country;

    @Builder.Default
    private Set<WechatRole> wechatRoles = new HashSet<>();

    public String getName(){
        return this.getNickName() != null?
                this.getNickName() :
                this.getUsername();
    }
}
