package kr.codeit.relaxtogether.dto.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtResponse {

    private String accessToken;

    @Builder
    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
