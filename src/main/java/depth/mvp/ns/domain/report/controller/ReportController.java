package depth.mvp.ns.domain.report.controller;

import depth.mvp.ns.domain.report.service.ReportService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @Scheduled(cron = "0 59 23 * * ?")
    public void generateReport() {
        reportService.generateNReport();
    }

    @GetMapping("/{selectedDate}")
    public ResponseEntity<?> getTodayReport(
            @CurrentUser CustomUserDetails customUserDetails,
            @PathVariable String selectedDate
    ) {
        LocalDate parsedDate = LocalDate.parse(selectedDate);
        if (customUserDetails != null) {
            return reportService.findReport(customUserDetails, parsedDate);
        }
        return reportService.findReport(null, parsedDate);
    }
}
