package depth.mvp.ns.domain.theme.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.user_point.domain.UserPoint;
import depth.mvp.ns.domain.user_point.domain.repository.UserPointRepository;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme.dto.response.ThemeDetailRes;
import depth.mvp.ns.domain.theme.dto.response.ThemeLikeRes;
import depth.mvp.ns.domain.theme.dto.response.ThemeListRes;
import depth.mvp.ns.domain.theme.dto.response.TodayThemeRes;
import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import depth.mvp.ns.domain.theme_like.domain.repository.ThemeLikeRepository;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.error.InvalidParameterException;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import depth.mvp.ns.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ThemeLikeRepository themeLikeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final UserPointRepository userPointRepository;

    // 오늘의 주제 조회
    public ResponseEntity<?> getTodayTheme(@CurrentUser CustomUserDetails customUserDetails) {
        Theme theme = themeRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));

        // 회원인지 여부에 따른 처리
        Long userId = null;
        boolean likedTheme = false; // 주제 좋아요 여부

        // 주제에 대한 좋아요 여부 확인하고 응답값 넘겨주기
        if(customUserDetails != null){
            User user = validateUser(customUserDetails.getId());
            userId = user.getId();
            likedTheme = themeLikeRepository.existsByThemeAndUserAndStatus(theme, user, Status.ACTIVE);
        }

        TodayThemeRes todayThemeRes = TodayThemeRes.builder()
                .content(theme.getContent())
                .userId(userId)
                .likedTheme(likedTheme)
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

        int score = 1;
        // 최초 좋아요 시 사용자에게 포인트 부여
        user.addPoint(score);
        // 포인트 내역 저장
        savePointHistory(user, score);
    }

    private void handleExistingLike(ThemeLike themeLike, User user) {
        int score = -1;
        if (themeLike.getStatus() == Status.ACTIVE) {
            // 좋아요 취소
            themeLike.updateStatus(Status.DELETE);
            user.addPoint(score);
            // 포인트 내역 삭제
            deletePointHistory(user, themeLike.getCreatedDate().toLocalDate(), Math.abs(score));
        } else {
            score = 1;
            // 좋아요 다시 활성화
            themeLike.updateStatus(Status.ACTIVE);
            user.addPoint(score);
            // 포인트 내역 저장
            savePointHistory(user, score);
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

    // 주제 상세 조회
    public ResponseEntity<?> getThemeDetail(Long themeId, String sortBy, Pageable pageable, CustomUserDetails customUserDetails) {
        Theme theme = validateTheme(themeId);
        Page<Board> boardPage;

        switch (sortBy) {
            case "likeCount":
                boardPage = boardRepository.findByThemeIdOrderByLikeCount(themeId, pageable);
                break;
            case "date":
                boardPage = boardRepository.findByThemeIdOrderByDate(themeId, pageable);
                break;
            default:
                Errors errors = new BindException(sortBy, "sortBy");
                errors.rejectValue("sortBy", "invalid", "잘못된 정렬 파라미터입니다.");
                throw new InvalidParameterException(errors);
        }

        List<ThemeDetailRes.BoardRes> boardResList = boardPage.getContent().stream()
                .map(board -> {
                    int likeCount = boardRepository.countLikesByBoardId(board.getId()); // 게시글 좋아요 수 계산

                    return ThemeDetailRes.BoardRes.builder()
                            .boardId(board.getId())
                            .title(board.getTitle())
                            .content(board.getContent())
                            .nickname(board.getUser().getNickname())
                            .date(board.getCreatedDate())
                            .likeCount(likeCount)
                            .build();
                }).collect(Collectors.toList());

        // 회원인지 여부에 따른 처리
        Long userId = null;
        boolean likedTheme = false; // 주제 좋아요 여부

        // 주제에 대한 좋아요 여부 확인하고 응답값 넘겨주기
        if(customUserDetails != null){
            User user = validateUser(customUserDetails.getId());
            userId = user.getId();
            likedTheme = themeLikeRepository.existsByThemeAndUserAndStatus(theme, user, Status.ACTIVE);
        }

        ThemeDetailRes themeDetailRes = ThemeDetailRes.builder()
                .userId(userId)
                .likedTheme(likedTheme)
                .content(theme.getContent())
                .date(theme.getDate())
                .likeCount(themeRepository.countLikesByThemeId(themeId))
                .boards(boardResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(themeDetailRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 주제 목록 조회
    public ResponseEntity<?> getThemeList(Pageable pageable, String sortBy) {
        Page<Theme> themePage;

        switch (sortBy) {
            case "likeCount":
                themePage = themeRepository.findAllOrderByLikeCount(pageable);
                break;
            case "boardCount":
                themePage = themeRepository.findAllOrderByBoardCount(pageable);
                break;
            case "date":
                themePage = themeRepository.findAllByOrderByDateDesc(pageable);
                break;
            default:
                Errors errors = new BindException(sortBy, "sortBy");
                errors.rejectValue("sortBy", "invalid", "잘못된 정렬 파라미터입니다.");
                throw new InvalidParameterException(errors);
        }

        return buildThemeListResponse(themePage);
    }

    // 주제 검색
    public ResponseEntity<?> searchTheme(String keyword,Pageable pageable) {
        Page<Theme> themePage = themeRepository.findByContentContaining(keyword, pageable);
        return buildThemeListResponse(themePage);
    }

    // 주제 목록 처리 & 응답을 반환하는 메소드
    private ResponseEntity<ApiResponse> buildThemeListResponse(Page<Theme> themePage) {
        List<ThemeListRes> themeListRes = themePage.getContent().stream()
                .map(theme -> {
                    int boardCount = themeRepository.countBoardsByThemeId(theme.getId()); // 게시글 수 계산
                    int likeCount = themeRepository.countLikesByThemeId(theme.getId());   // 주제 좋아요 수 계산

                    return ThemeListRes.builder()
                            .themeId(theme.getId())
                            .content(theme.getContent())
                            .date(theme.getDate())
                            .likeCount(likeCount)
                            .boardCount(boardCount)
                            .build();
                }).collect(Collectors.toList());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(themeListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void savePointHistory(User user, int score) {
        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .score(score)
                .build();
        userPointRepository.save(userPoint);
    }

    private void deletePointHistory(User user, LocalDate date, int score) {
        // 부여된 날짜 및 score로 point 찾기
        Optional<UserPoint> pointOptional = userPointRepository.findByUserAndCreatedDateAndScore(user, date, score);
        DefaultAssert.isTrue(pointOptional.isPresent(), "포인트 내역이 존재하지 않습니다.");
        UserPoint userPoint = pointOptional.get();

        userPointRepository.delete(userPoint);
    }

}
