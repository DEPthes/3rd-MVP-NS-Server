package depth.mvp.ns.domain.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wordcount_id")
    private Long id;

    private String word;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;


    @Builder
    public WordCount(String word, int count, Report report) {
        this.word = word;
        this.count = count;
        this.report = report;
    }
}
