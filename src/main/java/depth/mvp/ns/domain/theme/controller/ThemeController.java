package depth.mvp.ns.domain.theme.controller;

import depth.mvp.ns.domain.theme.service.ThemeService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/theme")
public class ThemeController {
    private final ThemeService themeService;
    @GetMapping("/today")
    public ResponseEntity<?> getTodayTheme(){
        return themeService.getTodayTheme();
    }

    @PostMapping("/{themeId}/like")
    public ResponseEntity<?> hitTheThemeLikeButton(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long themeId) {
        return themeService.hitLikeButton(customUserDetails, themeId);
    }
}
