package org.dragon.paotui.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResp implements Serializable {
    private WechatUser user;
    private WechatUserDetail userDetail;

    public String getAvatar(){
        String avatar = userDetail.getAvatar();
        if(avatar == null){
            avatar = user.getAvatarUrl();
        }
        return avatar;
    }
    public String getName(){
        return user.getName();
    }
}
