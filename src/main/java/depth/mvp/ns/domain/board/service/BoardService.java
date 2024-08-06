package depth.mvp.ns.domain.board.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board.dto.request.PublishReq;
import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
import depth.mvp.ns.domain.board.dto.request.UpdateReq;
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
        validateTitleForDraft(request.getTitle());

        Board board;
        if (request.getBoardId() != null) {
            // 기존 임시 저장 글이 있으면 글 업데이트
            board = validateBoard(request.getBoardId());
            updateBoardFields(board, request.getTitle(), request.getContent());
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
        validateTitle(request.getTitle());
        validateContent(request.getContent());

        Board board;
        if (request.getBoardId() != null) {
            // 임시저장된 게시물을 게시할 경우 특정 게시물을 찾고 업데이트
            board = validateBoard(request.getBoardId());
            updateBoardFields(board, request.getTitle(), request.getContent());
            board.setPublished(true);
        } else {
            board = createBoard(request.getTitle(), request.getContent(), true, user, theme);
        }

        boardRepository.save(board);

        // 게시 시 첫 게시글 작성 보너스를 부여
        if (!user.isCompleteFirstPost()) {
            user.addPoint(5);
            user.updateCompleteFirstPost(true);
        }

        //주제에 따른 포인트 부여
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
    public ResponseEntity<?> updateBoard(CustomUserDetails userDetails, UpdateReq request) {
        // 유효성 검사
        User user = validateUser(userDetails);
        Board board = validateBoard(request.getBoardId());
        validateTitle(request.getTitle());
        validateContent(request.getContent());

        updateBoardFields(board, request.getTitle(), request.getContent());

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
    // 제목 유효성 검사 (임시 저장용)
    private void validateTitleForDraft(String title) {
        DefaultAssert.isTrue(title != null && !title.isEmpty(), "제목을 입력해야 합니다.");
    }
    // 제목 유효성 검사
    private void validateTitle(String title){
        DefaultAssert.isTrue(title != null && !title.isEmpty(), "제목을 입력해야 합니다.");
        //DefaultAssert.isTrue(title.length() <= 20, "제목은 20자 이내로 작성해야 합니다.");
    }
    // 내용 유효성 검사
    private void validateContent(String content){
        DefaultAssert.isTrue(content != null && !content.isEmpty(), "내용을 입력해야 합니다.");
        //DefaultAssert.isTrue(content.length() >= 100, "내용은 100자 이상 작성해야 합니다.");
    }
    // 게시글 필드 업데이트
    private void updateBoardFields(Board board, String title, String content){
        board.setTitle(title);
        board.setContent(content);
        board.setLength(content.length());
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

}
