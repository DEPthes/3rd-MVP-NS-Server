package depth.mvp.ns.domain.theme.dto.response;

import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ThemeDetailRes {
    private PageInfo pageInfo; // 페이지 정보
    private  Long userId; // 사용자ID
    private  boolean likedTheme; // 주제 좋아요 여부
    private Long themeId; // 주제ID
    private String content; // 주제 내용
    private String date; // 발행일
    private int likeCount; // 주제 좋아요 수
    private List<BoardRes> boards; // 게시글 목록

    @Builder
    public ThemeDetailRes(PageInfo pageInfo, Long userId, boolean likedTheme, Long themeId,
                          String content, LocalDate date, int likeCount, List<BoardRes> boards){
        this.pageInfo = pageInfo;
        this.userId = userId;
        this.likedTheme = likedTheme;
        this.themeId = themeId;
        this.content = content;
        this.date = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.likeCount = likeCount;
        this.boards = boards;
    }

    @Getter
    public static class BoardRes {
        private Long boardId; // 게시글ID
        private String title; // 게시글 제목
        private String content; // 게시글 내용
        private String nickname; // 작성자 닉네임
        private String date; // 게시글 작성일
        private int likeCount; // 게시글 좋아요 수
        private boolean likedBoard; // 게시글 좋아요 여부

        @Builder
        public BoardRes(Long boardId, String title, String content,
                        String nickname, LocalDateTime date, int likeCount, boolean likedBoard){
            this.boardId = boardId;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.date = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.likeCount =likeCount;
            this.likedBoard = likedBoard;
        }
    }
}
