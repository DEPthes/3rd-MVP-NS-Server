package depth.mvp.ns.domain.board.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PublishReq {

    private Long boardId;    // 게시할 게시글의 ID (임시저장된 게시글이 있을 때 사용)

    @Size(min = 1, max = 20, message = "제목은 1자 이상 20자 이내로 작성해야 합니다.")
    private String title;    // 게시글 제목

    @Size(min = 100, message = "내용은 100자 이상이어야 합니다.")
    private String content;  // 게시글 내용

    private Long themeId;    // 주제 ID
}
