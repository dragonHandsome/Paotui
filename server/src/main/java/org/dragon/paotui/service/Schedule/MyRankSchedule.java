package org.dragon.paotui.service.Schedule;

import org.dragon.paotui.mapper.RankMapper;
import org.dragon.paotui.pojo.RankList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class MyRankSchedule {
    @Autowired
    RankMapper rankMapper;
    public MyRankSchedule(){
        startTimer();
    }
    @Cacheable(value = "rank")
    public List<RankList> countRankList(Integer num){
        if(num == null) num = 7;
        List<RankList> rankLists = rankMapper.getRankList(num);
        return rankLists;
    }
    @CacheEvict(value = "rank", allEntries = true)
    public void reCount(){

    }
    public void startTimer(){
        Timer timer = new Timer();
        Calendar instance = Calendar.getInstance();
        instance.set(2020, 4, 11, 13, 10);
        Date time = instance.getTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reCount();
            }
        }, time, 24 * 60 * 60);
    }
}
