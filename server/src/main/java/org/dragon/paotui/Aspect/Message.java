package org.dragon.paotui.Aspect;

import org.dragon.paotui.enumeration.MessageType;

public interface Message {
    MessageType getType();
    String getContent();
    Long getToUserId();
}
