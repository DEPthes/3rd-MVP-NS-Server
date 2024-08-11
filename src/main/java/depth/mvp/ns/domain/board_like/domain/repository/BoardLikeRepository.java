package depth.mvp.ns.domain.board_like.domain.repository;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board_like.domain.BoardLike;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserAndBoard(User user, Board board);

    List<BoardLike> findAllByUserAndStatus(User user, Status status);

    Page<BoardLike> findByUserAndStatus(User user, Status status, Pageable pageable);

    int countByBoardAndStatus(Board board, Status status);
}
