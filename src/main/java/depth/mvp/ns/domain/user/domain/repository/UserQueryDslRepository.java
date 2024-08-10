package depth.mvp.ns.domain.user.domain.repository;

import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.response.UserInfoByNicknameRes;
import depth.mvp.ns.domain.user.dto.response.UserRankingRes;

import java.util.List;

public interface UserQueryDslRepository {
    List<UserRankingRes> getTop3ByPointDesc(RankingType type);

    UserInfoByNicknameRes findByNickname(String nickname);
}
