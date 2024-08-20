package depth.mvp.ns.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SaveBoardRes {
    private String message; // 성공 메세지
    private Long boardId; // 임시저장된 글ID

    @Builder
    public SaveBoardRes(String message, Long boardId){
        this.message = message;
        this.boardId = boardId;
    }

}
