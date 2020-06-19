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
@RequestMapping("/adminPage/routes")
public class RoutesController {
    @Autowired
    RoutesService routesService;
    @Autowired
    PermissionService permissionService;
    @GetMapping
    public ViewData getRoutes(){
        try{
            List<Route> routes  = routesService.getRoutes();
            return ViewData.ok(routes);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
}
