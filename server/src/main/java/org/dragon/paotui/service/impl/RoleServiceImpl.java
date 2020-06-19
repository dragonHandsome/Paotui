package org.dragon.paotui.service.impl;

import org.dragon.paotui.mapper.AdminRoleMapper;
import org.dragon.paotui.mapper.RolePermissionMapper;
import org.dragon.paotui.pojo.AdminRole;
import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.pojo.Role;
import org.dragon.paotui.pojo.RolePermission;
import org.dragon.paotui.service.PermissionService;
import org.dragon.paotui.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends AbstractService<Role> implements RoleService {
    @Autowired
    AdminRoleMapper adminRoleMapper;
    @Autowired
    RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"admin", "role"}, allEntries = true)
    public void removeRole(Long roleId) {
        Example example = new Example(AdminRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId", roleId);
        //删除含有该角色的对应关系
        adminRoleMapper.deleteByExample(example);
        //删除含有该角色的权限关系
        rolePermissionMapper.deleteByExample(example);
        //删除角色
        deleteById(roleId);
    }

    @Override
    public List<Role> getRoles() {
        return rolePermissionMapper.findAllRoles();
    }

    @Override
    @CacheEvict(value = "role", allEntries = true)
    @Transactional
    public Role addRole(Role role) {
        insertAutoPrimary(role);
        for (Permission permission : role.getPermissions()) {
            RolePermission build = RolePermission.builder()
                    .permissionId(permission.getId())
                    .roleId(role.getId())
                    .build();
            rolePermissionMapper.insertSelective(build);
        }
        return role;
    }

    @Override
    @CacheEvict(value = {"admin", "role"}, allEntries = true)
    @Transactional
    public void updateRole(Role role) {
        update(role);
        clearPerRole(role.getId());
        ArrayList<Long> perIds = role.getPermissions().stream().reduce(new ArrayList<>(), (acc, per) -> {
            acc.add(per.getId());
            return acc;
        }, (a,b)-> null);
        addPermissionToRole(role.getId(), perIds);
    }

    @Override
    public void clearPerRole(Long roleId) {
        RolePermission build = RolePermission.builder()
                .roleId(roleId).build();
        rolePermissionMapper.delete(build);
    }

    @Override
    public void removePermissionToRole(Long roleId, List<Long> ids) {
        ids.forEach(id -> {
            RolePermission build = RolePermission.builder()
                    .permissionId(id)
                    .roleId(roleId).build();
            rolePermissionMapper.delete(build);
        });
    }

    @Override
    public void addPermissionToRole(Long roleId, List<Long> ids) {
        ids.forEach(id -> {
            RolePermission build = RolePermission.builder()
                    .permissionId(id)
                    .roleId(roleId).build();
            rolePermissionMapper.insertSelective(build);
        });
    }
}
