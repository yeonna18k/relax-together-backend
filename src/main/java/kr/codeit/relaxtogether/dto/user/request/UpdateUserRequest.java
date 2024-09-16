package kr.codeit.relaxtogether.dto.user.request;

import lombok.Getter;

@Getter
public class UpdateUserRequest {

    private String companyName;
    private String profileImage;

    public UpdateUserRequest(String companyName, String profileImage) {
        this.companyName = companyName;
        this.profileImage = profileImage;
    }
}
