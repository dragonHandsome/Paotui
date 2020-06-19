package org.dragon.paotui.Aspect;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.utils.JSONUtils;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class Customer implements HandelOnMessage{
    private Long id;

    private NotifyWebsocket websocket;

    private UserInfo user;

    private Map<Long, Notify> notifyQueue = new ConcurrentHashMap<>();

    private volatile Boolean pending = false;

    private volatile CustomerServiceProvider provider;

    public Boolean sendNotify(Notify notify) {
        if(provider == null) {
            notifyQueue.put(notify.getId(), notify);
            return true;
        }
        return provider.acceptNotify(notify);
    }
    public Boolean acceptNotify(Notify notify) {
        try {
            websocket.sendMessage(
                    JSONUtils.toJSON(ViewData.ok(notify))
            );
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    public void completeSession(){
        readAllNotifies();
        notifyQueue.clear();
        provider = null;
    }

    private void readAllNotifies() {
        Long[] isReadNotifyIds = new Long[notifyQueue.size()];
        Integer i = 0;
        for (Long id : notifyQueue.keySet()) {
            isReadNotifyIds[i++] = id;
        }
        NotifyWebsocket.notifyService
                .readNotify(isReadNotifyIds);
    }

    //发出所有通知
    public void sendNotify() {
        if(provider != null) {
            notifyQueue.forEach((id, notify) -> {
                provider.acceptNotify(notify);
            });
        }
    }

    public synchronized void loadProvider(CustomerServiceProvider provider){
        if(pending != true) {
            this.pending = true;
            this.provider = provider;
            //给客服发全部未处理消息
            sendNotify();
            //告诉自己谁来服务我
            remindWhoServerMe();
        } else {
            throw new RuntimeException("已经被接受");
        }
    }

    private void remindWhoServerMe() {
        if(provider != null) {
            NotifyFactory notifyFactory = NotifyWebsocket.notifyFactory;
            notifyFactory.init(provider.getId(), id);
            notifyFactory.createCustomerServiceNotify(
                    new StringBuilder()
                    .append("客服(")
                    .append(-provider.getId())
                    .append(")号,为您服务。")
                    .toString()
            );
        }
    }

    public void unLoadProvider(){
        this.pending = false;
        this.provider = null;
    }

    private Customer(Long id, NotifyWebsocket websocket){
        this.id = id;
        this.websocket = websocket;
        NotifyUser user = NotifyUser.getInstance(id);
        this.user = user;
    }

    public static Customer newInstance(Long id, NotifyWebsocket websocket){
        return new Customer(id, websocket);
    }

    private void createNotify(Message message) {
        NotifyFactory notifyFactory = NotifyWebsocket.notifyFactory;
        notifyFactory.init(id, 0l);
        Notify chatNotify = notifyFactory.createChatNotify(message.getContent());
        returnClientShowSendSuccess(chatNotify);
        sendNotify(chatNotify);
    }

    private void returnClientShowSendSuccess(Notify chatNotify) {
        try {
            websocket.sendMessage(
                    JSONUtils.toJSON(ViewData.ok(123, chatNotify))
            );
        } catch (IOException e) {
            MyLogUtil.error(e.getMessage());
        }
    }

    @Override
    public void handelOnMessage(Message message, CustomerService customerService) {
        createNotify(message);
    }
}
