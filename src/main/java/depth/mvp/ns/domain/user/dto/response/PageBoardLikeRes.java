package depth.mvp.ns.domain.user.dto.response;

import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageBoardLikeRes {

    private PageInfo pageInfo;
    private List<BoardLikeByUserRes> boardLikeResList;

    @Builder
    private PageBoardLikeRes(PageInfo pageInfo, List<BoardLikeByUserRes> boardLikeResList) {
        this.pageInfo = pageInfo;
        this.boardLikeResList = boardLikeResList;
    }
}
