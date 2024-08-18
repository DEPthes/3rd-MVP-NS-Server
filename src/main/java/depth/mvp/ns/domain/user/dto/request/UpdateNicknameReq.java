package depth.mvp.ns.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameReq {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{1,8}$",
            message = "한글 1~8글자 이내로 입력해주세요.")
    private String nickname;
}
