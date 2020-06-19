package org.dragon.paotui.pojo;

import lombok.*;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.enumeration.HandelResultType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Id;
import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskHandleRecord implements Serializable {
    @Id
    private Long id;
    private String reason;
    private Long adminId;
    @NonNull
    private Long taskId;
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private HandelResultType type;
}
