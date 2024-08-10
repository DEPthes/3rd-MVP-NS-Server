package depth.mvp.ns.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckPasswordReq {

    @NotBlank
    private String password;

    @NotBlank
    private String checkPassword;
}
