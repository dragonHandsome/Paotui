package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.utils.HotCountStrategyContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankList implements Serializable {
    @Id
    private Long id;
    private Long userId;
    private Integer score;
    private Integer count;
    private String background;
    private String name;
    private String motto;
    private String avatar;

    public Integer getHot(){
        return HotCountStrategyContext.getStrategy().getOutCome(score, count, userId);
    }
}
