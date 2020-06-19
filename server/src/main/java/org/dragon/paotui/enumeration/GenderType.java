package org.dragon.paotui.enumeration;

import lombok.Getter;

@Getter
public enum GenderType {
    FEMALE(0, "女"),
    MALE(1, "男"),
    OTHER(2, "未知");

    private String genderMsg;
    private Integer gender;
    GenderType(Integer gender, String genderMsg) {
        this.gender = gender;
        this.genderMsg = genderMsg;
    }
}
