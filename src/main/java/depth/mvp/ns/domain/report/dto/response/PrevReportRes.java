package depth.mvp.ns.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PrevReportRes(
        LocalDate selectedDate,
        String themeName,
        int writtenTotal,
        String wordCloud,
        String topWord,
        int count,
        ReportRes.LongestWriter longestWriter,
        List<BestPost> bestPost
) {
    @Builder
    public static record BestPost(
            String nickname,
            String imageUrl,
            String title,
            Long likeCount
    ) {
    }
}
