package depth.mvp.ns.domain.board.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board.dto.request.PublishReq;
import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
import depth.mvp.ns.domain.board.dto.request.UpdateReq;
import depth.mvp.ns.domain.board.dto.response.BoardLikeRes;
import depth.mvp.ns.domain.board.dto.response.ThemeLikeRes;
import depth.mvp.ns.domain.board_like.domain.BoardLike;
import depth.mvp.ns.domain.board_like.domain.repository.BoardLikeRepository;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.board.dto.response.BoardDetailRes;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.theme_like.domain.repository.ThemeLikeRepository;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import depth.mvp.ns.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ThemeLikeRepository themeLikeRepository;

    @Transactional
    // 게시글 임시 저장
    public ResponseEntity<?> saveDraft(CustomUserDetails userDetails, SaveDraftReq request) {
        // 유효성 검사
        User user = validateUser(userDetails);
        Theme theme = validateTheme(request.getThemeId());

        Board board;
        if (request.getBoardId() != null) {
            // 기존 임시 저장 글이 있으면 글 업데이트
            board = validateBoard(request.getBoardId());
            board.updateBoard(request.getTitle(),request.getContent(),request.getContent().length());
        } else {
            board = createBoard(request.getTitle(), request.getContent(), false, user, theme);
        }

        boardRepository.save(board);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("임시 저장이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);

    }
    @Transactional
    // 게시글 게시
    public ResponseEntity<?> publishBoard(CustomUserDetails userDetails, PublishReq request) {
        // 유효성 검사
        User user = validateUser(userDetails);
        Theme theme = validateTheme(request.getThemeId());

        Board board;
        if (request.getBoardId() != null) {
            // 임시저장된 게시물을 게시할 경우 특정 게시물을 찾고 업데이트
            board = validateBoard(request.getBoardId());
            board.updateBoard(request.getTitle(), request.getContent(), request.getContent().length());
            board.updateIsPublished(true);

        } else {
            board = createBoard(request.getTitle(), request.getContent(), true, user, theme);
        }

        boardRepository.save(board);

        // 게시 시 첫 게시글 작성 보너스를 부여
        if (!user.isCompleteFirstPost()) {
            user.addPoint(5);
            user.updateCompleteFirstPost(true);
        }

        // 주제에 따른 포인트 부여
        LocalDate today = LocalDate.now();
        LocalDate themeDate = theme.getDate();

        if(themeDate.isEqual(today)){
            user.addPoint(3);
        } else {
            user.addPoint(2);
        }

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("게시글이 작성되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);

    }
    // 게시 글 수정
    @Transactional
    public ResponseEntity<?> updateBoard(CustomUserDetails userDetails,Long boardId, UpdateReq request) {
        // 유효성 검사
        User user = validateUser(userDetails);
        Board board = validateBoard(boardId);

        board.updateBoard(request.getTitle(), request.getContent(), request.getContent().length());

        boardRepository.save(board);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("게시글이 수정되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
    // 게시글 삭제
    @Transactional
    public ResponseEntity<?> deleteBoard(CustomUserDetails userDetails, Long boardId) {
        // 유효성 검사
        User user = validateUser(userDetails);
        Board board = validateBoard(boardId);

        boardRepository.delete(board);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("게시글이 삭제되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 유효한 사용자 확인
    private User validateUser(CustomUserDetails userDetails){
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));
    }

    // 유효한 주제 확인
    private Theme validateTheme(Long themeId){
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));
    }

    // 유효한 게시글 확인
    private Board validateBoard(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }

    // 새로운 게시글 생성
    private Board createBoard(String title, String content, boolean isPublished, User user, Theme theme){
        return Board.builder()
                .title(title)
                .content(content)
                .isPublished(isPublished)
                .length(content.length())
                .user(user)
                .theme(theme)
                .build();
    }

    // 게시글 좋아요
    @Transactional
    public ResponseEntity<?> hitLikeButton(CustomUserDetails customUserDetails, Long boardId) {
        User user = validateUser(customUserDetails);
        Board board = validateBoard(boardId);

        Optional<BoardLike> optionalBoardLike = boardLikeRepository.findByUserAndBoard(user, board);

        boolean isLiked = true;
        if (optionalBoardLike.isEmpty()) {
            // 기존에 좋아요를 누르지 않은 경우
            handleFirstLike(user, board);
        } else {
            // 기존에 좋아요를 누른 경우: 상태에 따라 처리
            handleExistingLike(optionalBoardLike.get(), user, board);
            isLiked = optionalBoardLike.get().getStatus() == Status.ACTIVE;
        }

        BoardLikeRes boardLikeRes = BoardLikeRes.builder().liked(isLiked).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(boardLikeRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void handleFirstLike(User user, Board board) {
        BoardLike boardLike = BoardLike.builder()
                .user(user)
                .board(board)
                .build();
        boardLikeRepository.save(boardLike);

        // 사용자 및 게시물 작성자에게 포인트 부여
        user.addPoint(1);
        board.getUser().addPoint(1);
    }

    private void handleExistingLike(BoardLike boardLike, User user, Board board) {
        if (boardLike.getStatus() == Status.ACTIVE) {
            // 좋아요 취소
            boardLike.updateStatus(Status.DELETE);
            user.addPoint(-1);
            board.getUser().addPoint(-1);
        } else {
            // 좋아요 다시 활성화
            boardLike.updateStatus(Status.ACTIVE);
            user.addPoint(1);
            board.getUser().addPoint(1);
        }
    }
    // 게시글 조회
    public ResponseEntity<?> getBoardDetail(Long boardId, CustomUserDetails customUserDetails) {
        Board board = validateBoard(boardId);
        Theme theme = validateTheme(board.getTheme().getId());

        // 회원인지 여부에 따른 처리
        Long userId = null;
        boolean owner = false;
        boolean likedBoard = false;
        boolean likedTheme = false;

        if (customUserDetails != null) {
            User user = validateUser(customUserDetails);
            userId = user.getId();
            // 사용자 본인이 쓴 게시물인지 확인
            owner = userId.equals(board.getUser().getId());
            // 사용자가 특정 게시물에 좋아요를 눌렀는지 여부 확인
            likedBoard = boardLikeRepository.existsByBoardAndUserAndStatus(board, user, Status.ACTIVE);
            // 사용자가 특정 주제에 좋아요를 눌렀는지 여부 확인
            likedTheme = themeLikeRepository.existsByThemeAndUserAndStatus(theme, user, Status.ACTIVE);
        }

        BoardDetailRes boardDetailRes = BoardDetailRes.builder()
                .userId(board.getUser().getId())
                .owner(owner)
                .likedBoard(likedBoard)
                .likedTheme(likedTheme)
                .nickname(board.getUser().getNickname())
                .imageUrl(board.getUser().getImageUrl())
                .themeContent(board.getTheme().getContent())
                .boardTitle(board.getTitle())
                .boardContent(board.getContent())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(boardDetailRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}
