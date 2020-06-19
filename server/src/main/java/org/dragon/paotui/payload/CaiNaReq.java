package org.dragon.paotui.payload;

import lombok.Data;
import lombok.NonNull;
import org.dragon.paotui.enumeration.TaskStatus;
import org.dragon.paotui.pojo.WechatOption;

import java.util.Set;

@Data
public class CaiNaReq{
    @NonNull
    private Long taskId;
    @NonNull
    private Long caiNaUserId;
    @NonNull
    private Long replyId;
}
