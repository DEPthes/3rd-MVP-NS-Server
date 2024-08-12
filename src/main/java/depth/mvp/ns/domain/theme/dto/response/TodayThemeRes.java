package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TodayThemeRes {
    private String content; // 오늘의 주제
    private Long userId; // 사용자ID
    private boolean likedTheme; // 주제 좋아요 여부

    @Builder
    public TodayThemeRes(String content, Long userId, boolean likedTheme){
        this.content = content;
        this.userId = userId;
        this.likedTheme = likedTheme;
    }

}
