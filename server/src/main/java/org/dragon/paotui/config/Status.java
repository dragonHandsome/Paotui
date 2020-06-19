package org.dragon.paotui.config;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

public class Status {
    @Data
    @Builder
    public static class ExpireEnum{
        private Long time;
        private TimeUnit timeUnit;
    }
}
