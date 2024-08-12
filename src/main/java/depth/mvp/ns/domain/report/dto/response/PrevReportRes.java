package depth.mvp.ns.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            boolean isCurrentUser,
            Long userId,
            String nickname,
            String imageUrl,
            String title,
            Long likeCount,
            Long bestSelectionCount,
            Long boardId,
            LocalDateTime boardCreatedAt
    ) {
    }
}
