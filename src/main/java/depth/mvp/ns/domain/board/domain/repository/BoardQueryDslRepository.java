package depth.mvp.ns.domain.board.domain.repository;

import com.querydsl.core.Tuple;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.dto.response.UserProfileRes;

import java.time.LocalDate;
import java.util.List;

public interface BoardQueryDslRepository {
    List<Board> findTop3BoardWithMostLiked(Theme theme);

    Board findLongestBoardByTheme(Theme theme);

    Tuple findMostLikedBoardCountAndTitleWithUserAndTheme(User user, Theme theme);

//    UserProfileRes findByUserId(Long userId);

    UserProfileRes findBoardListByUser(User user, Long currentUserId, int pageSize, int offset);

    boolean isBoardLikedByUser(Long id, Long id1);
}
