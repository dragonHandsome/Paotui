package org.dragon.paotui.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.enumeration.TaskStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskQuery {
    private Integer page;
    private Integer limit;
    private Integer star;
    private String title;
    private TaskStatus status;
    private String sort;
    private Long categoryId;
    private Long fromUserId;
    private Long toUserId;
}
