package depth.mvp.ns.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardLikeByUserRes {

    private Long boardId;

    private String theme;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private int countLike;

    @Builder
    public BoardLikeByUserRes(Long boardId, String theme, String title, LocalDateTime createdDate, int countLike) {
        this.boardId = boardId;
        this.theme = theme;
        this.title = title;
        this.createdDate = createdDate;
        this.countLike = countLike;
    }

}
