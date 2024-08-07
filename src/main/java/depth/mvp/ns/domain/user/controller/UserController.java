package depth.mvp.ns.domain.user.controller;

import com.amazonaws.Response;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.service.UserService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> findUserInformation(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long userId) {
        return userService.getMyInfo(customUserDetails, userId);
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(
            @RequestParam(required = false, defaultValue = "TOTAL") RankingType type
            ) {
        return ResponseEntity.ok(userService.getRankingData(type));
    }
}
