package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {
    @Id
    private Long id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String avatar;

    private Set<Role> roles;
}