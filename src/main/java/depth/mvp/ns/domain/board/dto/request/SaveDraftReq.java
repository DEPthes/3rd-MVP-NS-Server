package depth.mvp.ns.domain.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveDraftReq {
    private Long boardId; // 게시글 ID (기존 임시 저장된 게시글을 업데이트할 때 사용)
    private String title; //글제목
    private String content; //글내용
    private Long themeId; //주제ID
}
