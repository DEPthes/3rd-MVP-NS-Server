package depth.mvp.ns.domain.board.controller;

import depth.mvp.ns.domain.board.dto.request.PublishReq;
import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
import depth.mvp.ns.domain.board.dto.request.UpdateReq;
import depth.mvp.ns.domain.board.service.BoardService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/draft")
    public ResponseEntity<?> saveDraft(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody SaveDraftReq request) {
        return boardService.saveDraft(userDetails, request);
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishBoard(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody PublishReq request) {
        return boardService.publishBoard(userDetails, request);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> updateBoard(@CurrentUser CustomUserDetails userDetails, @PathVariable Long boardId, @Valid @RequestBody UpdateReq request) {
        return boardService.updateBoard(userDetails, boardId, request);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@CurrentUser CustomUserDetails userDetails, @PathVariable Long boardId) {
        return boardService.deleteBoard(userDetails, boardId);
    }

}
