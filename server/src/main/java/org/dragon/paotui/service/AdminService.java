package org.dragon.paotui.service;

import org.dragon.paotui.pojo.Admin;

import java.util.List;

public interface AdminService extends BaseService<Admin>{
     Admin loadUserByUsername(String usernameOrEmail);

     Admin addUser(Admin admin);

     void addRoleToUser(Long adminId, List<Long> roleId);

     void removeRoleToUser(Long adminId, List<Long> roleId);

     List<Admin> getUserList();

    void updateUser(Admin admin);
}
