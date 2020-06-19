package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.Aspect.UserInfo;
import org.dragon.paotui.enumeration.Action;
import org.dragon.paotui.enumeration.NotifyType;
import org.dragon.paotui.payload.UserDetailResp;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notify implements Serializable {
    @Id
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    private UserInfo fromUser;

    private UserInfo toUser;

    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private NotifyType type;

    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private Action action;

    //存json算了 默认{"msg":""} 可加其他
    private String content;

    private Boolean isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;
}
