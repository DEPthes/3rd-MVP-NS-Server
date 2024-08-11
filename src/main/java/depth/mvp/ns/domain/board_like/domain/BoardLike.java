package depth.mvp.ns.domain.board_like.domain;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.common.BaseEntity;
import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public BoardLike(User user, Board board) {
        this.user = user;
        this.board = board;
    }

}
