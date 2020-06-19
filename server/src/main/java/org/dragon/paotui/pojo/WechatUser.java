package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WechatUser implements Serializable,UserDetails,Cloneable {
    @Id
    private Long id;
    @JsonIgnore
    private String openid;

    private String nickName;

    private String username;

    @JsonIgnore
    private String password;

    private String email;
    @JsonIgnore
    private Boolean enabled;

    private String gender;

    private String language;

    private String city;

    private String province;

    private String avatarUrl;

    private String country;

    private Integer hiddenPoint;

    public String getName(){
        return this.getNickName() != null?
                this.getNickName() :
                this.getUsername();
    }

    @Builder.Default
    @JsonIgnore
    private Set<WechatRole> wechatRoles = new HashSet<>();

    public void setWechatRoles(Set<WechatRole> wechatRoles) {
        this.wechatRoles.addAll(wechatRoles);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getWechatRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled();
    }
}