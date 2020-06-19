package org.dragon.paotui.Aspect;

import org.dragon.paotui.enumeration.UserType;

public interface UserInfo {
    Long getId();
    String getName();
    String getAvatar();
    UserType getType();
}
