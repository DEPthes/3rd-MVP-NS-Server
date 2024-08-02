package depth.mvp.ns.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageRes {

    private Long userId;

    private String nickname;

    private String imageUrl;

    @Builder
    public MyPageRes(Long userId, String nickname, String imageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

}
