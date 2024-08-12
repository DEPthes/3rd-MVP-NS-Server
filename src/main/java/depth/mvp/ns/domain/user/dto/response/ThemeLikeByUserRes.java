package depth.mvp.ns.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ThemeLikeByUserRes {

    private Long themeId;

    private String theme;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private int countLike;

    private int countBoard;

    @Builder
    public ThemeLikeByUserRes(Long themeId, String theme, LocalDate date, int countLike, int countBoard) {
        this.themeId = themeId;
        this.theme = theme;
        this.date = date;
        this.countLike = countLike;
        this.countBoard = countBoard;
    }
}
