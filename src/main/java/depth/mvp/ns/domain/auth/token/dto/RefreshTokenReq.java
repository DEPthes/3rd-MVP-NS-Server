package depth.mvp.ns.domain.auth.token.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenReq {

    private String accessToken;

    private String refreshToken;

}
