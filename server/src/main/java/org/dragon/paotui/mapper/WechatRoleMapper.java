package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.WechatRole;

import java.util.Optional;
import java.util.Set;

@Mapper
public interface WechatRoleMapper extends MyMapper<WechatRole> {
    @Select("select * from wechat_role where id in(" +
            "select role_id from wechat_user_role where user_id = #{userId})")
    Set<WechatRole> findWechatRolesByUserId(Long userId);

    @Select("select * from wechat_role where role_name = #{roleName}")
    Optional<WechatRole> findWechatRoleByRoleName(String roleName);

    @Insert("insert into wechat_user_role(user_id, role_id)" +
            "values(#{userId}, #{roleId})")
    Integer insertRelationWechatUserAndRole(Long userId, Long roleId);
}
