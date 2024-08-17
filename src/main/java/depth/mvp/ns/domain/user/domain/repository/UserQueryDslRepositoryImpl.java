package depth.mvp.ns.domain.user.domain.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.user.domain.QUser;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.response.*;
import depth.mvp.ns.domain.user_point.domain.QUserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

        QUserPoint userPoint = QUserPoint.userPoint;
        QUser user = QUser.user;

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
                        Expressions.asNumber(userPoint.score.sum()).coalesce(0),
                        id != null ? user.id.eq(id) : Expressions.asBoolean(false)
                ))
                .from(userPoint)
                .leftJoin(user)
                .on(userPoint.user.id.eq(user.id))
                .where(userPoint.createdDate.between(startDate, endDate))
                .groupBy(user.id, user.nickname, user.imageUrl)
                .orderBy(Expressions.numberTemplate(Integer.class, "COALESCE({0}, {1})", userPoint.score.sum(), 0).desc())
                .limit(3)
                .fetch();


        AtomicBoolean isUserIncluded = new AtomicBoolean(false);
        AtomicLong ranking = new AtomicLong(1);

        // optionRankingUsers 리스트를 가져옵니다.
        List<UserRankingRes.OptionRankingRes> optionRankingUsers = queryFactory
                .select(new QUserRankingRes_OptionRankingRes(
                        Expressions.constant(0L), // 초기 랭킹
                        user.id,
                        user.nickname,
                        userPoint.score.sum(),
                        id != null ? user.id.eq(id) : Expressions.asBoolean(false)
                ))
                .from(userPoint)
                .leftJoin(user)
                .on(userPoint.user.id.eq(user.id))
                .where(userPoint.createdDate.between(startDate, endDate))
                .groupBy(user.id, user.nickname)
                .orderBy(userPoint.score.sum().desc())
                .fetch()
                .stream()
                .map(optionRankingUser -> {
                    boolean isCurrentUser = optionRankingUser.userId().equals(id);
                    if (isCurrentUser) {
                        isUserIncluded.set(true);
                    }
                    return new UserRankingRes.OptionRankingRes(
                            ranking.getAndIncrement(),
                            optionRankingUser.userId(),
                            optionRankingUser.nickname(),
                            optionRankingUser.point(),
                            isCurrentUser
                    );
                })
                .collect(Collectors.toList());

        // 현재 사용자가 포함되지 않았을 때 랭킹을 추가합니다.
        if (id != null && !isUserIncluded.get()) {
            UserRankingRes.OptionRankingRes userRanking = queryFactory
                    .select(new QUserRankingRes_OptionRankingRes(
                            Expressions.constant(ranking.get()), // 랭킹 설정
                            user.id,
                            user.nickname,
                            userPoint.score.sum(),
                            Expressions.asBoolean(true) // 현재 사용자임을 명확히 표시
                    ))
                    .from(userPoint)
                    .leftJoin(user)
                    .on(userPoint.user.id.eq(user.id))
                    .where(user.id.eq(id))
                    .groupBy(user.id, user.nickname)
                    .fetchOne();

            // 현재 사용자를 추가
            if (userRanking != null) {
                optionRankingUsers.add(userRanking);
            }
        }

        return UserRankingRes.builder()
                .top3UserRes(top3Users)
                .optionRankingRes(optionRankingUsers)
                .build();


    }
}
