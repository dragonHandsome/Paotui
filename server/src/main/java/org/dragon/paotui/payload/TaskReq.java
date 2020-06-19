package org.dragon.paotui.payload;

import lombok.Data;
import org.dragon.paotui.enumeration.TaskStatus;
import org.dragon.paotui.pojo.WechatOption;

import java.util.Set;

@Data
public class TaskReq{
    private Long id;

    private String title;

    private String content;

    private TaskStatus status;

    private Long rewardMoney;

    private Integer star;

    private Long caiNaUserId;

    private Set<WechatOption> wechatOptions;
}
