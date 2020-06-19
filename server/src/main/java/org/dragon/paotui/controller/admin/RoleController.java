package org.dragon.paotui.controller.admin;

import org.apache.ibatis.annotations.Update;
import org.dragon.paotui.payload.Route;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Role;
import org.dragon.paotui.service.RoleService;
import org.dragon.paotui.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/adminPage/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @Autowired
    RoutesService routesService;

    @PutMapping
    public ViewData addRole(@RequestBody Role role){
        try{
            Role res = roleService.addRole(role);
            return ViewData.ok(res);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ViewData removeRole(@PathVariable Long id){
        try{
            roleService.removeRole(id);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }

    @PostMapping
    public ViewData updateRole(@RequestBody Role role){
        try{
            roleService.updateRole(role);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }

    @GetMapping
    public ViewData getRoles(){
        try{
            List<Role> roles =  roleService.getRoles();
            for (Role role : roles) {
                List<Route> routes = routesService.getRoutes(role.getPermissions());
                role.setRoutes(routes);
            }
            return ViewData.ok(roles);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @PutMapping("/permission/{roleId}/{perIds}")
    public ViewData addPermissionTo(@PathVariable Long roleId, @PathVariable String perIds){
        try{
            List<Long> ids = new ArrayList<>();
            Arrays.stream(perIds.split(",")).forEach(id -> {
                ids.add(Long.parseLong(id));
            });
            roleService.addPermissionToRole(roleId, ids);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }

    @DeleteMapping("/permission/{roleId}/{perIds}")
    public ViewData removePermissionTo(@PathVariable Long roleId, @PathVariable String perIds){
        try{
            List<Long> ids = new ArrayList<>();
            Arrays.stream(perIds.split(",")).forEach(id -> {
                ids.add(Long.parseLong(id));
            });
            roleService.removePermissionToRole(roleId, ids);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }

}
