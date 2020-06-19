package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.ibatis.type.JdbcType;
import org.dragon.paotui.enumeration.CategoryType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Id;
import javax.swing.*;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public class WechatCategory implements Serializable {
    @Id
    private Long id;


    private String name;

    private String info;

    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private CategoryType type;

    private String color;

    private String icon;
}