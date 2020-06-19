package org.dragon.paotui.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class JwtAuthenticationResponse implements Serializable {
    private String token;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String token){
        this.token = token;
    }

    public static JwtAuthenticationResponse take(String token){
        return new JwtAuthenticationResponse(token);
    }
}
