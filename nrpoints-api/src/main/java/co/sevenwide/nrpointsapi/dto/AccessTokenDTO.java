package co.sevenwide.nrpointsapi.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenDTO {
    private String accessToken;
    private UserDTO user;
}
