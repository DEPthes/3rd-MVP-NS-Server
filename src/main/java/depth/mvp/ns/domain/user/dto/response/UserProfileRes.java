package depth.mvp.ns.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import depth.mvp.ns.domain.board.domain.Board;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record UserProfileRes(
        Long userId,
        String nickname,
        String imageUrl,
        List<BoardListRes> boardListResList
) {

    @QueryProjection
    public UserProfileRes(Long userId, String nickname, String imageUrl, List<BoardListRes> boardListResList) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.boardListResList = boardListResList;
    }

    @Builder
    public static record BoardListRes(
            Long boardId,
            String title,
            String content,
            Long likeCount,
            boolean isLiked
    ) {

        @QueryProjection
        public BoardListRes(Long boardId, String title, String content, Long likeCount, boolean isLiked) {
            this.boardId = boardId;
            this.title = title;
            this.content = content;
            this.likeCount = likeCount;
            this.isLiked = isLiked;
        }
    }

}
