package kr.codeit.relaxtogether.dto.user.response;

import kr.codeit.relaxtogether.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailsResponse {

    private String email;
    private String name;
    private String companyName;
    private String profileImage;

    @Builder
    private UserDetailsResponse(String email, String name, String companyName, String profileImage) {
        this.email = email;
        this.name = name;
        this.companyName = companyName;
        this.profileImage = profileImage;
    }

    public static UserDetailsResponse of(User user) {
        return UserDetailsResponse.builder()
            .email(user.getEmail())
            .name(user.getName())
            .companyName(user.getCompanyName())
            .profileImage(user.getProfileImage())
            .build();
    }
}
