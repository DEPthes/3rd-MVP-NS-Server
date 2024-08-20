package depth.mvp.ns.domain.theme.domain.repository;

import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByDate(LocalDate data);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.theme.id = :themeId")
    int countBoardsByThemeId(@Param("themeId") Long themeId);

    @Query("SELECT COUNT(l) FROM ThemeLike l WHERE l.theme.id = :themeId AND l.status = 'ACTIVE'")
    int countLikesByThemeId(@Param("themeId") Long themeId);

    Page<Theme> findAllByOrderByDateDesc(Pageable pageable);

    @Query("SELECT t FROM Theme t ORDER BY (SELECT COUNT(l) FROM ThemeLike l WHERE l.theme = t AND l.status = 'ACTIVE') DESC")
    Page<Theme> findAllOrderByLikeCount(Pageable pageable);

    @Query("SELECT t FROM Theme t ORDER BY (SELECT COUNT(b) FROM Board b WHERE b.theme = t) DESC")
    Page<Theme> findAllOrderByBoardCount(Pageable pageable);

    @Query("SELECT t FROM Theme t WHERE t.content LIKE %:keyword% ORDER BY t.date DESC")
    Page<Theme> searchByContentWithDate(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM Theme t WHERE t.content LIKE %:keyword% ORDER BY (SELECT COUNT(l) FROM ThemeLike l WHERE l.theme = t AND l.status = 'ACTIVE') DESC")
    Page<Theme> searchByContentWithLikeCount(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM Theme t WHERE t.content LIKE %:keyword% ORDER BY (SELECT COUNT(b) FROM Board b WHERE b.theme = t) DESC")
    Page<Theme> searchByContentWithBoardCount(@Param("keyword") String keyword, Pageable pageable);

}
