package depth.mvp.ns.domain.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveDraftReq {
    private String title; //글제목
    private String content; //글내용
    private Long themeId; //주제ID
}
