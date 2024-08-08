package depth.mvp.ns.domain.report.controller;

import depth.mvp.ns.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

//    @Scheduled(cron = "0 59 23 * * ?")
    @PostMapping("/generate")
    public void generateReport() {
        reportService.generateNReport();
    }
}
