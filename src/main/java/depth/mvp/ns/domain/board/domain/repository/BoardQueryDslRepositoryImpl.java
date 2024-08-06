package depth.mvp.ns.domain.board.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.QBoard;
import depth.mvp.ns.domain.board_like.domain.QBoardLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static depth.mvp.ns.domain.board.domain.QBoard.board;
import static depth.mvp.ns.domain.board_like.domain.QBoardLike.boardLike;

@RequiredArgsConstructor
@Repository
public class BoardQueryDslRepositoryImpl implements BoardQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Board> findTop3BoardWithMostLiked() {
        return queryFactory
                .select(board)
                .from(board)
                .leftJoin(boardLike)
                .on(board.id.eq(boardLike.board.id))
                .groupBy(board.id)
                .orderBy(boardLike.count().desc())
                .limit(3)
                .fetch();
    }
}
