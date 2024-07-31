package depth.mvp.ns.domain.board.domain;

import depth.mvp.ns.domain.common.BaseEntity;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;

    private String content;

    private boolean isPublished;

    private int length;

    // user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // theme
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Builder
    public Board(String title, String content, boolean isPublished, int length, User user, Theme theme) {
        this.title = title;
        this.content = content;
        this.isPublished = isPublished;
        this.length = length;
        this.user = user;
        this.theme = theme;
    }
}
