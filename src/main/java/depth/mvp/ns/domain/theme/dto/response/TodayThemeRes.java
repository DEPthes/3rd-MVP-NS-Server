package depth.mvp.ns.domain.theme.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodayThemeRes {
    private String content; // 오늘의 주제
}
