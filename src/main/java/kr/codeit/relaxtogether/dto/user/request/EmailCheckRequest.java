package kr.codeit.relaxtogether.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailCheckRequest {

    @NotBlank
    @Email
    private String email;

    @Builder
    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
