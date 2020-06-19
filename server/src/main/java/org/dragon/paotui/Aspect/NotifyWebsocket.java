package org.dragon.paotui.Aspect;

import org.dragon.paotui.enumeration.MessageType;
import org.dragon.paotui.payload.NotifyChatReq;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.utils.JSONUtils;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/notify/{userId}")
@Component
@Scope("prototype")
public class NotifyWebsocket {
    private static Integer onlineCount = 0;
    private static Integer providerCount = 0;
    private static ConcurrentHashMap<Long, NotifyWebsocket> websocketContainer =
            new ConcurrentHashMap();
    public static CustomerService customerService = CustomerService.getInstance();
    private Session session;
    private Long userId;
    public static NotifyService notifyService;
    public static NotifyFactory notifyFactory;
    private HandelOnMessage handel;

    @OnOpen
    public void onOpen(@PathParam("userId")Long userId, Session session, EndpointConfig config) {
        this.userId = userId;
        this.session = session;
        //客户
        if (userId > 0) {
            registerCustomer(userId, session);
            //接收系统或者好友未读消息
            sendNotify();
        }
        //客服
        else if(userId < 0){
            //注册成为客服
            registerProvider(userId, session);
        }
    }
    //
    private void registerProvider(Long userId, Session session) {
        CustomerServiceProvider customerServiceProvider = customerService.registerProvider(userId, this);
        addAndReturnProviderOnlineCount();
        //监听OnMessage
        handel = customerServiceProvider;
        //接收未处理客户消息
        acceptAllNotDealNotify(customerServiceProvider);
    }

    private void acceptAllNotDealNotify(CustomerServiceProvider customerServiceProvider) {
        //0l代表给系统
        List<Notify> notReadNotifies = notifyService.getNotReadNotifies(0l);
        //把通知集合成customers
        Map<Long, Customer>  customers = new HashMap<>();
        notReadNotifies.forEach(notify -> {
            Customer customer =
                    customerService.registerCustomer(notify.getFromUserId(), null);
            //消息都加上
            customer.sendNotify(notify);
            customers.put(customer.getId(), customer);
        });
        //告诉新上线的客服取处理未处理的通知
        customers.forEach((id, customer) -> {
            customerService.notifyProvider(customerServiceProvider, customer);
        });
    }

    private void registerCustomer(Long userId, Session session) {
        websocketContainer.put(userId, this);// 加入map中
        addAndReturnOnlineCount(); // 在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if(!StringUtils.hasText(message)) return;
        Message msg = JSONUtils.toObj(message, NotifyChatReq.class);
        if(handel == null || msg.getType() == MessageType.CHAT){
            handelChat(msg, session);
        } else {
            handel.handelOnMessage(msg, customerService);
        }
    }

    private void handelChat(Message chatReq, Session session) {
        //==0代表发给系统客服
        if(chatReq.getToUserId() > 0) {
            notifyFactory.init(userId, chatReq.getToUserId());
            Notify chatNotify = notifyFactory.createChatNotify(chatReq.getContent());
            //回给客户端表示收到
            try {
                sendMessage(
                        JSONUtils.toJSON(ViewData.ok(123, chatNotify))
                );
            } catch (IOException e) {
                MyLogUtil.error(e.getMessage());
            }
        } else {
            //成为客服需求者，并申请客服服务
            Customer customer = customerService.applyProvider(userId, this);
            handel = customer;
            handel.handelOnMessage(chatReq, customerService);
        }
    }

    public void sendNotify(){
        List<Notify> notifies = notifyService.getNotReadNotifies(userId);
        notifies.forEach(notify -> {
            try{
                sendMessage(
                        JSONUtils.toJSON(ViewData.ok(notify))
                );
            }catch (Exception e){
                e.printStackTrace();
                MyLogUtil.warn("发送失败");
            }
        });
    }
    public static void sendNotify(Notify notify,Long toUserId){
        NotifyWebsocket notifyWebsocket = websocketContainer.get(toUserId);
        if(notifyWebsocket != null){
            try{
                notifyWebsocket.sendMessage(JSONUtils.toJSON(
                        ViewData.ok(notify)
                ));
            }catch (Exception e){
                MyLogUtil.warn("发送失败");
            }
        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        websocketContainer.remove(userId);
        if (userId != null && userId > 0) { // 从容器中删除
            customerService.customerOffLine(userId);
            subOnlineAndReturnCount(); // 在线数减1
            System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
        }
        if(userId != null && userId < 0) {
            customerService.unRegisterProvider(userId);
            subOnlineProviderOnlineCount();
            System.out.println("有一连接关闭！当前客服在线为" + getProviderOnlineCount());
        }
    }

    public void sendMessage(String message) throws IOException {
            this.session.getBasicRemote().sendText(message);
    }
    //发送消息给所有在线用户
    public static void sendNotifyToAll(Notify notify){
        websocketContainer.forEach((userId, websocket) -> {
            if(websocket != null){
                try{
                    websocket.sendMessage(
                            JSONUtils.toJSON(
                                    ViewData.ok(notify)
                            )
                    );
                }catch (Throwable e){
                    MyLogUtil.warn(e.getMessage());
                }
            }
        });
    }

    private static Integer getOnlineCount() {
        return onlineCount;
    }

    private synchronized Integer addAndReturnOnlineCount() {
        return ++onlineCount;
    }

    private synchronized Integer subOnlineAndReturnCount() {
        return --onlineCount;
    }

    private static Integer getProviderOnlineCount() {
        return providerCount;
    }

    private synchronized Integer addAndReturnProviderOnlineCount() {
        return ++providerCount;
    }

    private synchronized Integer subOnlineProviderOnlineCount() {
        return --providerCount;
    }
}