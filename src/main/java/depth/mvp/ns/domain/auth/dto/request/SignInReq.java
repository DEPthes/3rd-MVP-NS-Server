package depth.mvp.ns.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInReq {

    private String username;

    private String password;

    // 자동 로그인 여부(추후 구현)
}
