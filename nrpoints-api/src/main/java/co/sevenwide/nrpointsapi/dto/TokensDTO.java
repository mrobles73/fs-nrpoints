package co.sevenwide.nrpointsapi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokensDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
