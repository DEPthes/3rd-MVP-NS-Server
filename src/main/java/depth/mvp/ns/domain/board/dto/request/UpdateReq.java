package depth.mvp.ns.domain.board.dto.request;

import lombok.Getter;

@Getter
public class UpdateReq {
    private Long boardId;    // 수정할 게시글ID
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용
}
