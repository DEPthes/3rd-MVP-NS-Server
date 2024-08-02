package depth.mvp.ns.domain.auth.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckDuplicateRes {

    private boolean available;

    @Builder
    public CheckDuplicateRes(boolean available) { this.available = available; }
}
