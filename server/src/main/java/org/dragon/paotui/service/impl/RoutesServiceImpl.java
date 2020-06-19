package org.dragon.paotui.service.impl;

import org.dragon.paotui.payload.Route;
import org.dragon.paotui.pojo.Permission;
import org.dragon.paotui.service.PermissionService;
import org.dragon.paotui.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutesServiceImpl extends AbstractService<Permission> implements RoutesService {
    @Autowired
    PermissionService permissionService;
    @Override
    public List<Route> getRoutes() {
        List<Permission> all = permissionService.getAll();
        List<Route> tops = getRoutes(all);
        return tops;
    }
    public List<Route> getRoutes(List<Permission> all) {
        List<Route> tops = new ArrayList<>();
        Integer index = 0;
        if(all == null || all.size() == 0) return null;
        for (Permission permission : all) {
            Route route = createRoute(permission);
            //提取路由顶层
            if(permission.getRely() == 0l) {
                tops.add(route);
            }else {
                index = deepSeqEachAdd(tops, route, index);
            }
        }
        return tops;
    }

    //由于route本身 path有序 父级一定在前面 所以 index 依次往下查找 找不到++
    private Integer deepSeqEachAdd(List<Route> tops, Route route, Integer index) {
        if(tops != null && tops.size() > 0) {
            for ( ; index < tops.size(); ) {
                //如果依赖top 就加入top 否则抛弃
                Route top = tops.get(index);
                if(top.getId() == route.getRely()) {
                    top.getChildren().add(route);
                    //找到插入则返回-1
                    return index;
                }else {
                    List<Route> children = top.getChildren();
                    Integer childrenIndex = deepSeqEachAdd(children, route, 0);
                    Boolean isFinded = childrenIndex < children.size();
                    if(!isFinded) ++index;
                    else return index;
                }

            }
        }
        return index;
    }

    private Route createRoute(Permission permission) {
        Route.RouteMeta meta = Route.RouteMeta.builder()
                .icon(permission.getIcon())
                .title(permission.getTitle())
                .roles(permission.getRoles())
                .build();
        Route build = Route.builder()
                .id(permission.getId())
                .name(permission.getTitle())
                .component(permission.getComponent())
                .meta(meta)
                .path(permission.getPath())
                .rely(permission.getRely())
                .children(new ArrayList<>())
                .build();
        return build;
    }
}
