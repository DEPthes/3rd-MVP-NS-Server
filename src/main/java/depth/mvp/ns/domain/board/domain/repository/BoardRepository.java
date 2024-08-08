package depth.mvp.ns.domain.board.domain.repository;


import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryDslRepository {
    List<Board> findByTheme(Theme theme);
}