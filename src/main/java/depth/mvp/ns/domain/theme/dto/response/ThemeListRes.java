package depth.mvp.ns.domain.theme.dto.response;

import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ThemeListRes {
    private Long userId; // 회원ID
    private PageInfo pageInfo; // 페이지 정보
    private List<ThemeList> themeList; // 주제 목록

    @Builder
    public ThemeListRes(Long userId, PageInfo pageInfo, List<ThemeList> themeList){
        this.userId = userId;
        this.pageInfo = pageInfo;
        this.themeList = themeList;
    }

    @Getter
    public static class ThemeList {
        private Long themeId; // 주제ID
        private String content; // 주제 내용
        private String date; // 주제 발행일
        private int likeCount; // 주제 좋아요수
        private int boardCount; // 게시글 수
        private boolean likedTheme; // 주제 좋아요 여부

        @Builder
        public ThemeList(Long themeId, String content, LocalDate date, int likeCount, int boardCount, boolean likedTheme) {
            this.themeId = themeId;
            this.content = content;
            this.date = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.likeCount = likeCount;
            this.boardCount = boardCount;
            this.likedTheme = likedTheme;
        }
    }

}

