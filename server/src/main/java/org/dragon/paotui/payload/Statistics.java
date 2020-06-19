package org.dragon.paotui.payload;

import lombok.Data;

public class Statistics {
    //#获取上周根据是否有钱和星期几的任务数
    @Data
    public static class CountAllTaskGroupByLastWeekDayAndHasreward{
        private Integer count;
        private Boolean hasReward;
        private String date;
    }
    //#获取上周根据种类和星期几的单数
    @Data
    public static class CountAllTaskGroupByLastWeekDayAndCategory{
        private Integer count;
        private String category;
        private String date;
    }
    //#获取总的根据种类
    @Data
    public static class CountAllTaskGroupByCategory{
        private Integer count;
        private String category;
    }
}
