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

    @Query("SELECT COUNT(b) FROM Board b WHERE b.theme.id = :themeId AND b.isPublished = true ")
    int countBoardsByThemeId(@Param("themeId") Long themeId);

    @Query("SELECT COUNT(l) FROM ThemeLike l WHERE l.theme.id = :themeId AND l.status = 'ACTIVE'")
    int countLikesByThemeId(@Param("themeId") Long themeId);

    Page<Theme> findByDateBeforeOrderByDateDesc(LocalDate date, Pageable pageable);

    @Query("SELECT t FROM Theme t WHERE t.date < :date ORDER BY (SELECT COUNT(l) FROM ThemeLike l WHERE l.theme = t AND l.status = 'ACTIVE') DESC")
    Page<Theme> findAllByDateBeforeOrderByLikeCount(@Param("date") LocalDate date, Pageable pageable);


    @Query("SELECT t FROM Theme t WHERE t.date < :date ORDER BY (SELECT COUNT(b) FROM Board b WHERE b.theme = t AND b.isPublished = true) DESC")
    Page<Theme> findAllByDateBeforeOrderByBoardCount(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT t FROM Theme t WHERE t.content LIKE %:keyword% AND t.date < :date ORDER BY t.date DESC")
    Page<Theme> searchByContentBeforeDate(@Param("keyword") String keyword, @Param("date") LocalDate date, Pageable pageable);


    @Query("SELECT t FROM Theme t " +
            "WHERE t.content LIKE %:keyword% " +
            "AND t.date < :date " +
            "ORDER BY (SELECT COUNT(l) FROM ThemeLike l WHERE l.theme = t AND l.status = 'ACTIVE') DESC")
    Page<Theme> searchByContentBeforeDateWithLikeCount(@Param("keyword") String keyword, @Param("date") LocalDate date, Pageable pageable);


    @Query("SELECT t FROM Theme t " +
            "WHERE t.content LIKE %:keyword% " +
            "AND t.date < :date " +
            "ORDER BY (SELECT COUNT(b) FROM Board b WHERE b.theme = t AND b.isPublished = true) DESC")
    Page<Theme> searchByContentBeforeDateWithBoardCount(@Param("keyword") String keyword, @Param("date") LocalDate date, Pageable pageable);


}
