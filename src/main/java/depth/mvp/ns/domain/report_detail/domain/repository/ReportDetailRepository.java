package depth.mvp.ns.domain.report_detail.domain.repository;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.report_detail.domain.ReportDetail;
import depth.mvp.ns.domain.report_detail.domain.ReportType;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long>, ReportDetailQueryDslRepository {

    boolean existsByUserAndBoardAndReportType(User user, Board board, ReportType reportType);
}
