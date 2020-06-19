package org.dragon.paotui.Aspect;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public class CustomerService {
    private static volatile CustomerService customerService;
    @Getter
    private ConcurrentHashMap<Long, CustomerServiceProvider> providerContainer =
            new ConcurrentHashMap();
    @Getter
    private ConcurrentHashMap<Long, Customer> customers =
            new ConcurrentHashMap();

    public CustomerServiceProvider registerProvider(Long userId, NotifyWebsocket websocket){
        CustomerServiceProvider customerServiceProvider = providerContainer.get(userId);
        if(customerServiceProvider == null) {
            customerServiceProvider = CustomerServiceProvider.newInstance(userId, websocket);
            providerContainer.put(customerServiceProvider.getId(), customerServiceProvider);
        }
        else if (websocket != null) customerServiceProvider.setWebsocket(websocket);
        return customerServiceProvider;
    }

    public void unRegisterProvider(Long userId) {
        CustomerServiceProvider customerServiceProvider = providerContainer.get(userId);
        if(providerContainer != null) {
            //如果没有正在服务的对象就删了
            if ( customerServiceProvider.getCustomers().size() == 0 ) {
                providerContainer.remove(userId);
            } else {
                //websocket要关闭
                customerServiceProvider.setWebsocket(null);
            }
        }
    }
    //申请客服服务
    public Customer applyProvider(Long userId, NotifyWebsocket websocket) {
        Customer customer = registerCustomer(userId, websocket);
        //通知所有在线客服
        notifyAllProvider(customer);
        return customer;
    }
    //注册成为客服消费者者
    public Customer registerCustomer(Long userId, NotifyWebsocket websocket) {
        Customer customer = customers.get(userId);
        if(customer == null) {
            customer = Customer.newInstance(userId, websocket);
            customerService.getCustomers().put(customer.getId(), customer);
        } else if (websocket != null) customer.setWebsocket(websocket);
        return customer;
    }
    //对特定提供者发起通知
    public void notifyProvider(CustomerServiceProvider customerServiceProvider, Customer customer) {
        customerServiceProvider.notifyNews(customer);
    }
    //通知所有提供者
    private void notifyAllProvider(Customer customer) {
        providerContainer.forEach((id, provider) -> {
            provider.notifyNews(customer);
        });
    }

    public void customerOffLine(Long userId) {
        Customer customer = customers.get(userId);
        //没人服务就删了
        if(customer != null) {
            customer.setWebsocket(null);
            // 如果没在客服服务状态就直接删除
            if( !customers.get(userId).getPending() ) {
                customers.remove(userId);
            }
        }
    }

    private CustomerService(){

    }
    public static CustomerService getInstance(){
        if(customerService == null) {
            synchronized (CustomerService.class) {
                if(customerService == null) {
                    customerService = new CustomerService();
                }
            }
        }
        return customerService;
    }
    public Customer getCustomer(Long id) {
        return customers.get(id);
    }

    public void removeCustomer(Long userId) {
        customers.remove(userId);
    }
}
