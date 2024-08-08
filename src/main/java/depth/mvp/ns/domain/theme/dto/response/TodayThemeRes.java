package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TodayThemeRes {
    private String content; // 오늘의 주제
    @Builder
    public TodayThemeRes(String content){
        this.content = content;
    }

}
