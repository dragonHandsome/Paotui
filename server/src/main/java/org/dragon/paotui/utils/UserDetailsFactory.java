package org.dragon.paotui.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class UserDetailsFactory {
    public static UserDetailsBuilder builder(){
        return new UserDetailsBuilder();
    }
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    public static class UserDetailsBuilder  {
        //微信用户没密码
        private String password = "1";
        private String username;
        private Set<? extends GrantedAuthority> authorities;
        private boolean accountNonExpired = true;
        private boolean credentialsNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean enabled;
        public UserDetailsBuilder password(String password){
            //微信用户没密码 到时改成插入时随机密码
            this.password = password == null? "1" : password;
            return this;
        }
        public UserDetailsBuilder username(String username){
            this.username = username;
            return this;
        }
        public UserDetailsBuilder authorities(Set<? extends GrantedAuthority> authorities){
            this.authorities = authorities;
            return this;
        }
        public UserDetailsBuilder enabled(Boolean enabled){
            this.enabled = enabled;
            return this;
        }
        public UserDetails build(){
            return new User(getUsername(), getPassword(),
                    isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities()
            );
        }
    }
}
