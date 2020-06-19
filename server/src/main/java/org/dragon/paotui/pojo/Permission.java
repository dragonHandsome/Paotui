package org.dragon.paotui.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {
    @Id
    private Long id;

    private String title;

    private String path;

    private String icon;

    private Long rely;

    private String component;

    @Builder.Default
    private List<String> roles = new ArrayList<>();
}