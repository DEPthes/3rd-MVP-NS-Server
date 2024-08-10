package depth.mvp.ns.domain.board.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.QBoard;
import depth.mvp.ns.domain.board_like.domain.QBoardLike;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.QUser;
import depth.mvp.ns.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static depth.mvp.ns.domain.board.domain.QBoard.board;
import static depth.mvp.ns.domain.board_like.domain.QBoardLike.boardLike;
import static depth.mvp.ns.domain.user.domain.QUser.user;

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

    @Override
    public Board findLongestBoardByTheme(Theme theme) {
        return queryFactory
                .select(board)
                .from(board)
                .where(board.theme.eq(theme))
                .limit(1)
                .orderBy(board.length.desc())
                .fetchOne();
    }

    @Override
    public Tuple findMostLikedBoardCountAndTitleWithUserAndTheme(User user, Theme theme) {
        return queryFactory
                .select(boardLike.count(), board.title)
                .from(boardLike)
                .leftJoin(board).on(boardLike.board.id.eq(board.id))
                .where(board.user.eq(user),
                        board.theme.eq(theme))
                .groupBy(board.title)
                .orderBy(boardLike.count().desc())
                .limit(1)
                .fetchOne();
    }
}
