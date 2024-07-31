package depth.mvp.ns.domain.theme_like.domain.repository;

import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeLikeRepository extends JpaRepository<ThemeLike, Long> {
}
