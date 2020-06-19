package org.dragon.paotui.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dragon.paotui.config.WechatAuthProperties;
import org.dragon.paotui.enumeration.AccountType;
import org.dragon.paotui.mapper.PrimaryMapper;
import org.dragon.paotui.mapper.WeChatUserDetailMapper;
import org.dragon.paotui.mapper.WechatUserMapper;
import org.dragon.paotui.payload.SignUpRequest;
import org.dragon.paotui.payload.WeChatCode2SessionResp;
import org.dragon.paotui.pojo.WechatAccount;
import org.dragon.paotui.pojo.WechatRole;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;
import org.dragon.paotui.security.JwtTokenProvider;
import org.dragon.paotui.service.AccountService;
import org.dragon.paotui.service.EmailService;
import org.dragon.paotui.service.WechatAuthService;
import org.dragon.paotui.service.WechatRoleService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class WechatAuthServiceImpl implements WechatAuthService {
    @Autowired
    WechatUserMapper wechatUserMapper;
    @Autowired
    WeChatUserDetailMapper weChatUserDetailMapper;
    @Autowired
    WechatRoleService wechatRoleService;
    @Autowired
    WechatAuthProperties authProperties;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PrimaryMapper primaryMapper;
    @Autowired
    AccountService accountService;

    @Override
    @Transactional
    public UsernamePasswordAuthenticationToken loginOrRegister(String code) {
        String url = authProperties.getUrl(code);
        String response = restTemplate.getForObject(url, String.class);
        WeChatCode2SessionResp weChatCode2SessionResp =
                dealSessionResponce(response);
        String openid = weChatCode2SessionResp.getOpenid();
        WechatUser wechatUser = wechatUserMapper.findWechatUserByOpenid(openid).orElse(null);
        if(wechatUser == null){
            //构建用户
            wechatUser = WechatUser.builder()
                    .openid(openid)
                    .username("wx_" + UUID.randomUUID().toString().substring(0, 16).replace("-",""))
                    .enabled(true)
                    .build();
            //插入用户
            wechatUserMapper.insertWechatUser(wechatUser);
            Long userId = wechatUser.getId();
            //生成用户详细
            insertRelation(userId, wechatUser);
            //生成本地账户
            generateAccount(userId);
        }
        if(!StringUtils.hasText(wechatUser.getUsername())) MyLogUtil.throwError("用户名为空");
        return new UsernamePasswordAuthenticationToken(
                wechatUser,
                null,
                wechatUser.getWechatRoles()
        );
    }

    public void insertRelation(Long userId, WechatUser wechatUser) {
        wechatUser.setId(userId);
        WechatUserDetail build = WechatUserDetail.builder().userId(userId).build();
        weChatUserDetailMapper.insertSelective(build);
        WechatRole wechatRole =
                wechatRoleService.findWechatRoleByRoleName(WechatRole.defaultRole().getRoleName())
                        .orElseThrow(()-> new RuntimeException("哪个管理员把基本角色删了"));
        //维护基本角色
        Integer updatedCount = wechatRoleService.insertRelationWechatUserAndRole(wechatUser, wechatRole);
        if(updatedCount != 1) throw new RuntimeException("插入角色失败");
    }
    public void generateAccount(long userId){
        final WechatAccount account = generateNativeAccount(userId);
        accountService.save(account);
    }
    private WechatAccount generateNativeAccount(long userId){
        final WechatAccount build = WechatAccount.builder()
                .credit(0)
                .userId(userId)
                .type(AccountType.NATIVE)
                .build();
        return build;
    }
    @Autowired
    EmailService emailService;
    @Override
    @Transactional
    public void register(SignUpRequest signUpRequest) {
        Boolean nonExistsUser = nonExistsUser(signUpRequest);
        if(!nonExistsUser)  throw new RuntimeException("用户或者邮箱已被注册");
        WechatUser wechatUser = WechatUser.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .enabled(false)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();
        try{
            wechatUserMapper.insertSelective(wechatUser);
        }catch (Exception e){
            MyLogUtil.throwError("插入用户失败");
        }
        //发送邮件让用户注册
        try{
            sendEmailForEnabled(signUpRequest);
        }catch (Exception e){
            MyLogUtil.throwError("发送邮件失败");
        }
    }
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    private void sendEmailForEnabled(SignUpRequest signUpRequest) {
        StringBuilder content = new StringBuilder();
        //host
        String url = "http://192.168.0.102:8080/enableUser?";
        String validate = jwtTokenProvider.generateToken(signUpRequest.getUsername());
        content.append("<h1>")
                .append("<a href='")
                .append(url)
                .append("validate=")
                .append(validate)
                .append("'>")
                .append("感谢您的申请注册，点击链接开启用户")
                .append("</a></h1>");
        emailService.sendHtml(signUpRequest.getEmail(), "感谢您的注册", content.toString());
    }

    //判断用户名以及邮箱是否存在
    @Override
    public Boolean nonExistsUser(SignUpRequest signUpRequest) {
        Condition condition = new Condition(WechatUser.class);
        Example.Criteria criteria = condition.createCriteria();
        Objects.requireNonNull(signUpRequest.getUsername());
        Objects.requireNonNull(signUpRequest.getEmail());
        criteria.orEqualTo("username", signUpRequest.getUsername());
        criteria.orEqualTo("email", signUpRequest.getEmail());
        List<WechatUser> wechatUsers = wechatUserMapper.selectByCondition(condition);
        if (wechatUsers.size() > 1) {
            MyLogUtil.throwError("查到多余1个");
        }
        return wechatUsers.size() == 0;
    }

    private WeChatCode2SessionResp dealSessionResponce(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        WeChatCode2SessionResp res = null;
        try {
            res = objectMapper.readValue(response, WeChatCode2SessionResp.class);
        } catch (JsonProcessingException e) {
            System.out.println("反序列化失败");
        }
        if(res == null || !StringUtils.hasText(res.getOpenid())){
            throw new RuntimeException("调用微信接口失败");
        }
        if(res.getErrcode() != null){
            throw new RuntimeException(res.getErrmsg());
        }
        return res;

    }
}
