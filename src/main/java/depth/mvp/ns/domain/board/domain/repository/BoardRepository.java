package depth.mvp.ns.domain.board.domain.repository;


import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryDslRepository {
    List<Board> findByTheme(Theme theme);

    @Query("SELECT COUNT(l) FROM BoardLike l WHERE l.board.id = :boardId AND l.status = 'ACTIVE'")
    int countLikesByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT b FROM Board b WHERE b.theme.id = :themeId ORDER BY b.createdDate DESC")
    Page<Board> findByThemeIdOrderByDate(@Param("themeId") Long themeId, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.theme.id = :themeId ORDER BY (SELECT COUNT(bl) FROM BoardLike bl " +
            "WHERE bl.board.id = b.id AND bl.status = 'ACTIVE') DESC")
    Page<Board> findByThemeIdOrderByLikeCount(@Param("themeId") Long themeId, Pageable pageable);
}
