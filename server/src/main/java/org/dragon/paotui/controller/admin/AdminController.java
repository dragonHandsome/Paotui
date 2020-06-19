package org.dragon.paotui.controller.admin;

import lombok.Data;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Admin;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/adminPage/admin")
public class AdminController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AdminService adminService;

    @GetMapping("/info")
    public ViewData getUserInfo(@CurrentUser Admin admin){
        try{
            Admin build = Admin.builder()
                    .email(admin.getEmail())
                    .id(admin.getId())
                    .name(admin.getName())
                    .roles(admin.getRoles())
                    .username(admin.getUsername())
                    .avatar(admin.getAvatar())
                    .build();
            return ViewData.ok(build);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @PutMapping
    public ViewData addUser(@RequestBody  Admin admin){
        try{
            Admin addUser = adminService.addUser(admin);
            return ViewData.ok(addUser);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @PostMapping
    public ViewData updateUser(@RequestBody Admin admin){
        try{
            adminService.updateUser(admin);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @GetMapping
    public ViewData getUserList(){
        try{
            List<Admin> admins = adminService.getUserList();
            return ViewData.ok(admins);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @PutMapping("/role/{adminId}/{roleIds}")
    public ViewData addRoleToUser(@PathVariable Long adminId, @PathVariable String roleIds){
        try{
            List<Long> ids = new ArrayList<>();
            Arrays.stream(roleIds.split(",")).forEach(id -> {
                ids.add(Long.parseLong(id));
            });
            adminService.addRoleToUser(adminId, ids);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @DeleteMapping("/role/{adminId}/{roleIds}")
    public ViewData removeRoleToUser(@PathVariable Long adminId, @PathVariable String roleIds){
        try{
            List<Long> ids = new ArrayList<>();
            Arrays.stream(roleIds.split(",")).forEach(id -> {
                ids.add(Long.parseLong(id));
            });
            adminService.removeRoleToUser(adminId, ids);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
}
