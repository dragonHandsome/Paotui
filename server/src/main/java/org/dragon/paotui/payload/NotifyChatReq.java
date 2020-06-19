package org.dragon.paotui.payload;

import lombok.*;
import org.dragon.paotui.Aspect.Message;
import org.dragon.paotui.enumeration.MessageType;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyChatReq implements Serializable, Message {
    @NonNull
    private Long toUserId;
    @NonNull
    private String content;
    @NonNull
    private MessageType type;

}
