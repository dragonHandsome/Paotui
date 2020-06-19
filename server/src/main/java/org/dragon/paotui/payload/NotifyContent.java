package org.dragon.paotui.payload;

import lombok.Builder;
import lombok.Data;
import org.dragon.paotui.utils.JSONUtils;
@Data
@Builder
public class NotifyContent {
    private String msg;
    private Long taskId;
    private Long userId;
    public String toJSON(){
        return toJSON(this);
    }
    public static String toJSON(NotifyContent notifyContent){
        return JSONUtils.toJSON(notifyContent);
    }
    public static NotifyContent toObj(String json){
        return JSONUtils.toObj(json, NotifyContent.class);
    }
}
