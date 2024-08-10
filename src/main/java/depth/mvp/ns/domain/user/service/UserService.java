package depth.mvp.ns.domain.user.service;

import depth.mvp.ns.domain.user.domain.RankingType;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.domain.user.dto.response.MyPageRes;
import depth.mvp.ns.domain.user.dto.response.UserInfoByNicknameRes;
import depth.mvp.ns.domain.user.dto.response.UserRankingRes;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.payload.ApiResponse;
import depth.mvp.ns.global.payload.DefaultAssert;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

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

    public List<UserRankingRes> getRankingData(RankingType type) {
        return userRepository.getTop3ByPointDesc(type);
    }

    public ResponseEntity<?> getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        MyPageRes myPageRes = MyPageRes.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(myPageRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> getUInfoByNickname(Long userId, String nickname) {

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(EntityNotFoundException::new);

            UserInfoByNicknameRes byNickname = userRepository.findByNickname(nickname);

            UserInfoByNicknameRes myInfoByNickname = userRepository.findByNickname(user.getNickname());

            List<UserInfoByNicknameRes> userInfoByNicknameResList = new ArrayList<>();
            userInfoByNicknameResList.add(byNickname);
            userInfoByNicknameResList.add(myInfoByNickname);

            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(userInfoByNicknameResList)
                    .build();
            return ResponseEntity.ok(apiResponse);
        }

        UserInfoByNicknameRes byNickname = userRepository.findByNickname(nickname);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(byNickname)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
