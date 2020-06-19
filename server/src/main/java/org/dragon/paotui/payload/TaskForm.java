package org.dragon.paotui.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.pojo.WechatOption;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//本不该出现在这里 暂时不改了
public class TaskForm implements Serializable {
    @Pattern(regexp =".+")
    private String title;

    private String content;

    private Long rewardMoney;

    @NotNull
    private Long categoryId;

    private Long toUserId;

    private List<WechatOption> wechatOptions;
}
