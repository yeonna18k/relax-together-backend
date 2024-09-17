package kr.codeit.relaxtogether.dto.user.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserRequest {

    private String companyName;
    private String profileImage;

    @Builder
    public UpdateUserRequest(String companyName, String profileImage) {
        this.companyName = companyName;
        this.profileImage = profileImage;
    }
}
