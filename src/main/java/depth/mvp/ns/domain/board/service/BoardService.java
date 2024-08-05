package depth.mvp.ns.domain.board.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board.dto.request.PublishReq;
import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
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
    //게시글 임시 저장
    public ResponseEntity<?> saveDraft(CustomUserDetails userDetails, SaveDraftReq request) {
        // 유효한 사용자 확인
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 유효한 주제 확인
        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));

        // 제목 유효성 검사
        DefaultAssert.isTrue(request.getTitle() != null && !request.getTitle().isEmpty(), "제목을 입력해야 합니다.");

        Board board;
        if (request.getBoardId() != null) {
            // 기존 임시 저장 글이 있으면 글 업데이트
            board = boardRepository.findById(request.getBoardId())
                    .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "임시 저장된 게시글을 찾을 수 없습니다."));
            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            board.setLength(request.getContent().length());
            boardRepository.save(board);
        } else {
            // 새로운 임시 저장 글 생성
            board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .isPublished(false)
                    .length(request.getContent().length())
                    .user(user)
                    .theme(theme)
                    .build();
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("임시 저장이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);

    }
    @Transactional
    //게시글 게시
    public ResponseEntity<?> publishBoard(CustomUserDetails userDetails, PublishReq request) {
        // 유효한 사용자 확인
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 유효한 주제 확인
        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "주제를 찾을 수 없습니다."));

        //제목 유효성 검사
        DefaultAssert.isTrue(request.getTitle() != null && !request.getTitle().isEmpty(), "제목을 입력해야 합니다.");
        DefaultAssert.isTrue(request.getTitle().length() <=20, "제목은 20자 이내로 작성해야 합니다.");

        //내용 유효성 검사
        DefaultAssert.isTrue(request.getContent() != null && !request.getContent().isEmpty(), "본문을 입력해야 합니다.");
        DefaultAssert.isTrue(request.getContent().length() >= 100, "내용은 100자 이상이어야 합니다.");

        Board board;
        if (request.getBoardId() != null) {
            // 임시저장된 게시물을 게시할 경우 특정 게시물을 찾고 업데이트
            board = boardRepository.findById(request.getBoardId())
                    .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "게시물을 찾을 수 없습니다."));
            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            board.setPublished(true);
            board.setLength(request.getContent().length());
        } else {
            // 새로운 게시물 생성
            board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .isPublished(true)
                    .length(request.getContent().length())
                    .user(user)
                    .theme(theme)
                    .build();
        }

        boardRepository.save(board);

        // 게시 시 첫 게시글 작성 보너스를 부여
        if (!user.isCompleteFirstPost()) {
            user.addPoint(5);
            user.updateCompleteFirstPost(true);
            userRepository.save(user);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("게시글이 작성되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}
