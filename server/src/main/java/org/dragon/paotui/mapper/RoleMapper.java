package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Role;

import java.util.Set;

@Mapper
public interface RoleMapper extends MyMapper<Role> {
    @Select("select * from role where id in(" +
            "select role_id from admin_role where admin_id = #{adminId})")
    Set<Role> findRolesByAdminId(Long adminId);
}
