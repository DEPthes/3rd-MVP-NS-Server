package depth.mvp.ns.domain.board.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveDraftReq {

    private Long boardId; // 게시글 ID (기존 임시 저장된 게시글을 업데이트할 때 사용)

    @Size(min = 1, max = 20, message = "제목은 1자 이상 20자 이내로 작성해야 합니다.")
    private String title; // 글제목

    private String content; // 글내용

    private Long themeId; // 주제ID
}
