package kr.codeit.relaxtogether.dto.email.response;

import lombok.Builder;

public class TokenVerificationResponse {

    private String email;

    @Builder
    public TokenVerificationResponse(String email) {
        this.email = email;
    }
}
