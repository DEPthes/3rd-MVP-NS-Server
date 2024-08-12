package depth.mvp.ns.domain.user.dto.response;

import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageRes<T> {

    private PageInfo pageInfo;
    private List<T> resList;
}
