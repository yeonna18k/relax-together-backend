package kr.codeit.relaxtogether.dto.user.response;

import kr.codeit.relaxtogether.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailsResponse {

    private Long id;
    private String email;
    private String name;
    private String companyName;
    private String profileImage;

    @Builder
    public UserDetailsResponse(Long id, String email, String name, String companyName, String profileImage) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.companyName = companyName;
        this.profileImage = profileImage;
    }

    public static UserDetailsResponse of(User user) {
        return UserDetailsResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .companyName(user.getCompanyName())
            .profileImage(user.getProfileImage())
            .build();
    }
}
