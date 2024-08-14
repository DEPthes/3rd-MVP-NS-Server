package depth.mvp.ns.domain.user.domain.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.point.domain.QPoint;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static depth.mvp.ns.domain.point.domain.QPoint.point;
import static depth.mvp.ns.domain.user.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserInfoByNicknameRes findByNickname(String nickname) {
        UserInfoByNicknameRes userInfo = queryFactory
                .select(new QUserInfoByNicknameRes(
                                Expressions.constant(0L), // 랭킹
                                user.id,
                                user.nickname,
                                user.point
                        )
                )
                .from(user)
                .where(user.nickname.eq(nickname))
                .fetchOne();

        Long rank = queryFactory
                .select(user.count())
                .from(user)
                .where(user.point.gt(userInfo.point()))
                .fetchOne();

        return new UserInfoByNicknameRes(
                rank + 1,
                userInfo.userId(),
                userInfo.nickname(),
                userInfo.point()
        );

    }

    @Override
    public UserRankingRes getNRankingDesc(Long id, RankingType type) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (type) {
            case DAILY:
                startDate = endDate.toLocalDate().atStartOfDay();
                break;
            case WEEKLY:
                startDate = endDate.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
                break;
            case MONTHLY:
                startDate = endDate.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case TOTAL:
            default:
                startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
                break;
        }

        List<UserRankingRes.Top3UserRes> top3Users = queryFactory
                .select(new QUserRankingRes_Top3UserRes(
                        user.id,
                        user.nickname,
                        user.imageUrl,
                        point.score.sum(),
                        id != null ? user.id.eq(id) : Expressions.asBoolean(false)
                ))
                .from(point)
                .leftJoin(user)
                .on(point.user.id.eq(user.id))
                .where(point.createdDate.between(startDate, endDate))
                .groupBy(user.id, user.nickname, user.imageUrl)
                .orderBy(point.score.sum().desc())
                .limit(3)
                .fetch();

        List<UserRankingRes.OptionRankingRes> optionRankingUsers = queryFactory
                .select(new QUserRankingRes_OptionRankingRes(
                        Expressions.constant(0L), // 랭킹
                        user.id,
                        user.nickname,
                        point.score.sum(),
                        id != null ? user.id.eq(id) : Expressions.asBoolean(false)
                ))
                .from(point)
                .leftJoin(user)
                .on(point.user.id.eq(user.id))
                .where(point.createdDate.between(startDate, endDate))
                .groupBy(user.id, user.nickname)
                .orderBy(point.score.sum().desc())
                .limit(10)
                .fetch();


        AtomicLong ranking = new AtomicLong(1);
        optionRankingUsers = optionRankingUsers.stream()
                .map(optionRankingUser -> new UserRankingRes.OptionRankingRes(
                        ranking.getAndIncrement(),
                        optionRankingUser.userId(),
                        optionRankingUser.nickname(),
                        optionRankingUser.point(),
                        optionRankingUser.isCurrentUser()
                ))
                .collect(Collectors.toList());

        optionRankingUsers = optionRankingUsers.stream()
                .filter(optionRankingUser -> !optionRankingUser.isCurrentUser())
                .limit(9)
                .collect(Collectors.toList());

        if (id != null) {
            UserRankingRes.OptionRankingRes userRanking = queryFactory
                    .select(new QUserRankingRes_OptionRankingRes(
                            Expressions.constant(0L),
                            user.id,
                            user.nickname,
                            point.score.sum(),
                            user.id.eq(id)
                    ))
                    .from(user)
                    .where(user.id.eq(id))
                    .fetchOne();

            optionRankingUsers.add(0, userRanking);
        }

        return UserRankingRes.builder()
                .top3UserRes(top3Users)
                .optionRankingRes(optionRankingUsers)
                .build();

    }
}
