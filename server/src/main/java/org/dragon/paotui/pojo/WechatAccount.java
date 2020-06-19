package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.enumeration.AccountType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatAccount implements Serializable {
    @Id
    private Long id;

    private Integer credit;

    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private AccountType type;

    private Long userId;
}