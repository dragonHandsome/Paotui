package org.dragon.paotui.service.impl;

import org.dragon.paotui.mapper.AdminMapper;
import org.dragon.paotui.mapper.AdminRoleMapper;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.pojo.AdminRole;
import org.dragon.paotui.pojo.Role;
import org.dragon.paotui.service.AdminService;
import org.dragon.paotui.service.RoleService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl extends AbstractService<Admin> implements AdminService {
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    AdminRoleMapper adminRoleMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;

    @Override
//    @Cacheable(value = "admin", key = "#result.id", unless = "#result == null ")
    public Admin loadUserByUsername(String usernameOrEmail) {
        Admin admin = adminMapper.findAdminByUserNameOrEmail(usernameOrEmail);
        return admin;
    }

    @Override
    @Transactional
    public Admin addUser(Admin admin) {
        Admin admin1 = loadUserByUsername(admin.getUsername());
        if(admin1 != null) {
            MyLogUtil.error("用户已经存在");
            return null;
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        insertAutoPrimary(admin);
        addRoleToUser(admin, admin.getRoles());
        return admin;
    }

    @Override
    @CacheEvict(value = "admin", key = "#p0")
    public void addRoleToUser(Long adminId, List<Long> roleIds) {
        roleIds.forEach(id -> {
            AdminRole build = AdminRole.builder()
                    .adminId(adminId)
                    .roleId(id).build();
            adminRoleMapper.insertSelective(build);
        });
    }

    private void addRoleToUser(Admin admin, Set<Role> roles) {
        ArrayList<Long> roleIds = admin.getRoles().stream()
                .reduce(new ArrayList<Long>(), (acc, cur) -> {
                    acc.add(cur.getId());
                    return acc;
                }, (a, b) -> null);
        addRoleToUser(admin.getId(), roleIds);
    }

    @Override
    @CacheEvict(value = "admin", key = "#p0")
    public void removeRoleToUser(Long adminId, List<Long> roleIds) {
        roleIds.forEach(id -> {
            AdminRole build = AdminRole.builder()
                    .adminId(adminId)
                    .roleId(id).build();
            adminRoleMapper.delete(build);
        });
    }

    @Override
    public List<Admin> getUserList() {
        List<Admin> allUser = adminMapper.findAllUser();
        return allUser;
    }

    @Override
    @Transactional
    public void updateUser(Admin admin) {
        if(StringUtils.hasText(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        update(admin);
        removeAllRole(admin);
        addRoleToUser(admin, admin.getRoles());
    }

    private void removeAllRole(Admin admin) {
        AdminRole build = AdminRole.builder()
                .adminId(admin.getId()).build();
        adminRoleMapper.delete(build);
    }
}
