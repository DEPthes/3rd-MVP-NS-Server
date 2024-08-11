package depth.mvp.ns.domain.user.dto.response;

import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageThemeLikeRes {

    private PageInfo pageInfo;

    private List<ThemeLikeByUserRes> themeLikeResList;

    @Builder
    public PageThemeLikeRes(PageInfo pageInfo, List<ThemeLikeByUserRes> themeLikeResList) {
        this.pageInfo = pageInfo;
        this.themeLikeResList = themeLikeResList;
    }
}
