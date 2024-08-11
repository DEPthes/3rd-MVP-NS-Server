package depth.mvp.ns.domain.theme.service;

import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme.dto.response.ThemeLikeRes;
import depth.mvp.ns.domain.theme.dto.response.TodayThemeRes;
import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import depth.mvp.ns.domain.theme_like.domain.repository.ThemeLikeRepository;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ThemeLikeRepository themeLikeRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> getTodayTheme() {

        Theme theme = themeRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));


        TodayThemeRes todayThemeRes = TodayThemeRes.builder()
                .content(theme.getContent())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(todayThemeRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 주제 좋아요
    @Transactional
    public ResponseEntity<?> hitLikeButton(CustomUserDetails customUserDetails, Long themeId) {
        User user = validateUser(customUserDetails.getId());
        Theme theme = validateTheme(themeId);

        Optional<ThemeLike> optionalThemeLike = themeLikeRepository.findByUserAndTheme(user, theme);

        boolean isLiked = true;
        // 기존에 좋아요를 누르지 않은 경우
        if (optionalThemeLike.isEmpty()) {
            handleFirstLike(user, theme);
        } else {
            handleExistingLike(optionalThemeLike.get(), user);
            isLiked = optionalThemeLike.get().getStatus() == Status.ACTIVE;
        }

        ThemeLikeRes themeLikeRes = ThemeLikeRes.builder().liked(isLiked).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(themeLikeRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void handleFirstLike(User user, Theme theme) {
        ThemeLike themeLike = ThemeLike.builder()
                .user(user)
                .theme(theme)
                .build();
        themeLikeRepository.save(themeLike);

        // 최초 좋아요 시 사용자에게 포인트 부여
        user.addPoint(1);
    }

    private void handleExistingLike(ThemeLike themeLike, User user) {
        if (themeLike.getStatus() == Status.ACTIVE) {
            // 좋아요 취소
            themeLike.updateStatus(Status.DELETE);
            user.addPoint(-1);
        } else {
            // 좋아요 다시 활성화
            themeLike.updateStatus(Status.ACTIVE);
            user.addPoint(1);
        }
    }

    // 유효한 사용자 확인
    private User validateUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));
    }

    // 유효한 주제 확인
    private Theme validateTheme(Long themeId){
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));
    }
}
