package depth.mvp.ns.domain.theme.controller;

import depth.mvp.ns.domain.theme.service.ThemeService;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/theme")
public class ThemeController {
    private final ThemeService themeService;
    @GetMapping("/today")
    public ResponseEntity<?> getTodayTheme(@CurrentUser CustomUserDetails customUserDetails){
        return themeService.getTodayTheme(customUserDetails);
    }

    @GetMapping("/{themeId}/past")
    public ResponseEntity<?> getPastTheme(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long themeId){
        return themeService.getPastTheme(customUserDetails, themeId);
    }

    @PostMapping("/{themeId}/like")
    public ResponseEntity<?> hitTheThemeLikeButton(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long themeId) {
        return themeService.hitLikeButton(customUserDetails, themeId);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getThemeList(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return themeService.getThemeList(customUserDetails, pageable, sortBy);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTheme(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        return themeService.searchTheme(customUserDetails, keyword, pageable);
    }


    @GetMapping("/{themeId}")
    public ResponseEntity<?> getThemeDetail(
            @CurrentUser CustomUserDetails customUserDetails,
            @PathVariable Long themeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return themeService.getThemeDetail(themeId, sortBy, pageable, customUserDetails);
    }
}
