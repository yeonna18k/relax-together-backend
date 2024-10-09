package kr.codeit.relaxtogether.controller;

import static kr.codeit.relaxtogether.exception.ErrorCode.AUTHENTICATION_FAIL;
import static kr.codeit.relaxtogether.exception.ErrorCode.TOKEN_EXPIRED;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.auth.jwt.JwtUtil;
import kr.codeit.relaxtogether.dto.user.request.ChangePasswordRequest;
import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.dto.user.request.LoginRequest;
import kr.codeit.relaxtogether.dto.user.request.UpdateUserRequest;
import kr.codeit.relaxtogether.dto.user.response.JwtResponse;
import kr.codeit.relaxtogether.dto.user.response.UserDetailsResponse;
import kr.codeit.relaxtogether.entity.JwtToken;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.JwtTokenRepository;
import kr.codeit.relaxtogether.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auths")
@RestController
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "이메일 중복 확인", description = "해당 이메일을 가진 기존 유저가 있는지 확인합니다.")
    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody EmailCheckRequest emailCheckRequest) {
        return ResponseEntity.ok(userService.checkEmail(emailCheckRequest));
    }

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody JoinUserRequest joinUserRequest) {
        userService.signup(joinUserRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("success");
    }

    @Operation(summary = "로그인 사용자의 정보 조회", description = "로그인 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> loginUserDetails(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDetailsResponse userDetailsResponse = userService.getUserDetails(userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDetailsResponse);
    }

    @Operation(summary = "로그인 사용자의 정보 수정", description = "로그인 사용자의 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<String> update(@Valid @RequestBody UpdateUserRequest updateUserRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.update(updateUserRequest, userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body("success");
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword(), null);
        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
            String accessToken = jwtUtil.createAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.createRefreshToken(userDetails.getUsername());
            jwtTokenRepository.save(createJwtToken(refreshToken));
            response.addCookie(createCookieForRefreshToken(refreshToken));
            response.addCookie(createCookieForIsLoginUser("true"));
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(JwtResponse.builder()
                    .accessToken(accessToken)
                    .build());
        } catch (AuthenticationException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("잘못된 이메일 또는 비밀번호입니다.");
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ApiException(AUTHENTICATION_FAIL);
        }
        String accessToken = authorization.split(" ")[1];
        try {
            if (jwtUtil.ieExpired(accessToken)) {
                throw new ApiException(TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new ApiException(TOKEN_EXPIRED);
        }

        String refreshToken = getRefreshToken(request.getCookies());
        handleLogout(refreshToken, response);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("success");
    }

    @Operation(summary = "토큰 재발급", description = "기존 Refresh 토큰을 통해 Access 토큰과 Refresh 토큰을 새로 발급합니다.")
    @GetMapping("/refresh-token")
    public ResponseEntity<JwtResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        // Refresh 토큰 검증
        String refreshToken = getRefreshToken(request.getCookies());
        try {
            if (refreshToken == null
                || !jwtTokenRepository.existsByToken(refreshToken)
                || !jwtUtil.getType(refreshToken).equals("refresh")) {
                handleLogout(refreshToken, response);
                throw new ApiException(AUTHENTICATION_FAIL);
            }
            if (jwtUtil.ieExpired(refreshToken)) {
                handleLogout(refreshToken, response);
                throw new ApiException(TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            handleLogout(refreshToken, response);
            throw new ApiException(TOKEN_EXPIRED);
        }

        // Refresh 토큰 삭제
        jwtTokenRepository.deleteByToken(refreshToken);

        // 새 토큰들 생성 및 저장
        String newAccessToken = jwtUtil.createNewAccessToken(refreshToken);
        String newRefreshToken = jwtUtil.createNewRefreshToken(refreshToken);
        jwtTokenRepository.save(createJwtToken(newRefreshToken));
        response.addCookie(createCookieForRefreshToken(newRefreshToken));
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(JwtResponse.builder()
                .accessToken(newAccessToken)
                .build());
    }

    @Operation(summary = "비밀번호 분실 시, 비밀번호 변경", description = "비밀번호 분실 시에 새로운 비밀번호로 변경합니다.")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest passwordRequest) {
        userService.changePassword(passwordRequest);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body("success");
    }

    private void handleLogout(String refreshToken, HttpServletResponse response) {
        jwtTokenRepository.deleteByToken(refreshToken);
        response.addCookie(createCookieForDeleteRefreshToken());
        response.addCookie(createCookieForIsLoginUser("false"));
    }

    private JwtToken createJwtToken(String token) {
        return JwtToken.builder()
            .token(token)
            .build();
    }

    private String getRefreshToken(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Cookie createCookieForRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie createCookieForDeleteRefreshToken() {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie createCookieForIsLoginUser(String isLoginUser) {
        Cookie cookie = new Cookie("isLoginUser", isLoginUser);
        cookie.setMaxAge(60 * 60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
