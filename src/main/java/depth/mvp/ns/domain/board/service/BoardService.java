package depth.mvp.ns.domain.board.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board.dto.request.PublishReq;
import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
import depth.mvp.ns.domain.board.dto.request.UpdateReq;
import depth.mvp.ns.domain.board.dto.response.BoardDetailRes;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
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
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;

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

    public ResponseEntity<?> getBoardDetail(Long boardId, CustomUserDetails customUserDetails) {
        Board board = validateBoard(boardId);

        // 회원인지 여부에 따른 처리
        Long userId = null;
        boolean isOwner = false;
        boolean liked = false;

        if (customUserDetails != null) {
            userId = customUserDetails.getId();
            isOwner = userId.equals(board.getUser().getId());
            liked = boardRepository.isBoardLikedByUser(boardId, userId);
        }

        BoardDetailRes boardDetailRes = BoardDetailRes.builder()
                .userId(board.getUser().getId())
                .isOwner(isOwner)
                .liked(liked)
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
