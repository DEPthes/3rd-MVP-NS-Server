package depth.mvp.ns.domain.theme.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.theme.dto.response.ThemeDetailRes;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme.dto.response.TodayThemeRes;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.error.InvalidParameterException;
import depth.mvp.ns.global.payload.ApiResponse;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;
    private  final BoardRepository boardRepository;
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

    // 주제 상세 조회
    public ResponseEntity<?> getThemeDetail(Long themeId, String sortBy, Pageable pageable) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND,  "주제를 찾을 수 없습니다."));

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
                            .userName(board.getUser().getUsername())
                            .date(board.getCreatedDate())
                            .likeCount(likeCount)
                            .build();
                }).collect(Collectors.toList());

        ThemeDetailRes themeDetailRes = ThemeDetailRes.builder()
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
}
