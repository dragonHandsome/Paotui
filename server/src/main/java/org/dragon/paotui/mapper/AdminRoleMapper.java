package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.pojo.AdminRole;

@Mapper
public interface AdminRoleMapper extends MyMapper<AdminRole> {

}
