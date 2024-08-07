package depth.mvp.ns.domain.theme.service;

import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme.dto.response.ThemeListRes;
import depth.mvp.ns.domain.theme.dto.response.TodayThemeRes;
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
        List<ThemeListRes> themeListRes = themeList.stream()
                .map(theme -> {
                    int boardCount = themeRepository.countBoardsByThemeId(theme.getId()); // 게시글 수 계산
                    int likeCount = themeRepository.countLikesByThemeId(theme.getId());   //  주제 좋아요 수 계산

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
    // 주제 발행일 최신순으로 정렬
    public ResponseEntity<?> getThemeSortedByDate() {
        List<Theme> themeList = themeRepository.findAllOrderByDateDesc();
        List<ThemeListRes> themeListRes = themeList.stream()
                .map(theme -> {
                    int boardCount = themeRepository.countBoardsByThemeId(theme.getId()); // 게시글 수 계산
                    int likeCount = themeRepository.countLikesByThemeId(theme.getId());   //  주제 좋아요 수 계산

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
