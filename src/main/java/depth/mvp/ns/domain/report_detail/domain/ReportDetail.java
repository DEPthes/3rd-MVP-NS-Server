package depth.mvp.ns.domain.report_detail.domain;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.common.BaseEntity;
import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_detali_id")
    private Long id;

    // user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // report
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;


    @Builder
    public ReportDetail(User user, Report report, Board board, ReportType reportType) {
        this.user = user;
        this.report = report;
        this.board = board;
        this.reportType = reportType;
    }
}
