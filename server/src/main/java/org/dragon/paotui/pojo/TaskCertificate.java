package org.dragon.paotui.pojo;

import lombok.*;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCertificate {
    @Id
    private Long id;
    @NonNull
    private Long taskId;
    private String content;
    private String images;
    private Date createTime;
}
