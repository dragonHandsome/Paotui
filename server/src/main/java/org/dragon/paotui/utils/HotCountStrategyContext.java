package org.dragon.paotui.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class HotCountStrategyContext {
    public static HotCountStrategy getStrategy(){
        return SpringUtil.getBean(HotCountStrategy.class);
    }

}
