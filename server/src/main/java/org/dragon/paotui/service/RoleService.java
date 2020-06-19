package org.dragon.paotui.service;

import org.dragon.paotui.pojo.Role;

import java.util.List;

public interface RoleService extends BaseService<Role> {
    void removeRole(Long roleId);

    List<Role> getRoles();

    Role addRole(Role role);

    void updateRole(Role role);

    void clearPerRole(Long roleId);

    void removePermissionToRole(Long roleId, List<Long> ids);

    void addPermissionToRole(Long roleId, List<Long> ids);
}
