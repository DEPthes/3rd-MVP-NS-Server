package depth.mvp.ns.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReportRes(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
