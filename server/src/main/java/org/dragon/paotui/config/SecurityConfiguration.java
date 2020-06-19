package org.dragon.paotui.config;

import org.dragon.paotui.security.*;
import org.dragon.paotui.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    WechatUserService userService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    public JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    CustomAccessDecisionManager customAccessDecisionManager;

    @Autowired
    CustomFilterInvocationSecurityMetaDataFilter customFilterInvocationSecurityMetaDataFilter;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                        @Override
                        public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                            object.setAccessDecisionManager(customAccessDecisionManager);
                            object.setSecurityMetadataSource(customFilterInvocationSecurityMetaDataFilter);
                            return object;
                        }
                    })
                    //无需认证
//                    .antMatchers(//"/*",
//                            "/",
//                            "/signup",
//                            "/signin",
//                            "/auth/signin",
//                            "/enableUser",
//                            "/favicon.ico",
//                            "/uploads/**",
//                            "/templates/**",
//                            "/**/*.jpg",
//                            "/**/*.png",
//                            "/**/*.js",
//                            "/**/*.css",
//                            "/**/*.html",
//                            "/auth**"
//                    ).permitAll()
                    //动态判断权限
                    .anyRequest()
                        .authenticated()
                        .and()
                    .httpBasic()
                        .and()
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                 ;
    }
}