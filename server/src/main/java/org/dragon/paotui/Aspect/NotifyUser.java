package org.dragon.paotui.Aspect;

import lombok.Data;
import org.dragon.paotui.enumeration.UserType;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.utils.NotifyServiceProvider;

@Data
public class NotifyUser implements UserInfo{
    private UserDetailResp user;
    private Admin admin;

    private Long id;
    private String name;
    private String avatar;
    private UserType type;

    public NotifyUser(UserDetailResp user){
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.id = user.getUser().getId();
        this.type = UserType.User;
    }
    public NotifyUser(Admin admin){
        this.name = admin.getName();
        this.avatar = admin.getAvatar();
        this.id = admin.getId();
        this.type = UserType.Admin;
    }

    public static NotifyUser getInstance(Long userId){
        if(userId > 0) {
            UserDetailResp userDetail = NotifyServiceProvider.userService
                    .findUserDetail(userId);
            return new NotifyUser(userDetail);
        } else if (userId < 0) {
            Admin admin = NotifyServiceProvider.adminService
                    .findById(-userId);
            return new NotifyUser(admin);
        }
        return null;
    }
}
