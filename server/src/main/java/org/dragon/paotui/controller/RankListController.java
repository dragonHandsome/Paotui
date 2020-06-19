package org.dragon.paotui.controller;

import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.RankList;
import org.dragon.paotui.service.Schedule.MyRankSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rankList")
public class RankListController {
    @Autowired
    MyRankSchedule rankSchedule;
    @GetMapping
    public ViewData getRankList(){
        try{
            //rankSchedule.reCount(); 改了RankList记得清除缓存
            List<RankList> rankLists = rankSchedule
                    .countRankList(7);
            return ViewData.ok(rankLists);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error("获取排行榜失败");
        }
    }
}
