package depth.mvp.ns.domain.report.domain;

import depth.mvp.ns.domain.common.BaseEntity;
import depth.mvp.ns.domain.theme.domain.Theme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private int total; // 오늘 작성된 글의 총 갯수

    private String topWord;   // 사용자가 가장 많이 사용한 단어

    private int count;    // 사용한 횟수 ex) 107번 노출

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Builder
    public Report(int total, String topWord, int count, Theme theme) {
        this.total = total;
        this.topWord = topWord;
        this.count = count;
        this.theme = theme;
    }
}
