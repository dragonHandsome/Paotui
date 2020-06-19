package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRole {
    @Id
    private Long id;
    private Long adminId;
    private Long roleId;
}
