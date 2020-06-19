package org.dragon.paotui.service;

import org.dragon.paotui.pojo.Permission;

import java.util.List;

public interface PermissionService extends BaseService<Permission>{
    //rely 排序 目的提取根节点 path 升序 子层级就在父层级后面
    List<Permission> getAll();

    void removePermission(Long perId);

    void insertPermission(Permission permission);
}
