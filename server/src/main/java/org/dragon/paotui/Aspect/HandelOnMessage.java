package org.dragon.paotui.Aspect;

import javax.websocket.Session;

public interface HandelOnMessage {
    void handelOnMessage(Message message, CustomerService customerService);
}
