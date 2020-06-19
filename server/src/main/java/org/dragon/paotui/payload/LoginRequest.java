package org.dragon.paotui.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    private String usernameOrEmail;

    @NotNull
    private String password;
}
