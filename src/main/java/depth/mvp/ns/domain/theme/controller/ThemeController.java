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
    public ResponseEntity<?> getTodayTheme(){
        return themeService.getTodayTheme();
    }

    @PostMapping("/{themeId}/like")
    public ResponseEntity<?> hitTheThemeLikeButton(@CurrentUser CustomUserDetails customUserDetails, @PathVariable Long themeId) {
        return themeService.hitLikeButton(customUserDetails, themeId);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getThemeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        Pageable pageable = PageRequest.of(page, size);
        return themeService.getThemeList(pageable, sortBy);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTheme(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size){
        Pageable pageable = PageRequest.of(page, size);
        return themeService.searchTheme(keyword, pageable);
    }


    @GetMapping("/{themeId}")
    public ResponseEntity<?> getThemeDetail(
            @PathVariable Long themeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        Pageable pageable = PageRequest.of(page, size);
        return themeService.getThemeDetail(themeId, sortBy, pageable);
    }
}
