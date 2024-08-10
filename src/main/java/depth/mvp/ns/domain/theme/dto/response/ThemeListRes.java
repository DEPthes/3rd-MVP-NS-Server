package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ThemeListRes {
    private Long themeId; // 주제 목록
    private String content; // 주제 내용
    private LocalDate date; // 주제 발행일
    private int likeCount; // 주제 좋아요수
    private int boardCount; // 게시글 수

    @Builder
    ThemeListRes(Long themeId, String content, LocalDate date, int likeCount, int boardCount){
        this.themeId = themeId;
        this.content = content;
        this.date = date;
        this.likeCount = likeCount;
        this.boardCount = boardCount;
    }
}

