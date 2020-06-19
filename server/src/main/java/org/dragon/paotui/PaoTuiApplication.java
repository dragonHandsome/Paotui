package org.dragon.paotui;

import org.dragon.paotui.Aspect.NotifyWebsocket;
import org.dragon.paotui.service.AdminService;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.NotifyFactory;
import org.dragon.paotui.utils.NotifyServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@EnableWebSecurity
@EnableWebSocket
@EnableGlobalMethodSecurity(prePostEnabled = true,jsr250Enabled = true, securedEnabled = true)
public class PaoTuiApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(PaoTuiApplication.class, args);
    }
    @Override//打包
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
    @Bean
    @Scope("prototype")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    @Autowired
    public void setNotifyService(NotifyService notifyService, WechatUserService userService, NotifyFactory notifyFactory, AdminService adminService){
        NotifyWebsocket.notifyService = notifyService;
        NotifyWebsocket.notifyFactory = notifyFactory;
        NotifyServiceProvider.userService = userService;
        NotifyServiceProvider.notifyService = notifyService;
        NotifyServiceProvider.adminService = adminService;
    }
}
