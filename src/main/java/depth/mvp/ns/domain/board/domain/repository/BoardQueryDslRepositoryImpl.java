package depth.mvp.ns.domain.board.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.QBoard;
import depth.mvp.ns.domain.board_like.domain.QBoardLike;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.QUser;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.dto.response.QUserProfileRes;
import depth.mvp.ns.domain.user.dto.response.QUserProfileRes_BoardListRes;
import depth.mvp.ns.domain.user.dto.response.UserProfileRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                .select(
                        board.id,
                        board.title,
                        board.createdDate,
                        QUser.user.id,
                        QUser.user.nickname,
                        QUser.user.imageUrl,
                        boardLike.id.count().coalesce(0L).as("likeCount")
                )
                .from(board)
                .join(board.user, QUser.user)
                .leftJoin(boardLike)
                .on(boardLike.board.eq(board)
                        .and(boardLike.status.eq(Status.ACTIVE)))
                .where(
                        board.user.eq(user),
                        board.theme.eq(theme)
                )
                .groupBy(
                        board.id,
                        board.title,
                        board.createdDate,
                        QUser.user.id,
                        QUser.user.nickname,
                        QUser.user.imageUrl
                )
                .orderBy(
                        boardLike.id.count().coalesce(0L).desc()
                )
                .limit(1)
                .fetchFirst();

    }

//    @Override
//    public UserProfileRes findByUserId(Long userId) {
//        User user = queryFactory
//                .selectFrom(QUser.user)
//                .where(QUser.user.id.eq(userId))
//                .fetchOne();
//
//        if (user == null) {
//            return null;
//        }
//
//        List<UserProfileRes.BoardListRes> boardListResList = queryFactory
//                .select(new QUserProfileRes_BoardListRes(
//                        QBoard.board.id,
//                        QBoard.board.title,
//                        QBoard.board.content,
//                        boardLike.count()
//                ))
//                .from(QBoard.board)
//                .leftJoin(boardLike).on(QBoard.board.id.eq(boardLike.board.id))
//                .where(QBoard.board.user.id.eq(userId))
//                .groupBy(QBoard.board.id, QBoard.board.title, QBoard.board.content)
//                .orderBy(QBoard.board.createdDate.desc())
//                .limit(3)
//                .fetch();
//
//        return new UserProfileRes(
//                user.getId(),
//                user.getNickname(),
//                user.getImageUrl(),
//                boardListResList
//        );
//    }

    @Override
    public UserProfileRes findBoardListByUser(User user, Long currentUserId, int pageSize, int offset) {

        List<Long> likedBoardIds = new ArrayList<>();


        if (currentUserId != null) {
            likedBoardIds = queryFactory
                    .select(boardLike.board.id)
                    .from(boardLike)
                    .where(boardLike.user.id.eq(currentUserId))
                    .fetch();
        }

        QBoard board = QBoard.board;
        QBoardLike boardLike = QBoardLike.boardLike;

        BooleanExpression isLikedExpression = currentUserId != null ? board.id.in(likedBoardIds) : Expressions.FALSE;

        List<UserProfileRes.BoardListRes> boardListResList = queryFactory
                .select(new QUserProfileRes_BoardListRes(
                        board.id,
                        board.title,
                        board.content,
                        boardLike.id.count(),
                        isLikedExpression
                ))
                .from(board)
                .leftJoin(boardLike)
                .on(board.id.eq(boardLike.board.id))
                .where(board.user.id.eq(user.getId()),
                        board.isPublished.eq(true))
                .groupBy(board.id, board.title, board.content)
                .orderBy(board.createdDate.desc())
                .offset(offset)
                .limit(pageSize)
                .fetch();

        return new UserProfileRes(
                user.getId(),
                user.getNickname(),
                user.getImageUrl(),
                boardListResList
        );
    }

    @Override
    public boolean isBoardLikedByUser(Long boardId, Long userId) {
        if (userId == null) {
            return false;
        }
        Integer count = queryFactory
                .selectOne()
                .from(boardLike)
                .where(
                        boardLike.board.id.eq(boardId),
                        boardLike.user.id.eq(userId),
                        boardLike.status.eq(Status.ACTIVE)
                )
                .fetchFirst();

        return count != null;
    }
}
