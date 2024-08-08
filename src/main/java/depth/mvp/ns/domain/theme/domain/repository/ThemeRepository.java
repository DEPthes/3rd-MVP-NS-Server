package depth.mvp.ns.domain.theme.domain.repository;

import depth.mvp.ns.domain.theme.domain.Theme;
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

    @Query("SELECT COUNT(l) FROM ThemeLike l WHERE l.theme.id = :themeId")
    int countLikesByThemeId(@Param("themeId") Long themeId);

    @Query("SELECT t FROM Theme t ORDER BY t.date DESC")
    List<Theme> findAllOrderByDateDesc();

    @Query("SELECT t FROM Theme t ORDER BY (SELECT COUNT(l) FROM ThemeLike l WHERE l.theme = t) DESC")
    List<Theme> findAllOrderByLikeCountDesc();

    @Query("SELECT t FROM Theme t ORDER BY (SELECT COUNT(b) FROM Board b WHERE b.theme = t) DESC")
    List<Theme> findAllOrderByBoardCountDesc();

    @Query("SELECT t FROM Theme t WHERE t.content LIKE %:keyword%")
    List<Theme> findByContentContaining(@Param("keyword") String keyword);
}
