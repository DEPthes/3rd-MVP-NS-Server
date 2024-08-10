package depth.mvp.ns.domain.user.service;

import com.sun.security.auth.UserPrincipal;
import depth.mvp.ns.domain.s3.service.S3Uploader;
import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.domain.user.dto.response.MyPageRes;
import depth.mvp.ns.domain.user.dto.response.UserRankingRes;
import depth.mvp.ns.global.config.security.token.CurrentUser;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.error.DefaultException;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import depth.mvp.ns.global.payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 마이페이지 내 정보 조회
    public ResponseEntity<?> getMyInfo(CustomUserDetails customUserDetails) {
        Optional<User> userOp = userRepository.findById(customUserDetails.getId());
        DefaultAssert.isTrue(userOp.isPresent(), "사용자가 존재하지 않습니다.");
        User user = userOp.get();

        MyPageRes myPageRes = MyPageRes.builder()
                .userId(customUserDetails.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(myPageRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 프로필 수정
    @Transactional
    public ResponseEntity<?> updateImage(@CurrentUser CustomUserDetails customUserDetails, boolean isDefault, Optional<MultipartFile> image) {
        User user = validUserById(customUserDetails.getId());
        if (!user.getImageName().contains("default")) {
            s3Uploader.deleteFile(user.getImageName());
        }

        String imageName;
        String imageUrl;
        if (isDefault) {
            imageName = "default.png";  // 추후 다시 수정
            imageUrl = "https://ns-s3-image-bucket.s3.amazonaws.com/default.png";
        } else if (!image.get().isEmpty()){
            imageName = s3Uploader.uploadImage(image.get());
            imageUrl = s3Uploader.getFullPath(imageName);
        } else {
            throw new InvalidParameterException("올바른 파라미터가 아닙니다.");
        }

        user.updateImage(imageUrl, imageName);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("프로필이 변경되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 수정
    // 비밀번호 일치 체크

    private User validUserById(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        DefaultAssert.isTrue(optionalUser.isPresent(), "유저 정보가 유효하지 않습니다.");
        return optionalUser.get();
    }

    public List<UserRankingRes> getRankingData(RankingType type) {
        return userRepository.getTop3ByPointDesc(type);
    }


}
