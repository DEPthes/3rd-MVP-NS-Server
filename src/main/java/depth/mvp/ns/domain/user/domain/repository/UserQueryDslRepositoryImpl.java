package depth.mvp.ns.domain.user.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import depth.mvp.ns.domain.user.domain.QUser;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.response.QUserRankingRes;
import depth.mvp.ns.domain.user.dto.response.UserRankingRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
