package depth.mvp.ns.domain.user.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board_like.domain.BoardLike;
import depth.mvp.ns.domain.board_like.domain.repository.BoardLikeRepository;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import depth.mvp.ns.domain.theme_like.domain.repository.ThemeLikeRepository;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.domain.user.dto.response.BoardLikeByUserRes;
import depth.mvp.ns.domain.user.dto.response.MyBoardRes;
import depth.mvp.ns.domain.user.dto.response.PageRes;
import depth.mvp.ns.domain.user.dto.response.ThemeLikeByUserRes;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import depth.mvp.ns.global.payload.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLikeService {

    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final ThemeLikeRepository themeLikeRepository;
    private final BoardRepository boardRepository;

    // Description: 좋아요를 누른 게시글
    // 좋아요 누른 글 조회 + 정렬
    public ResponseEntity<?> getLikedBoardsByUser(CustomUserDetails customUserDetails, int page, int size, String sortBy) {
        User user = validUserById(customUserDetails.getId());

        List<BoardLikeByUserRes> boardLikeByUserResList;
        List<BoardLike> allBoardLikes = boardLikeRepository.findAllByUserAndStatus(user, Status.ACTIVE);
        // currentLike는 다른 로직 적용
        if (!Objects.equals(sortBy, "currentLike")) {
            boardLikeByUserResList = processBoardLikes(allBoardLikes);
            // date, like 정렬 적용
            sortBoardLikes(boardLikeByUserResList, sortBy);
            // 페이징 적용
            boardLikeByUserResList = applyPagination(boardLikeByUserResList, page, size);
        } else {
            // currentLike 기준 정렬 및 페이징
            boardLikeByUserResList = sortBoardByCurrentLike(user, page, size, null);
        }

        PageInfo pageInfo = createPageInfo(allBoardLikes.size(), page, size);
        PageRes<BoardLikeByUserRes> pageBoardLikeRes = PageRes.<BoardLikeByUserRes>builder()
                .pageInfo(pageInfo)
                .resList(boardLikeByUserResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageBoardLikeRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private List<BoardLikeByUserRes> processBoardLikes(List<BoardLike> boardLikeList) {
        return boardLikeList.stream()
                .map(boardLike -> boardLike.getBoard())
                .map(board -> BoardLikeByUserRes.builder()
                        .boardId(board.getId())
                        .theme(board.getTheme().getContent())
                        .title(board.getTitle())
                        .createdDate(board.getCreatedDate())
                        .countLike(boardLikeRepository.countByBoardAndStatus(board, Status.ACTIVE))
                        .build())
                .collect(Collectors.toList());
    }

    // 정렬 메소드
    private void sortBoardLikes(List<BoardLikeByUserRes> boardLikeByUserResList, String sortBy) {
        switch (sortBy) {
            case "date":
                boardLikeByUserResList.sort(Comparator.comparing(BoardLikeByUserRes::getCreatedDate, Comparator.reverseOrder()));
                break;
            case "like":
                boardLikeByUserResList.sort(Comparator.comparing(BoardLikeByUserRes::getCountLike, Comparator.reverseOrder()));
                break;
            default:
                throw new InvalidParameterException("잘못된 요청 파라미터입니다.");
        }
    }

    // currentLike 기준으로 정렬 및 페이징
    private List<BoardLikeByUserRes> sortBoardByCurrentLike(User user, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<BoardLike> boardLikePage;
        // 조회, 검색 동일한 메소드 사용하므로 keyword 유무로 구분
        if (keyword == null) {
            // 좋아요한 게시글 조회
            boardLikePage = boardLikeRepository.findByUserAndStatus(user, Status.ACTIVE, pageable);
        } else {
            // 좋아요한 게시글 검색
            boardLikePage = boardLikeRepository.findPagesByUserAndStatusAndBoardFieldsContaining(
                    user, Status.ACTIVE, keyword, pageable);
        }

        return boardLikePage.stream()
                .map(boardLike -> {
                    Board board = boardLike.getBoard();
                    return BoardLikeByUserRes.builder()
                            .boardId(board.getId())
                            .theme(board.getTheme().getContent())
                            .title(board.getTitle())
                            .createdDate(board.getCreatedDate())
                            .countLike(boardLikeRepository.countByBoardAndStatus(board, Status.ACTIVE))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 수동 페이징 메소드
    private <T> List<T> applyPagination(List<T> sortedList, int page, int size) {
        int start = (page - 1) * size;
        int end = Math.min(start + size, sortedList.size());
        return sortedList.subList(start, end);
    }

    private PageInfo createPageInfo(int totalElements, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageInfo(
                page,
                size,
                totalElements,
                totalPages
        );
    }

    // 검색
    public ResponseEntity<?> searchLikedBoardsByUser(CustomUserDetails customUserDetails, int page, int size, String keyword, String sortBy) {
        User user = validUserById(customUserDetails.getId());
        List<BoardLikeByUserRes> boardLikeByUserResList;
        List<BoardLike> allBoardLikes =  boardLikeRepository.findByUserAndStatusAndBoardFieldsContaining(
                user, Status.ACTIVE, keyword);

        // currentLike는 다른 로직 적용
        if (!Objects.equals(sortBy, "currentLike")) {
            boardLikeByUserResList = processBoardLikes(allBoardLikes);
            // date, like 정렬 적용
            sortBoardLikes(boardLikeByUserResList, sortBy);
            // 페이징 적용
            boardLikeByUserResList = applyPagination(boardLikeByUserResList, page, size);
        } else {
            // currentLike 기준 정렬 및 페이징
            boardLikeByUserResList = sortBoardByCurrentLike(user, page, size, keyword);
        }

        PageInfo pageInfo = createPageInfo(allBoardLikes.size(), page, size);
        PageRes<BoardLikeByUserRes> pageBoardLikeRes = PageRes.<BoardLikeByUserRes>builder()
                .pageInfo(pageInfo)
                .resList(boardLikeByUserResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageBoardLikeRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // Description: 좋아요를 누른 주제
    // 주제 조회
    public ResponseEntity<?> getLikedThemesByUser(CustomUserDetails customUserDetails, int page, int size, String sortBy) {
        // date, like, currentLike, board
        User user = validUserById(customUserDetails.getId());

        List<ThemeLikeByUserRes> themeLikeByUserResList;
        List<ThemeLike> allThemeLikes = themeLikeRepository.findAllByUserAndStatus(user, Status.ACTIVE);

        if (!Objects.equals(sortBy, "currentLike")) {
            themeLikeByUserResList = processThemeLikes(allThemeLikes);
            // date, like, board 정렬 적용
            sortThemeLikes(themeLikeByUserResList, sortBy);
            // 페이징 적용
            themeLikeByUserResList = applyPagination(themeLikeByUserResList, page, size);
        } else {
            // currentLike 기준 정렬 및 페이징
            themeLikeByUserResList = sortThemeByCurrentLike(user, page, size, null);
        }

        PageInfo pageInfo = createPageInfo(allThemeLikes.size(), page, size);
        PageRes<ThemeLikeByUserRes> pageThemeLikeRes = PageRes.<ThemeLikeByUserRes>builder()
                .pageInfo(pageInfo)
                .resList(themeLikeByUserResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageThemeLikeRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private List<ThemeLikeByUserRes> processThemeLikes(List<ThemeLike> themeLikeList) {
        return themeLikeList.stream()
                .map(themeLike -> themeLike.getTheme())
                .map(theme -> ThemeLikeByUserRes.builder()
                        .themeId(theme.getId())
                        .theme(theme.getContent())
                        .date(theme.getDate())
                        .countBoard(boardRepository.countByThemeAndIsPublished(theme, true))
                        .countLike(themeLikeRepository.countByThemeAndStatus(theme, Status.ACTIVE))
                .build())
                .collect(Collectors.toList());
    }

    // 정렬 메소드
    private void sortThemeLikes(List<ThemeLikeByUserRes> themeLikeByUserResList, String sortBy) {
        switch (sortBy) {
            case "date":
                themeLikeByUserResList.sort(Comparator.comparing(ThemeLikeByUserRes::getDate, Comparator.reverseOrder()));
                break;
            case "like":
                themeLikeByUserResList.sort(Comparator.comparing(ThemeLikeByUserRes::getCountLike, Comparator.reverseOrder()));
                break;
            case "board":
                themeLikeByUserResList.sort(Comparator.comparing(ThemeLikeByUserRes::getCountBoard, Comparator.reverseOrder()));
                break;
            default:
                throw new InvalidParameterException("잘못된 요청 파라미터입니다.");
        }
    }

    // currentLike 기준으로 정렬 및 페이징
    private List<ThemeLikeByUserRes> sortThemeByCurrentLike(User user, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<ThemeLike> themeLikePage;

        if (keyword == null) {
            // 좋아요한 주제 조회
            themeLikePage = themeLikeRepository.findByUserAndStatus(user, Status.ACTIVE, pageable);
        } else {
            // 좋아요한 주제 검색
            themeLikePage = themeLikeRepository.findPagesByUserAndStatusAndThemeFieldsContaining(
                    user, Status.ACTIVE, keyword, pageable);
        }

        return themeLikePage.stream()
                .map(themeLike -> {
                    Theme theme = themeLike.getTheme();
                    return ThemeLikeByUserRes.builder()
                            .themeId(theme.getId())
                            .theme(theme.getContent())
                            .date(theme.getDate())
                            .countBoard(boardRepository.countByThemeAndIsPublished(theme, true))
                            .countLike(themeLikeRepository.countByThemeAndStatus(theme, Status.ACTIVE))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 주제 검색
    public ResponseEntity<?> searchLikedThemesByUser(CustomUserDetails customUserDetails, int page, int size, String keyword, String sortBy) {
        User user = validUserById(customUserDetails.getId());
        List<ThemeLikeByUserRes> themeLikeByUserResList;
        List<ThemeLike> allThemeLikes =  themeLikeRepository.findByUserAndStatusAndThemeFieldsContaining(
                user, Status.ACTIVE, keyword);

        // currentLike는 다른 로직 적용
        if (!Objects.equals(sortBy, "currentLike")) {
            themeLikeByUserResList = processThemeLikes(allThemeLikes);
            // date, like, board 정렬 적용
            sortThemeLikes(themeLikeByUserResList, sortBy);
            // 페이징 적용
            themeLikeByUserResList = applyPagination(themeLikeByUserResList, page, size);
        } else {
            // currentLike 기준 정렬 및 페이징
            themeLikeByUserResList = sortThemeByCurrentLike(user, page, size, keyword);
        }

        PageInfo pageInfo = createPageInfo(allThemeLikes.size(), page, size);
        PageRes<ThemeLikeByUserRes> pageThemeLikeRes = PageRes.<ThemeLikeByUserRes>builder()
                .pageInfo(pageInfo)
                .resList(themeLikeByUserResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageThemeLikeRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 내 글
    public ResponseEntity<?> getMyBoards(CustomUserDetails customUserDetails, int page, int size, boolean filterDrafts, String sortBy) {
        // 최신순, 좋아요 순
        User user = validUserById(customUserDetails.getId());
        // 임시저장 제외 여부에 따라 totalElements 달라짐
        List<Board> allMyBoards;
        if (filterDrafts) {
            // 임시저장 제외 필터 체크한 경우: isPublished가 true인 것만 가져옴
            allMyBoards = boardRepository.findListBoardsByUserAndIsPublished(user, true);
        } else {
            // 임시저장 제외 필터 체크하지 않은 경우: isPublished 상관없이 가져옴
            allMyBoards = boardRepository.findByUser(user);
        }

        List<MyBoardRes> myBoardResList;
        switch (sortBy) {
            case "date":
                myBoardResList = sortByDate(user, page, size, filterDrafts, null);
                break;
            case "like":
                myBoardResList = sortByLike(user, filterDrafts, null);
                myBoardResList.sort(Comparator.comparing(MyBoardRes::getCountLike, Comparator.reverseOrder()));
                myBoardResList = applyPagination(myBoardResList, page, size);
                break;
            default:
                throw new InvalidParameterException("잘못된 요청 파라미터입니다.");
        }

        PageInfo pageInfo = createPageInfo(allMyBoards.size(), page, size);
        PageRes<MyBoardRes> pageMyBoardRes = PageRes.<MyBoardRes>builder()
                .pageInfo(pageInfo)
                .resList(myBoardResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageMyBoardRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> searchMyBoards(CustomUserDetails customUserDetails, int page, int size, String keyword, boolean filterDrafts, String sortBy) {
        // 최신순, 좋아요 순
        User user = validUserById(customUserDetails.getId());

        List<Board> allMyBoards;
        // 임시저장 제외 여부에 따라 totalElements 달라짐
        if (filterDrafts) {
            allMyBoards = boardRepository.findListByUserAndIsPublishedAndBoardFieldsContaining(
                    user, true, keyword);
        } else {
            allMyBoards = boardRepository.findListByUserAndBoardFieldsContaining(user, keyword);
        }

        List<MyBoardRes> myBoardResList;
        switch (sortBy) {
            case "date":
                myBoardResList = sortByDate(user, page, size, filterDrafts, keyword);
                break;
            case "like":
                myBoardResList = sortByLike(user, filterDrafts, keyword);
                myBoardResList.sort(Comparator.comparing(MyBoardRes::getCountLike, Comparator.reverseOrder()));
                myBoardResList = applyPagination(myBoardResList, page, size);
                break;
            default:
                throw new InvalidParameterException("잘못된 요청 파라미터입니다.");
        }

        PageInfo pageInfo = createPageInfo(allMyBoards.size(), page, size);
        PageRes<MyBoardRes> pageMyBoardRes = PageRes.<MyBoardRes>builder()
                .pageInfo(pageInfo)
                .resList(myBoardResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(pageMyBoardRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 임시저장 제외 여부, 검색어 여부에 따른 로직 분리
    private List<Board> getFilteredBoards(User user, boolean filterDrafts, String keyword, Pageable pageable) {
        if (filterDrafts) {
            // 임시저장 제외
            if (keyword == null) {
                // 내 게시글 조회
                return pageable == null
                        ? boardRepository.findListBoardsByUserAndIsPublished(user, true)
                        : boardRepository.findPageBoardsByUserAndIsPublished(user, true, pageable).getContent();
            } else {
                // 내 게시글 검색
                return pageable == null
                        ? boardRepository.findListByUserAndIsPublishedAndBoardFieldsContaining(user, true, keyword)
                        : boardRepository.findPagesByUserAndIsPublishedAndBoardFieldsContaining(user, true, keyword, pageable).getContent();
            }
        } else {
            // 임시저장 포함
            if (keyword == null) {
                // 내 게시글 조회
                return pageable == null
                        ? boardRepository.findByUser(user)
                        : boardRepository.findByUser(user, pageable).getContent();
            } else {
                // 내 게시글 검색
                return pageable == null
                        ? boardRepository.findListByUserAndBoardFieldsContaining(user, keyword)
                        : boardRepository.findPagesByUserAndAndBoardFieldsContaining(user, keyword, pageable).getContent();
            }
        }
    }

    private List<MyBoardRes> sortByDate(User user, int page, int size, boolean filterDrafts, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Board> boardList = getFilteredBoards(user, filterDrafts, keyword, pageable);

        return boardList.stream()
                .map(board -> MyBoardRes.builder()
                        .boardId(board.getId())
                        .theme(board.getTheme().getContent())
                        .title(board.getTitle())
                        .createdDate(board.getCreatedDate())
                        .countLike(boardLikeRepository.countByBoardAndStatus(board, Status.ACTIVE))
                        .isPublished(board.findIsPublished())
                        .liked(boardLikeRepository.existsByUserAndBoardAndStatus(user, board, Status.ACTIVE))
                        .build())
                .collect(Collectors.toList());
    }

    private List<MyBoardRes> sortByLike(User user, boolean filterDrafts, String keyword) {
        List<Board> boardList = getFilteredBoards(user, filterDrafts, keyword, null);

        return boardList.stream()
                .map(board -> MyBoardRes.builder()
                        .boardId(board.getId())
                        .theme(board.getTheme().getContent())
                        .title(board.getTitle())
                        .createdDate(board.getCreatedDate())
                        .countLike(boardLikeRepository.countByBoardAndStatus(board, Status.ACTIVE))
                        .isPublished(board.findIsPublished())
                        .liked(boardLikeRepository.existsByUserAndBoardAndStatus(user, board, Status.ACTIVE))
                        .build())
                .collect(Collectors.toList());
    }

    private User validUserById (Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        DefaultAssert.isTrue(optionalUser.isPresent(), "유저 정보가 유효하지 않습니다.");
        return optionalUser.get();
    }

}
