package depth.mvp.ns.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardDetailRes {
    private Long userId; // 게시글 작성자
    private boolean owner; // 본인 확인 여부
    private boolean likedBoard; // 게시글 좋아요 여부
    private  boolean likedTheme; // 주제 좋아요 여부
    private String nickname; // 작성자 닉네임
    private String imageUrl; // 작성자 프사
    private Long themeId; // 주제ID
    private String themeContent; // 주제 내용
    private String boardTitle; // 게시글 제목
    private String boardContent; // 게시글 내용
    private boolean published;
    private boolean myBestBoard; // 해당 글이 내가 쓴 베스트 게시글인지 확인

    @Builder
    public BoardDetailRes(Long userId, boolean owner, boolean likedBoard, boolean likedTheme, String nickname,
                          String imageUrl, Long themeId, String themeContent, String boardTitle, String boardContent, boolean published, boolean myBestBoard){
        this.userId = userId;
        this.owner = owner;
        this.likedBoard = likedBoard;
        this.likedTheme = likedTheme;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.themeId = themeId;
        this.themeContent = themeContent;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.published = published;
        this.myBestBoard = myBestBoard;
    }
}
