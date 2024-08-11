package depth.mvp.ns.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardLikeRes {

    private boolean liked;

    @Builder
    public BoardLikeRes(boolean liked) { this.liked = liked; }
}
