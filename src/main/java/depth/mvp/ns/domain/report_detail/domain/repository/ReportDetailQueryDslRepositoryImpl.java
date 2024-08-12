package depth.mvp.ns.domain.report_detail.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.report_detail.domain.ReportDetail;
import depth.mvp.ns.domain.report_detail.domain.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static depth.mvp.ns.domain.report_detail.domain.QReportDetail.reportDetail;

@RequiredArgsConstructor
@Repository
public class ReportDetailQueryDslRepositoryImpl implements ReportDetailQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReportDetail> findAllBestReportTypeByReport(Report report) {
        return queryFactory
                .select(reportDetail)
                .from(reportDetail)
                .where(reportDetail.report.eq(report),
                        reportDetail.reportType.eq(ReportType.BEST))
                .fetch();
    }

    @Override
    public Long findBestSelectedCountByUserId(Long userId) {
        return queryFactory
                .select(reportDetail.id.count())
                .from(reportDetail)
                .where(reportDetail.user.id.eq(userId),
                        reportDetail.reportType.eq(ReportType.BEST))
                .fetchOne();
    }
}
