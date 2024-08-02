package depth.mvp.ns.domain.auth.controller;

import depth.mvp.ns.domain.auth.dto.request.SignInReq;
import depth.mvp.ns.domain.auth.dto.request.SignUpReq;
import depth.mvp.ns.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @Valid @RequestPart SignUpReq signUpReq
            //@RequestPart boolean isDefaultImage,
            //@RequestPart Optional<MultipartFile> image
    ) {
        return authService.signUp(signUpReq);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInReq signInReq) {
        return authService.signIn(signInReq);
    }

}
