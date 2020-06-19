package org.dragon.paotui.payload;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class JwtTakenData implements Serializable {
    private Long id;
    private String username;
}
