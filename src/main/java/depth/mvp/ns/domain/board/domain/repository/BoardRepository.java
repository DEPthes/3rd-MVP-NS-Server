package depth.mvp.ns.domain.board.domain.repository;


import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryDslRepository {
    List<Board> findByTheme(Theme theme);

    @Query("SELECT COUNT(l) > 0 FROM BoardLike l WHERE l.board.id = :boardId AND l.user.id = :userId AND l.status = 'ACTIVE'")
    boolean isBoardLikedByUser(@Param("boardId") Long boardId, @Param("userId") Long userId);
}
