package depth.mvp.ns.domain.board.dto.request;

import lombok.Getter;

@Getter
public class PublishReq {
    private Long boardId;    // 게시할 게시글의 ID (게시하려는 게시글이 이미 존재할 때 사용)
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용
    private Long themeId;    // 게시글 테마 ID
}
