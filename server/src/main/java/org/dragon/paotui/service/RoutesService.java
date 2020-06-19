package org.dragon.paotui.service;

import org.dragon.paotui.payload.Route;
import org.dragon.paotui.pojo.Permission;

import java.util.List;

public interface RoutesService extends BaseService<Permission>{
    List<Route> getRoutes();

    public List<Route> getRoutes(List<Permission> all);
}
