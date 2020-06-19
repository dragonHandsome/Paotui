package org.dragon.paotui.payload;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.pojo.WechatCategory;
import org.dragon.paotui.pojo.WechatTask;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResp implements Serializable {
    List<WechatCategory> categories;
    List<TaskDetail> tasks;
    @Data
    @Builder
    public static class TaskDetail implements Serializable{
        WechatTask task;
        WechatUser fromUser;
        WechatUserDetail userDetail;
    }
}
