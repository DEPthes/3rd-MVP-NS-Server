package depth.mvp.ns.domain.user.controller;

import com.amazonaws.Response;
import depth.mvp.ns.domain.user.service.UserService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> findUserInformation(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long userId) {
        return userService.getMyInfo(customUserDetails, userId);
    }
}
