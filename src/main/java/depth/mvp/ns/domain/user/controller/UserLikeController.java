package depth.mvp.ns.domain.user.controller;

import depth.mvp.ns.domain.user.service.UserLikeService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user/like")
public class UserLikeController {

    private final UserLikeService userLikeService;

    @GetMapping("/board")
    public ResponseEntity<?> findLikedBoardsByUser(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "currentLike") String sortBy) {   // date, like, currentLike
        return userLikeService.getLikedBoardsByUser(customUserDetails, page, sortBy);
    }

    @GetMapping("/board/search")
    public ResponseEntity<?> searchLikedBoardsByUser(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "currentLike") String sortBy) {
        return userLikeService.searchLikedBoardsByUser(customUserDetails, page, keyword, sortBy);
    }

    @GetMapping("/theme")
    public ResponseEntity<?> findLikedThemesByUser(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "currentLike") String sortBy) {   // date, like, currentLike, board
        return userLikeService.getLikedThemesByUser(customUserDetails, page, sortBy);
    }

    @GetMapping("/theme/search")
    public ResponseEntity<?> searchLikedThemesByUser(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "currentLike") String sortBy) {
        return userLikeService.searchLikedThemesByUser(customUserDetails, page, keyword, sortBy);
    }
}
