package depth.mvp.ns.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.util.List;

@Builder
public record UserRankingRes(
        List<Top3UserRes> top3UserRes,
        List<OptionRankingRes> optionRankingRes

) {

    public static record Top3UserRes(
            Long userId,
            String nickname,
            String imageUrl,
            int point,
            boolean isCurrentUser
    ) {

        @QueryProjection
        public Top3UserRes(Long userId, String nickname, String imageUrl, int point, boolean isCurrentUser) {
            this.userId = userId;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.point = point;
            this.isCurrentUser = isCurrentUser;
        }
    }

    public static record OptionRankingRes(
            Long ranking,
            Long userId,
            String nickname,
            int point,
            boolean isCurrentUser
    ) {

        @QueryProjection
        public OptionRankingRes(Long ranking, Long userId, String nickname, int point, boolean isCurrentUser) {
            this.ranking = ranking;
            this.userId = userId;
            this.nickname = nickname;
            this.point = point;
            this.isCurrentUser = isCurrentUser;
        }
    }
}
