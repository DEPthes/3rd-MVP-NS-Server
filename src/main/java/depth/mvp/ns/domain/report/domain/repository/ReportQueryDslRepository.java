package depth.mvp.ns.domain.report.domain.repository;

import depth.mvp.ns.domain.theme.domain.Theme;

import java.time.LocalDate;

public interface ReportQueryDslRepository {
    int getBoardCount(Theme theme);
}
