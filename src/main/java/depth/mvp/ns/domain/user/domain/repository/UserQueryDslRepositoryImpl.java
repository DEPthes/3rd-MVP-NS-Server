package depth.mvp.ns.domain.user.domain.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.user.domain.QUser;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.response.QUserInfoByNicknameRes;
import depth.mvp.ns.domain.user.dto.response.QUserRankingRes;
import depth.mvp.ns.domain.user.dto.response.UserInfoByNicknameRes;
import depth.mvp.ns.domain.user.dto.response.UserRankingRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import scala.sys.BooleanProp;

import java.util.List;

import static depth.mvp.ns.domain.user.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserRankingRes> getTop3ByPointDesc(RankingType type) {
        if (type.equals(RankingType.TOTAL)) {
            return queryFactory
                    .select(new QUserRankingRes(
                            user.id,
                            user.nickname,
                            user.imageUrl,
                            user.point
                    ))
                    .from(user)
                    .orderBy(user.point.desc())
                    .limit(3)
                    .fetch();
        }
        return null;
    }

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
}
