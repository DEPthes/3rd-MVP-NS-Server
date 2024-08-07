package depth.mvp.ns.domain.theme.controller;

import depth.mvp.ns.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/theme")
public class ThemeController {
    private final ThemeService themeService;
    @GetMapping("/today")
    public ResponseEntity<?> getTodayTheme(){
        return themeService.getTodayTheme();
    }
}
