package depth.mvp.ns.domain.report.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.board.domain.QBoard;
import depth.mvp.ns.domain.theme.domain.Theme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static depth.mvp.ns.domain.board.domain.QBoard.board;

@RequiredArgsConstructor
@Repository
public class ReportQueryDslRepositoryImpl implements ReportQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public int getBoardCount(Theme theme) {

        Long count = queryFactory
                .select(board.count())
                .from(board)
                .where(board.theme.eq(theme))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }
}
