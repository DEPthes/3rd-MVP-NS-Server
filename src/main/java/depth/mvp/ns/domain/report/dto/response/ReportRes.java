package depth.mvp.ns.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReportRes(
        LocalDate selectedDate,
        String themeName,
        int writtenTotal,
        LongestWriter longestWriter
) {
    @Builder
    public static record LongestWriter(
            Long userId,
            boolean isCurrentUser,
            String nickname,
            String imageUrl,
            int length
    ) {
    }
}
