package depth.mvp.ns.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record UserInfoByNicknameRes(
        Long ranking,
        Long userId,
        String nickname,
        int point
) {

    @QueryProjection
    public UserInfoByNicknameRes(Long ranking, Long userId, String nickname, int point) {
        this.ranking = ranking;
        this.userId = userId;
        this.nickname = nickname;
        this.point = point;
    }
}
