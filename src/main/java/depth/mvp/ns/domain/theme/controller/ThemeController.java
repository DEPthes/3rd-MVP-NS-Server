package depth.mvp.ns.domain.theme.controller;

import depth.mvp.ns.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/list")
    public ResponseEntity<?> getThemeList(){
        return themeService.getThemeList();
    }

    @GetMapping("/sorted/date")
    public ResponseEntity<?> getThemeSortedByDate(){
        return themeService.getThemeSortedByDate();
    }
    @GetMapping("/sorted/like-count")
    public ResponseEntity<?> getThemeSortedByLikeCount(){
        return themeService.getThemeSortedByLikeCount();
    }

    @GetMapping("/sorted/board-count")
    public ResponseEntity<?> getThemeSortedByBoardCount(){
        return themeService.getThemeSortedByBoardCount();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTheme(@RequestParam String keyword){
        return themeService.searchTheme(keyword);
    }
 }
