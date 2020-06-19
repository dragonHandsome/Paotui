package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Permission;

import java.util.List;

@Mapper
public interface PermissionMapper extends MyMapper<Permission> {
}
