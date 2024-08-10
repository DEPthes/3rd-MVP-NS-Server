package depth.mvp.ns.domain.user.controller;

import com.amazonaws.Response;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.dto.request.CheckPasswordReq;
import depth.mvp.ns.domain.user.dto.request.UpdateNicknameReq;
import depth.mvp.ns.domain.user.service.UserService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> findUserInformation(@CurrentUser CustomUserDetails customUserDetails) {
        return userService.getMyInfo(customUserDetails);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestPart boolean isDefault,
            @RequestPart Optional<MultipartFile> image) {
        return userService.updateImage(customUserDetails, isDefault, image);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<?> updateNickname(
            @CurrentUser CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateNicknameReq updateNicknameReq) {
        return userService.updateNickname(customUserDetails, updateNicknameReq);
    }

    @PostMapping("/check/password")
    public ResponseEntity<?> checkPassword(
            @CurrentUser CustomUserDetails customUserDetails,
            @Valid @RequestBody CheckPasswordReq checkPasswordReq) {
        return userService.checkPassword(customUserDetails, checkPasswordReq);
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(
            @RequestParam(required = false, defaultValue = "TOTAL") RankingType type
            ) {
        return ResponseEntity.ok(userService.getRankingData(type));
    }
}
