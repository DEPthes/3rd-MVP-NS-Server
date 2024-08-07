package depth.mvp.ns.domain.report_detail.domain.repository;

import depth.mvp.ns.domain.report_detail.domain.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long> {
}
