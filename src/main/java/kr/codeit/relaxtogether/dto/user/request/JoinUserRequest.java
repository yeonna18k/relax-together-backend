package kr.codeit.relaxtogether.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.codeit.relaxtogether.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class JoinUserRequest {

    private static final String BASIC_PROFILE_IMAGE = "https://firebasestorage.googleapis.com/v0/b/relax-together.appspot.com/o/images%2Fprofile.svg?alt=media&token=45e21601-a6c5-46de-bbe4-d04f4e411122";

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 8자리 이상이어야 합니다.")
    private String password;

    private String name;
    private String companyName;
    private String profileImage;

    @Builder
    public JoinUserRequest(String email, String password, String name, String companyName) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.companyName = companyName;
        this.profileImage = BASIC_PROFILE_IMAGE;
    }

    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .companyName(companyName)
            .profileImage(profileImage)
            .build();
    }
}
