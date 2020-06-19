package org.dragon.paotui.service.impl;


import org.dragon.paotui.mapper.RolePermissionMapper;
import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionServiceImpl extends AbstractService<Permission> implements PermissionService {
    @Autowired
    RolePermissionMapper rolePermissionMapper;
    @Override
    public List<Permission> getAll() {
        return rolePermissionMapper.getPermissions();
    }

    @Override
    @Transactional
    public void removePermission(Long perId) {
        //把依赖自己的全删了
        StringBuilder sb = new StringBuilder(perId.toString());
        List<Permission> permissions = findAllByProperty("rely", perId);
        permissions.forEach(permission -> {
            sb.append("," + permission.getId());
            rolePermissionMapper.deleteByPerId(permission.getId());
        });
        deleteByIds(sb.toString());
    }

    @Override
    public void insertPermission(Permission permission) {
        if(permission.getRely() == null) {
            permission.setRely(0l);
        }
        insertAutoPrimary(permission);
    }
}
