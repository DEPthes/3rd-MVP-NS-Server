package depth.mvp.ns.domain.report.domain.repository;

import depth.mvp.ns.domain.report.domain.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordCountRepository extends JpaRepository<WordCount, Long> {
}
