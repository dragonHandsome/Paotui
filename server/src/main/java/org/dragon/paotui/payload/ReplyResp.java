package org.dragon.paotui.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResp implements Serializable {
    List<ReplyDetail> replyList;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyDetail implements Serializable{
        private Long id;
        private UserDetailResp fromUser;
        private UserDetailResp toUser;
        private Long taskId;
        private String content;
        private Date createdTime;
        private Long toReplyId;
    }
}
