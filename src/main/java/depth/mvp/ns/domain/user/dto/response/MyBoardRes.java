package depth.mvp.ns.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyBoardRes {

    private Long boardId;

    private String theme;

    private String title;

    private int countLike;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private boolean isPublished;

    @Builder
    public MyBoardRes(Long boardId, String theme, String title, int countLike, LocalDateTime createdDate, boolean isPublished) {
        this.boardId = boardId;
        this.theme = theme;
        this.title = title;
        this.countLike = countLike;
        this.createdDate = createdDate;
        this.isPublished = isPublished;
    }
}
