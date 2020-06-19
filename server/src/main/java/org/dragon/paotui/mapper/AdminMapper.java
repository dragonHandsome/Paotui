package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Admin;

import java.util.List;

@Mapper
public interface AdminMapper extends MyMapper<Admin> {
    @Select("select * from admin where username = #{usernameOrEmail} or email = #{usernameOrEmail} ")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "email",column = "email"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "name",column = "name"),
            @Result(property = "roles",column = "id",
                    many = @Many(select ="org.dragon.paotui.mapper.RoleMapper.findRolesByAdminId", fetchType = FetchType.LAZY)),
    })
    Admin findAdminByUserNameOrEmail(String usernameOrEmail);

    @Select("select * from admin")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "name",column = "name"),
            @Result(property = "roles",column = "id",
                    many = @Many(select ="org.dragon.paotui.mapper.RoleMapper.findRolesByAdminId")),
    })
    List<Admin> findAllUser();
}
