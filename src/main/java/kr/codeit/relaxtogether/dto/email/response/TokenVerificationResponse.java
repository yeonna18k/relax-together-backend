package kr.codeit.relaxtogether.dto.email.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenVerificationResponse {

    private String email;

    @Builder
    public TokenVerificationResponse(String email) {
        this.email = email;
    }
}
