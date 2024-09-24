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

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 8자리 이상이어야 합니다.")
    private String password;

    private String name;
    private String companyName;

    @Builder
    public JoinUserRequest(String email, String password, String name, String companyName) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.companyName = companyName;
    }

    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .companyName(companyName)
            .build();
    }
}
