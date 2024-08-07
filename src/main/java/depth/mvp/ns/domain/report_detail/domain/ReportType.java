package depth.mvp.ns.domain.report_detail.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportType {
    BEST("BEST"),
    LONGEST("LONGEST");

    private String value;
}
