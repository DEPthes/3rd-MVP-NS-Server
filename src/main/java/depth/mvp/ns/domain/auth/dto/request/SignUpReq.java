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
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?!.*[\\W_]).{8,}$" +   // 소문자, 대문자, 숫자
            "|^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])(?!.*\\d).{8,}$" +     // 소문자, 대문자, 특수문자
            "|^(?=.*[a-z])(?=.*[\\W_])(?=.*\\d)(?!.*[A-Z]).{8,}$" +     // 소문자, 특수문자, 숫자
            "|^(?=.*[A-Z])(?=.*[\\W_])(?=.*\\d)(?!.*[a-z]).{8,}$",      // 대문자, 특수문자, 숫자
            message = "비밀번호는 알파벳 대·소문자, 숫자, 특수문자 중 3종류 이상을 조합하여, 최소 8자리 이상의 길이로 구성해주세요.")
    private String password;

    private String checkPassword;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{1,8}$",
            message = "한글 1~8글자 이내로 입력해주세요.")
    private String nickname;


}
