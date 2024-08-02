package depth.mvp.ns.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class SignUpReq {

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{6,10}$",
            message = "영어 소문자와 숫자를 조합하여 6자에서 10자 이내로 입력해주세요.")
    private String username;

    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d|.*[\\W_])|(?=.*[A-Z])(?=.*[\\W_])(?=.*[a-z\\d])|(?=.*[a-z])(?=.*[\\W_])(?=.*[A-Z\\d])[A-Za-z\\d\\W_]{8,}$",
            message = "비밀번호는 알파벳 대·소문자, 숫자, 특수문자 중 3종류 이상을 조합하여, 최소 8자리 이상의 길이로 구성해주세요.")
    private String password;

    private String checkPassword;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{1,10}$",
            message = "한글 1~10글자 이내로 입력해주세요.")
    private String nickname;


}
