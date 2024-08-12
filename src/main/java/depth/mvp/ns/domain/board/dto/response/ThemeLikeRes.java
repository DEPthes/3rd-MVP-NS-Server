package depth.mvp.ns.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ThemeLikeRes {
    private boolean liked;

    @Builder
    public ThemeLikeRes(boolean liked) { this.liked = liked; }
}
