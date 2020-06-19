package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reply implements Serializable {
    @Id
    private Long id;
    private Long taskId;//哪个任务的数据
    private Long toUserId; //如果第二层 可以知道对谁回复
    private Long fromUserId;
    //内容存json格式
    private String content;

    private Date createdTime;//根据创建时间排序

    private Long toReplyId;//如果为0 则代表第一层 非0第二层
}
