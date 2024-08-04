package depth.mvp.ns.domain.board.controller;

import depth.mvp.ns.domain.board.dto.request.SaveDraftReq;
import depth.mvp.ns.domain.board.service.BoardService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/draft")
    public ResponseEntity<?> saveDraft(@CurrentUser CustomUserDetails userDetails, @RequestBody SaveDraftReq request) {
        return boardService.saveDraft(userDetails, request);
    }
}
