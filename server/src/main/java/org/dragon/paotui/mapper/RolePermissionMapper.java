package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.pojo.Role;
import org.dragon.paotui.pojo.RolePermission;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends MyMapper<RolePermission> {
    @Select("select * from role")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "roleName",column = "role_name"),
            @Result(property = "description",column = "description"),
            @Result(property = "permissions",column = "id",
                    many = @Many(select ="org.dragon.paotui.mapper.RolePermissionMapper.findAllPermissionByRoleId")),
    })
    List<Role> findAllRoles();

    @Select("select * from permission where id in " +
            "(select permission_id from role_permission where role_id = #{roleId})" +
            "ORDER BY rely ASC,path ASC")
    List<Permission> findAllPermissionByRoleId(Long roleId);

    @Select("select role_name from role where id in " +
            "(select role_id from role_permission where permission_id = #{perId})")
    List<String> findAllRolesByPermissionId(Long perId);

    @Select("select * from permission ORDER BY rely ASC,path ASC")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "path",column = "path"),
            @Result(property = "title",column = "title"),
            @Result(property = "icon",column = "icon"),
            @Result(property = "rely",column = "rely"),
            @Result(property = "component",column = "component"),
            @Result(property = "roles",column = "id",
                    many = @Many(select ="org.dragon.paotui.mapper.RolePermissionMapper.findAllRolesByPermissionId")),
    })
    List<Permission> getPermissions();

    @Delete("delete from role_permission where permission_id = #{perId}")
    void deleteByPerId(Long perId);
}
