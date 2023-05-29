package co.sevenwide.nrpointsapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDTO {

    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

}
