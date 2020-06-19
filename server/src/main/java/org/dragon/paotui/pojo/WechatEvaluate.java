package org.dragon.paotui.pojo;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class WechatEvaluate implements Serializable {
    @Id
    private Long id;


    private BigDecimal starLevel;


    private String content;


    private Long taskId;

}