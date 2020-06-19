package org.dragon.paotui.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.pojo.Role;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Route implements Serializable {
    private Long id;

    private String path;

    private String name;

    private String component;

    private RouteMeta meta;

    private Long rely;

    private List<Route> children;
    @Data
    @Builder
    public static class RouteMeta implements Serializable{
        private String title;
        private String icon;
        private List<String> roles;
    }

}
