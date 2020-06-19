package org.dragon.paotui.Aspect;

import lombok.Data;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.utils.JSONUtils;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CustomerServiceProvider implements HandelOnMessage{
    private Long id;

    private NotifyWebsocket websocket;

    private Map<Long, Customer> customers = new ConcurrentHashMap<>();


    private CustomerServiceProvider(Long id, NotifyWebsocket websocket) {
        this.id = id;
        this.websocket = websocket;
    }

    public static CustomerServiceProvider newInstance(Long id, NotifyWebsocket websocket) {
        return new CustomerServiceProvider(id, websocket);
    }
    public void notifyNews(Customer customer) {
        if(!customer.getPending()) {
            showNotify(customer);
        }
    }
    //接收消息
    public Boolean acceptNotify(Notify notify) {
        try {
            websocket.sendMessage(
                    JSONUtils.toJSON(
                            ViewData.ok(notify)
                    )
            );
            return true;
        }catch (Exception e) {
            return false;
        }
    }


    public Boolean sendNotify(Message message, Long userId){
        Customer customer = customers.get(userId);
        if(customer != null) {
            NotifyFactory notifyFactory = NotifyWebsocket.notifyFactory;
            notifyFactory.init(id, message.getToUserId());
            Notify sysNotify = notifyFactory.createCustomerServiceNotify(message.getContent());
            returnClientShowSendSuccess(sysNotify);
            if(customer.getWebsocket() != null) {
                return customer.acceptNotify(sysNotify);
            }
            return false;
        } else {
            customers.remove(userId);
            return false;
        }
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

    //挂在自己的会话中
    private void acceptCustomer(Customer customer){
        if (customer != null) {
            customers.put(customer.getId(), customer);
        }
    }
    //完成会话
    private void completeSession(Long userId, CustomerService customerService){
        Customer customer = customers.get(userId);
        if(customer != null) {
            tellCustomerSessionComplete(customer);
            customer.completeSession();
            remove(userId);
            customerService.removeCustomer(userId);
        }
    }

    private void tellCustomerSessionComplete(Customer customer) {
        NotifyFactory notifyFactory = NotifyWebsocket.notifyFactory;
        notifyFactory.init(0l, customer.getId());
        Notify customerServiceNotify = notifyFactory.createCustomerServiceNotify("本次会话结束。");
        customer.acceptNotify(customerServiceNotify);
    }

    private Customer remove(Long userId){
        Customer customer = customers.get(userId);
        if(customer != null) {
            customer.unLoadProvider();
            customers.remove(userId);
        }

        return customer;
    }

    //接收此次用户会话
    private void acceptSession(Customer customer){
        if(customer.getPending() != true) {
            //客户挂载客服
            customer.loadProvider(this);
            //客服接收客户
            acceptCustomer(customer);
        }
    }
    //撤销此次会话
    private void cancelSession(Message message, CustomerService customerService) {
        Customer customer = remove(message.getToUserId());
        //被取消会话后重新申请
        customerService.applyProvider(customer.getId(), customer.getWebsocket());
    }

    private void showNotify(Customer customer) {
        Notify notify = createSysNotify(customer);
        acceptNotify(notify);
    }


    //提示客服有用户发来消息
    private Notify createSysNotify(Customer customer) {
        return Notify.builder()
                .id(-customer.getId())
                .fromUser(null)
                .fromUserId(0l)
                .toUserId(customer.getId())
                .toUser(customer.getUser())
                .createdTime(new Date())
                .build();
    }

    @Override
    //onMessage的时候会调用，即是处理自己要发的消息
    public void handelOnMessage(Message message, CustomerService customerService) {
        switch (message.getType()) {
            //发给用户的消息
            case TO_USER:
                sendNotify(message, message.getToUserId());
                break;
                //取消此次会话
            case CANCEL_SESSION:
                cancelSession(message, customerService);
                break;
                //接收此次会话
            case ACCEPT_SESSION:
                acceptSession(customerService.getCustomer(message.getToUserId()));
                break;
                //完成此次会话
            case COMPLETE_SESSION:
                completeSession(message.getToUserId(), customerService);
                break;
        }
    }

}
