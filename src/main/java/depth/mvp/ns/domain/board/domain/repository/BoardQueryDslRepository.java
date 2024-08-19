package depth.mvp.ns.domain.board.domain.repository;

import com.querydsl.core.Tuple;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.dto.response.UserProfileRes;

import java.util.List;

public interface BoardQueryDslRepository {
    List<Board> findTop3BoardWithMostLiked();

    Board findLongestBoardByTheme(Theme theme);

    Tuple findMostLikedBoardCountAndTitleWithUserAndTheme(User user, Theme theme);

//    UserProfileRes findByUserId(Long userId);

    UserProfileRes findBoardListByUser(User user, Long currentUserId);
}
