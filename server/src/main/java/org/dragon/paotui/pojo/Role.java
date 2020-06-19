package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.payload.Route;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority, Serializable {
    @Id
    private Long id;


    private String roleName;

    private String description;

    private List<Permission> permissions;

    private List<Route> routes;

    @Override
    public String getAuthority() {
        return roleName;
        //return ((Role)(this)).getRoleName();
    }
}