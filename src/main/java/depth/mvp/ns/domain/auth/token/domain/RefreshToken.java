package depth.mvp.ns.domain.auth.token.domain;

import depth.mvp.ns.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "token")
    private String token;

    protected RefreshToken() {
    }

    @Builder
    public RefreshToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public void updateRefreshToken(String token) { this.token = token; }
}
