package org.dragon.paotui.service;

import org.dragon.paotui.payload.ReplyReq;
import org.dragon.paotui.payload.ReplyResp;
import org.dragon.paotui.pojo.Reply;

public interface ReplyService extends BaseService<Reply>{
    void insertReply(ReplyReq replyReq, Long id);

    ReplyResp getReplyList(Long taskId);

    void updateReply(Reply reply);
}
