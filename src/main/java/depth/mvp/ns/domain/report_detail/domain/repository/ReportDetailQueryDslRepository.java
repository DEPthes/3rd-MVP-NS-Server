package depth.mvp.ns.domain.report_detail.domain.repository;

import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.report_detail.domain.ReportDetail;

import java.util.List;

public interface ReportDetailQueryDslRepository {
    List<ReportDetail> findAllBestReportTypeByReport(Report report);

    Long findBestSelectedCountByUserId(Long userId);
}
