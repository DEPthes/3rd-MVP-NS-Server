package depth.mvp.ns.domain.theme.service;

import depth.mvp.ns.domain.auth.dto.request.CheckUsernameReq;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme.dto.response.ThemeListRes;
import depth.mvp.ns.domain.theme.dto.response.TodayThemeRes;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;
    // 오늘의 주제 조회
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

    // 주제 목록 조회
    public ResponseEntity<?> getThemeList() {
        List<Theme> themeList = themeRepository.findAll();
        return buildThemeListResponse(themeList);
    }

    // 주제 발행일 최신순으로 정렬
    public ResponseEntity<?> getThemeSortedByDate() {
        List<Theme> themeList = themeRepository.findAllOrderByDateDesc();
        return buildThemeListResponse(themeList);
    }

    // 주제 좋아요순으로 정렬
    public ResponseEntity<?> getThemeSortedByLikeCount() {
        List<Theme> themeList = themeRepository.findAllOrderByLikeCountDesc();
        return buildThemeListResponse(themeList);
    }

    // 주제 게시글순으로 정렬
    public ResponseEntity<?> getThemeSortedByBoardCount() {
        List<Theme> themeList = themeRepository.findAllOrderByBoardCountDesc();
        return buildThemeListResponse(themeList);
    }
    // 주제 좋아요 누른 순으로 정렬(회원기준?)
//    public ResponseEntity<?> getThemeSortedByUserLike(CustomUserDetails customUserDetails) {
//
//    }

    // 주제 검색
    public ResponseEntity<?> searchTheme(String keyword) {
        List<Theme> themeList = themeRepository.findByContentContaining(keyword);
        return buildThemeListResponse(themeList);
    }

    // 주제 목록 처리 & 응답을 반환하는 메소드
    private ResponseEntity<ApiResponse> buildThemeListResponse(List<Theme> themeList) {
        List<ThemeListRes> themeListRes = themeList.stream()
                .map(theme -> {
                    int boardCount = themeRepository.countBoardsByThemeId(theme.getId()); // 게시글 수 계산
                    int likeCount = themeRepository.countLikesByThemeId(theme.getId());   // 주제 좋아요 수 계산

                    return ThemeListRes.builder()
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



}
