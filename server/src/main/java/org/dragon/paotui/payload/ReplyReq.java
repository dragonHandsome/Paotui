package org.dragon.paotui.payload;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReplyReq {
    private Long toUserId;
    @NotNull
    private Long taskId;
    @NotNull
    private String content;

    private Long toReplyId;
}
