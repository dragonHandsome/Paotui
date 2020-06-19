package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.enumeration.TaskStatus;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatTask implements Serializable {
    @Id
    private Long id;

    private String title;
    
    private String content;

    private Long fromUserId;

    private Long toUserId;

    private String toUserName;

    private Date createTime;

    private Date lastModifiedTime;

    private Integer star;
    //枚举需加
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private TaskStatus status;

    private Long categoryId;

    private WechatCategory category;

    private Long rewardMoney;

    @Builder.Default
    private Set<WechatOption> wechatOptions= new HashSet<>();
}