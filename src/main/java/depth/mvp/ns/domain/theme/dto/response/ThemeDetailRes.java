package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ThemeDetailRes {
    private String content; // 주제 내용
    private String date; // 발행일
    private int likeCount; // 주제 좋아요 수
    private List<BoardRes> boards; // 게시글 목록

    @Builder
    public ThemeDetailRes(String content, LocalDate date, int likeCount, List<BoardRes> boards){
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
        private String nickname; // 작성자 이름
        private String date; // 게시글 작성일
        private int likeCount; // 게시글 좋아요 수

        @Builder
        public BoardRes(Long boardId, String title, String content, String nickname, LocalDateTime date, int likeCount){
            this.boardId = boardId;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.date = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.likeCount =likeCount;
        }
    }
}
