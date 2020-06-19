package org.dragon.paotui.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dragon.paotui.mapper.AttentionMapper;
import org.dragon.paotui.mapper.WeChatUserDetailMapper;
import org.dragon.paotui.mapper.WechatUserMapper;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.WechatAttention;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;
import org.dragon.paotui.pojo.WechatUserTemp;
import org.dragon.paotui.service.WechatAuthService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.JSONUtils;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WechatUserServiceImpl extends AbstractService<WechatUser> implements UserDetailsService, WechatUserService {
    @Autowired
    WechatUserMapper wechatUserMapper;
    @Autowired
    WeChatUserDetailMapper weChatUserDetailMapper;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    AttentionMapper attentionMapper;
    @Autowired
    WechatAuthService authService;
    @Autowired
    NotifyFactory notifyFactory;
    @Override
    /**
     * wechat中传入username
     */
    public WechatUser loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Objects.requireNonNull(usernameOrEmail);
        WechatUserTemp user = findWechatUserByUsername(usernameOrEmail);
        WechatUser build = WechatUser.builder()
                .username(user.getUsername())
                .enabled(user.getEnabled())
                .email(user.getEmail())
                .openid(user.getOpenid())
                .password(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .id(user.getId())
                .language(user.getLanguage())
                .nickName(user.getNickName())
                .province(user.getProvince())
                .wechatRoles(user.getWechatRoles())
                .build();
        return build;
    }
    //序列化 再也不把用户对象直接输出了。。。 vo entity分清楚了
    @Cacheable(value = "user", key = "#result.id")
    public WechatUserTemp findWechatUserByUsername(String usernameOrEmail){
        WechatUser user = wechatUserMapper.findWechatUserByUsername(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("找不到用户"));
        WechatUserTemp build = WechatUserTemp.builder()
                .username(user.getUsername())
                .enabled(user.getEnabled())
                .email(user.getEmail())
                .openid(user.getOpenid())
                .password(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .id(user.getId())
                .language(user.getLanguage())
                .nickName(user.getNickName())
                .province(user.getProvince())
                .wechatRoles(user.getWechatRoles())
                .build();
        return build;
    }
    @Override
    @CacheEvict(value = {"user", "userDetail"},key = "#p0.id")
    public void updateWechatUser(WechatUser wechatUser) {
        update(wechatUser);
    }

    @Override
    @Cacheable(value = "userDetail",key="#p0")
    public WechatUserDetail findWechatUserDetailByUserId(Long userId) {
        Example example = new Example(WechatUserDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        return weChatUserDetailMapper.selectOneByExample(example);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userDetail",key="#p0.userId")
    public void updateUserDetail(WechatUserDetail wechatUserDetail) {
            Example example = new Example(WechatUserDetail.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", wechatUserDetail.getUserId());
            weChatUserDetailMapper.updateByExampleSelective(wechatUserDetail, example);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userDetail",key="#p0.userId")
    public void updateUserBackground(WechatUserDetail wechatUserDetail) {
            Example example = new Example(WechatUserDetail.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", wechatUserDetail.getUserId());
            WechatUserDetail build = WechatUserDetail.builder().backgroundImage(wechatUserDetail.getBackgroundImage()).build();
            weChatUserDetailMapper.updateByExampleSelective(build, example);
    }

    @Override
    @Cacheable(value = "user",key = "#p0")
    public WechatUser findWechatUserByUserId(Long userId) {
        WechatUser wechatUser = wechatUserMapper.selectByPrimaryKey(userId);
        return Optional.ofNullable(wechatUser)
                .orElseThrow(()-> new RuntimeException("找不到用户"));
    }
    @Override
    @Transactional
    @CacheEvict(value = "user",key = "#result.id")
    public WechatUser enableUser(String username) {
        final WechatUser user = findBy("username", username);
        WechatUser build = WechatUser.builder()
                .enabled(true)
                .build();
        enableUser(user.getId());
        authService.generateAccount(user.getId());
        authService.insertRelation(user.getId(), user);
        return user;
    }

    private void enableUser(Long id) {
        wechatUserMapper.enableUser(id);
    }

    @Override
    @CacheEvict(value = {"like", "likeOne"},key = "#p0")
    public void toggleLike(Long fromUserId, Long toUserId) {
        Boolean isLike = false;
        final WechatAttention build = WechatAttention.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .build();
        WechatAttention wechatAttention = attentionMapper.selectOne(build);
        //没就插入表中
        if(wechatAttention == null){
            attentionMapper.insertSelective(build);
            isLike = true;
        }else {
            //有就更新切换
            wechatAttention.setIsLike(!wechatAttention.getIsLike());
            attentionMapper.updateByPrimaryKeySelective(
                    wechatAttention
            );
            isLike = wechatAttention.getIsLike();
        }
        if(isLike) createLikeNotify(fromUserId, toUserId);
    }

    private void createLikeNotify(Long fromUserId, Long toUserId) {
        NotifyFactory factory = notifyFactory.init(fromUserId, toUserId);
        factory.createLikeNotify();
    }

    @Override
    @Cacheable(value = "likeOne",key = "#p0")
    public Boolean isLike(Long fromUserId, Long toUserId) {
        Boolean isLike = false;
        final WechatAttention build = WechatAttention.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .build();
        final WechatAttention wechatAttention = attentionMapper.selectOne(build);
        if(
                wechatAttention != null &&
                wechatAttention.getIsLike()
        )isLike = true;
        return isLike;
    }

    @Override
    @Cacheable(value = "like",key = "#p0")
    public List<UserDetailResp> findLikedUserList(Long id) {
        final WechatAttention condition = WechatAttention.builder()
                .fromUserId(id)
                .isLike(true)
                .build();
        final List<WechatAttention> wechatAttentions = attentionMapper.select(condition);
        List<UserDetailResp> userDetailResps = new ArrayList<>();
        wechatAttentions.forEach(item -> {
            final WechatUser user = findWechatUserByUserId(item.getToUserId());
            final WechatUserDetail userDetail = findWechatUserDetailByUserId(item.getToUserId());
            final UserDetailResp build = UserDetailResp.builder()
                    .userDetail(userDetail)
                    .user(user).build();
            userDetailResps.add(build);
        });
        return userDetailResps;
    }

    @Override
    public UserDetailResp findUserDetail(Long fromUserId) {
        final WechatUser wechatUserByUserId = findWechatUserByUserId(fromUserId);
        final WechatUserDetail wechatUserDetailByUserId = findWechatUserDetailByUserId(fromUserId);
        final UserDetailResp build = UserDetailResp.builder()
                .user(wechatUserByUserId)
                .userDetail(wechatUserDetailByUserId)
                .build();
        return build;
    }

    @Override
    public void reduceHiddenPoint(long userId) {
        wechatUserMapper.reduceHiddenPoint(userId, 20);
    }
    @Override
    public void reduceHiddenPoint(long userId, Integer point) {
        wechatUserMapper.reduceHiddenPoint(userId, point);
    }
    public void increaseHiddentPoint(long userId) {
        wechatUserMapper.increaseHiddenPoint(userId, 5);
    }
}
