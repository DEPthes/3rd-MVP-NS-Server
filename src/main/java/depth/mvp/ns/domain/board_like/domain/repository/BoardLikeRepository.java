package depth.mvp.ns.domain.board_like.domain.repository;

import depth.mvp.ns.domain.board_like.domain.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
}
