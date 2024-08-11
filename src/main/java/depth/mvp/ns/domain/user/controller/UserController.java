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

    @GetMapping
    public ResponseEntity<?> findUserInformation(@CurrentUser CustomUserDetails customUserDetails) {
        return userService.getMyInfo(customUserDetails);
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(required = false, defaultValue = "TOTAL") RankingType type
            ) {
        if (customUserDetails != null) {
            return ResponseEntity.ok(userService.getRankingData(customUserDetails.getId(), type));
        }
        return ResponseEntity.ok(userService.getRankingData(null, type));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable(required = true) Long userId) {
        return userService.getProfile(userId);
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> getUserInfoByNickname(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(required = true) String nickname
    ) {
        if (customUserDetails != null) {
            return userService.getUInfoByNickname(customUserDetails.getId(), nickname);
        }
        return userService.getUInfoByNickname(null, nickname);
    }
}
