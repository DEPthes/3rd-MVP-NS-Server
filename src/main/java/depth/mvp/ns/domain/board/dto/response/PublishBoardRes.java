package depth.mvp.ns.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublishBoardRes {

    private String message; // 성공 메세지
    private boolean firstPost;

    @Builder
    public PublishBoardRes(String message, boolean firstPost){
        this.message = message;
        this.firstPost = firstPost;
    }
}
