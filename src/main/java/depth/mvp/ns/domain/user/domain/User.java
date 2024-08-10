package depth.mvp.ns.domain.user.domain;

import depth.mvp.ns.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String imageUrl;

    private String imageName;

    private int point = 0;

    private boolean completeFirstPost = false;

    @Enumerated(EnumType.STRING)
    private final Role role = Role.USER;

    @Builder
    public User(String username, String password, String nickname, String imageUrl, String imageName) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    // 포인트 부여 메소드: 부여하고자 하는 포인트 숫자만큼 입력
    public void addPoint(int point) {
        this.point += point;
    }

    public void updateCompleteFirstPost(boolean b) { this.completeFirstPost = b; }

    public void updateImage(String imageUrl, String imageName) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

}
