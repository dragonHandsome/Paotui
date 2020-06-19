package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatAttention implements Serializable {
    @Id
    private Long id;


    private Long toUserId;


    private Long fromUserId;

    private Boolean isLike;
}