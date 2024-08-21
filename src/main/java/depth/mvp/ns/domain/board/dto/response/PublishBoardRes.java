package depth.mvp.ns.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublishBoardRes {

    private String message; // 성공 메세지
    private boolean firstPost;   // 첫번째 게시글 여부
    private boolean todayTheme;  // 현재 주제 여부


    @Builder
    public PublishBoardRes(String message, boolean firstPost, boolean todayTheme){
        this.message = message;
        this.firstPost = firstPost;
        this.todayTheme = todayTheme;
    }
}
