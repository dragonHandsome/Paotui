package org.dragon.paotui.controller.admin;

import org.dragon.paotui.payload.Route;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.service.PermissionService;
import org.dragon.paotui.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminPage/menu")
public class MenuController {
    @Autowired
    RoutesService routesService;
    @Autowired
    PermissionService permissionService;
    @GetMapping
    public ViewData getMenu(){
        try{
            List<Permission> permissions  = permissionService.getAll();
            return ViewData.ok(permissions);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }

    @PutMapping
    public ViewData addPermission(@RequestBody Permission permission){
        try{
            permissionService.insertPermission(permission);
            return ViewData.ok(permission);
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @DeleteMapping("/{perId}")
    public ViewData removePermission(@PathVariable Long perId){
        try{
            permissionService.removePermission(perId);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
    @PostMapping
    public ViewData updatePermission(@RequestBody Permission permission){
        try{
            permissionService.update(permission);
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(e.getMessage());
        }
    }
}
