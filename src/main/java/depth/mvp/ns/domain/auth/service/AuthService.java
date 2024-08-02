package depth.mvp.ns.domain.auth.service;

import depth.mvp.ns.domain.auth.dto.request.CheckNicknameReq;
import depth.mvp.ns.domain.auth.dto.request.CheckUsernameReq;
import depth.mvp.ns.domain.auth.dto.response.CheckDuplicateRes;
import depth.mvp.ns.domain.auth.token.dto.RefreshTokenReq;
import depth.mvp.ns.domain.auth.dto.request.SignInReq;
import depth.mvp.ns.domain.auth.dto.request.SignUpReq;
import depth.mvp.ns.domain.auth.token.dto.TokenDto;
import depth.mvp.ns.domain.auth.token.domain.RefreshToken;
import depth.mvp.ns.domain.auth.token.domain.repository.RefreshTokenRepository;
import depth.mvp.ns.domain.s3.service.S3Uploader;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.config.security.token.TokenProvider;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;
    private final S3Uploader s3Uploader;

    @Transactional
    public ResponseEntity<?> signUp(SignUpReq signUpReq, boolean isDefault, Optional<MultipartFile> image){
        DefaultAssert.isTrue(!userRepository.existsByUsername(signUpReq.getUsername()), "이미 존재하는 사용자입니다.");
        DefaultAssert.isTrue(signUpReq.getPassword().equals(signUpReq.getCheckPassword()), "비밀번호가 일치하지 않습니다.");

        String imageName;
        String imageUrl;
        if (isDefault) {
            imageName = "default.png";  // 추후 다시 수정
            imageUrl = "https://ns-s3-image-bucket.s3.amazonaws.com/default.png";
        } else {
            imageName = s3Uploader.uploadImage(image.get());
            imageUrl = s3Uploader.getFullPath(imageName);
        }

        User user = User.builder()
                .username(signUpReq.getUsername())
                .password(passwordEncoder.encode(signUpReq.getPassword()))
                .nickname(signUpReq.getNickname())
                // imageUrl, imageName
                .imageName(imageName)
                .imageUrl(imageUrl)
                .build();

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("회원가입이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 아이디 중복 체크
    public ResponseEntity<?> checkUsername(CheckUsernameReq usernameReq) {
        boolean availableUsername = !userRepository.existsByUsername(usernameReq.getUsername());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(CheckDuplicateRes.builder()
                        .available(availableUsername)
                        .build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 중복 체크
    public ResponseEntity<?> checkNickname(CheckNicknameReq nicknameReq) {
        boolean availableNickname = !userRepository.existsByNickname(nicknameReq.getNickname());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(CheckDuplicateRes.builder()
                        .available(availableNickname)
                        .build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> signIn(SignInReq signInReq) {
        // 1. Username을 기반으로 사용자를 조회
        Optional<User> optionalUser = userRepository.findByUsername(signInReq.getUsername());
        DefaultAssert.isTrue(optionalUser.isPresent(), "유저 정보가 유효하지 않습니다.");
        User user = optionalUser.get();

        try {
            // 2. AuthenticationManager를 사용해 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInReq.getUsername(),
                            signInReq.getPassword()
                    )
            );

            // 3. TokenProvider를 사용해 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // 4. RefreshToken 생성
            RefreshToken refreshToken = RefreshToken.builder()
                    .username(user.getUsername())
                    .token(tokenDto.getRefreshToken())
                    .build();

            // 5. RefreshToken 저장 또는 업데이트
            refreshTokenRepository.findByUsername(user.getUsername())
                    .ifPresentOrElse(
                            existingToken -> existingToken.updateRefreshToken(tokenDto.getRefreshToken()), // 이미 존재하면 토큰 업데이트
                            () -> refreshTokenRepository.save(refreshToken) // 없으면 새로 저장
                    );

            // 6. 생성된 TokenDto를 반환
            return ResponseEntity.ok(tokenDto);

        } catch (Exception e) {
            log.error("인증 또는 토큰 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 오류 발생");
        }
    }

    @Transactional
    public ResponseEntity<?> signOut(CustomUserDetails customUserDetails) {
        log.info("userDetails:" + customUserDetails);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(customUserDetails.getUsername());
        DefaultAssert.isTrue(refreshToken.isPresent(), "이미 로그아웃 되었습니다");

        refreshTokenRepository.delete(refreshToken.get());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("로그아웃 되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }




    @Transactional
    public ResponseEntity<?> refresh(RefreshTokenReq refreshTokenReq) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshTokenReq.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 유저 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(refreshTokenReq.getAccessToken());

        // 3. 저장소에서 유저 정보를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getToken().equals(refreshTokenReq.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        refreshToken.updateRefreshToken(tokenDto.getRefreshToken());

        // 토큰 발급
        return ResponseEntity.ok(tokenDto);
    }

}