package depth.mvp.ns.domain.board.domain.repository;


import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryDslRepository {
    @Query("SELECT b FROM Board b WHERE b.theme = :theme AND b.isPublished = true")
    List<Board> findByTheme(@Param("theme") Theme theme);

    @Query("SELECT COUNT(l) FROM BoardLike l WHERE l.board.id = :boardId AND l.status = 'ACTIVE'")
    int countLikesByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT b FROM Board b WHERE b.theme.id = :themeId AND b.isPublished = true  ORDER BY b.createdDate DESC")
    Page<Board> findByThemeIdAndIsPublishedTrueOrderByDate(@Param("themeId") Long themeId, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.theme.id = :themeId AND b.isPublished = true ORDER BY (SELECT COUNT(bl) FROM BoardLike bl " +
            "WHERE bl.board.id = b.id AND bl.status = 'ACTIVE') DESC")
    Page<Board> findByThemeIdAndIsPublishedTrueOrderByLikeCount(@Param("themeId") Long themeId, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.theme = :theme AND b.isPublished = :isPublished")
    int countByThemeAndIsPublished(Theme theme, boolean isPublished);

    @Query("SELECT b FROM Board b WHERE b.user = :user AND b.isPublished = :isPublished")
    Page<Board> findPageBoardsByUserAndIsPublished(User user, boolean isPublished, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.user = :user AND b.isPublished = :isPublished")
    List<Board> findListBoardsByUserAndIsPublished(User user, boolean isPublished);

    @Query("SELECT b FROM Board b WHERE b.user = :user AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Board> findPagesByUserAndAndBoardFieldsContaining(User user, String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.user = :user AND b.isPublished = :isPublished AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Board> findPagesByUserAndIsPublishedAndBoardFieldsContaining(
            @Param("user") User user,
            @Param("isPublished") boolean isPublished,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT b FROM Board b WHERE b.user = :user AND b.isPublished = :isPublished AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Board> findListByUserAndIsPublishedAndBoardFieldsContaining(
            @Param("user") User user,
            @Param("isPublished") boolean isPublished,
            @Param("keyword") String keyword
    );

    @Query("SELECT b FROM Board b WHERE b.user = :user AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Board> findListByUserAndBoardFieldsContaining(User user, String keyword);

    Page<Board> findByUser(User user, Pageable pageable);

    List<Board> findByUser(User user);

    @Query("SELECT COUNT(bl) > 0 FROM BoardLike bl WHERE bl.board.id = :boardId AND bl.user.id = :userId AND bl.status = 'ACTIVE'")
    boolean isBoardLikedByUser(@Param("boardId") Long boardId, @Param("userId") Long userId);


    @Query("SELECT COUNT(b) FROM Board b WHERE b.user = :user AND b.isPublished = true")
    int countByUser(User user);
}
