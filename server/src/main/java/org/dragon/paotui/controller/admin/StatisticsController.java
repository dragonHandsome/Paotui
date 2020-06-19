package org.dragon.paotui.controller.admin;

import org.dragon.paotui.mapper.StatisticsMapper;
import org.dragon.paotui.payload.Route;
import org.dragon.paotui.payload.Statistics;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.service.PermissionService;
import org.dragon.paotui.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminPage/statistics")
public class StatisticsController {
    @Autowired
    StatisticsMapper statisticsMapper;
    //#获取总的根据种类
    @GetMapping("/countAllTaskGroupByCategories")
    public ViewData countAllTaskGroupByCategories(){
        try {
            List<Statistics.CountAllTaskGroupByCategory> countAllTaskGroupByCategories =
                    statisticsMapper.CountAllTaskGroupByCategory();
            return ViewData.ok(countAllTaskGroupByCategories);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    //#获取上周根据种类和星期几的单数
    @GetMapping("/countAllTaskGroupByLastWeekDayAndCategories")
    public ViewData countAllTaskGroupByLastWeekDayAndCategories(){
        try {
            List<Statistics.CountAllTaskGroupByLastWeekDayAndCategory> countAllTaskGroupByLastWeekDayAndCategories =
                    statisticsMapper.CountAllTaskGroupByLastWeekDayAndCategory();
            return ViewData.ok(countAllTaskGroupByLastWeekDayAndCategories);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    //#获取上周根据是否有钱和星期几的任务数
    @GetMapping("/countAllTaskGroupByLastWeekDayAndHasrewards")
    public ViewData countAllTaskGroupByLastWeekDayAndHasrewards(){
        try {
            List<Statistics.CountAllTaskGroupByLastWeekDayAndHasreward> countAllTaskGroupByLastWeekDayAndHasrewards =
                    statisticsMapper.CountAllTaskGroupByLastWeekDayAndHasreward();
            return ViewData.ok(countAllTaskGroupByLastWeekDayAndHasrewards);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
}
