package depth.mvp.ns.domain.user.dto;

import depth.mvp.ns.domain.user.dto.response.UserProfileRes;
import depth.mvp.ns.global.payload.PageInfo;
import lombok.Builder;

@Builder
public record UserProfileWithPageInfoRes(UserProfileRes userProfileRes, PageInfo pageInfo) {
}
