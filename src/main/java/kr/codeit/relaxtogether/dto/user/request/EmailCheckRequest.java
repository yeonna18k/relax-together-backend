package kr.codeit.relaxtogether.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailCheckRequest {

    @NotBlank
    @Email
    private String email;

    @Builder
    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
