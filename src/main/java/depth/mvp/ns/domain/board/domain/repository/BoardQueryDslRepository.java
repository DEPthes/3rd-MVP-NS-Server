package depth.mvp.ns.domain.board.domain.repository;

import depth.mvp.ns.domain.board.domain.Board;

import java.util.List;

public interface BoardQueryDslRepository {
    List<Board> findTop3BoardWithMostLiked();
}
