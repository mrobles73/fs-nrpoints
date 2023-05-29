package co.sevenwide.nrpointsapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDTO {

    private String password;
    private String confirmPassword;
    private String token;
}
