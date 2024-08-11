package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeLikeRes {

    private boolean liked;

    @Builder
    public ThemeLikeRes(boolean liked) { this.liked = liked; }
}