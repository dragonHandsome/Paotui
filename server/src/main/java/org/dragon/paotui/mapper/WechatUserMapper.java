package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserTemp;

import java.util.Optional;

@Mapper
public interface WechatUserMapper extends MyMapper<WechatUser> {
    @Select("select * from wechat_user where openid = #{openid} limit 1")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickName", column = "nickName"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "language", column = "language"),
            @Result(property = "city", column = "city"),
            @Result(property = "province", column = "province"),
            @Result(property = "hiddenPoint", column = "hidden_point"),
            @Result(property = "wechatRoles", column = "id",
            many = @Many(select ="org.dragon.paotui.mapper.WechatRoleMapper.findWechatRolesByUserId", fetchType = FetchType.LAZY)
            )
    })
    Optional<WechatUser> findWechatUserByOpenid(String openid);

    @Select("select * from wechat_user where username = #{usernameOrEmail} or email = #{usernameOrEmail} limit 1")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "language", column = "language"),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "city", column = "city"),
            @Result(property = "hiddenPoint", column = "hidden_point"),
            @Result(property = "province", column = "province"),
            @Result(property = "wechatRoles", column = "id",
                    many = @Many(select ="org.dragon.paotui.mapper.WechatRoleMapper.findWechatRolesByUserId", fetchType = FetchType.LAZY)
            )
    })
    Optional<WechatUser> findWechatUserByUsername(String usernameOrEmail);

//    @Update("update wechat_user set nick_name = #{nickName},gender = #{gender},language = #{language}," +
//            "city = #{city}, province = #{province}, avatar_url = #{avatarUrl}, country = #{country}" +
//            " where username = #{username}")
//    void updateWechatUser(WechatUser wechatUser);

    @Options(useGeneratedKeys = true, keyProperty ="id")
    @Insert("insert into wechat_user(openid,username,enabled) values(#{openid},#{username},#{enabled})")
    int insertWechatUser(WechatUser wechatUser);
    @Update("update wechat_user set enabled = 1 where id = #{id}")
    void enableUser(Long id);

    @Update("update wechat_user set hidden_point = hidden_point - #{i} where id = #{userId}")
    void reduceHiddenPoint(long userId, int i);

    @Update("update wechat_user set hidden_point = hidden_point + #{i} where id = #{userId}")
    void increaseHiddenPoint(long userId, int i);
}
