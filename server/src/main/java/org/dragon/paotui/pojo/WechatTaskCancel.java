package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.enumeration.TaskDealStatus;
import org.dragon.paotui.enumeration.TaskStatus;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatTaskCancel implements Serializable {
    @Id
    private Long id;

    @NotNull
    private Long taskId;

    private String content;

    private String images;

    private WechatTask task;
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private TaskDealStatus status;

    private Date createTime;

    private Date lastModifiedTime;

}