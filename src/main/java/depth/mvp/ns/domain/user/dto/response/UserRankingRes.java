package depth.mvp.ns.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record UserRankingRes(
        Long userId,
        String nickname,
        String imageUrl,
        int point
) {

    @QueryProjection
    public UserRankingRes(Long userId, String nickname, String imageUrl, int point) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.point = point;
    }
}
