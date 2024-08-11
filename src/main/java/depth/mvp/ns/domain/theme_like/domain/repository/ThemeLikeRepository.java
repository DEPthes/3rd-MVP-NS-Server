package depth.mvp.ns.domain.theme_like.domain.repository;

import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeLikeRepository extends JpaRepository<ThemeLike, Long> {
    Optional<ThemeLike> findByUserAndTheme(User user, Theme theme);
}
