package depth.mvp.ns.domain.report.domain.repository;

import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, ReportQueryDslRepository {
    Optional<Report> findByTheme(Theme theme);
}
