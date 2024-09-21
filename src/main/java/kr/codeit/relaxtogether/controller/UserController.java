package kr.codeit.relaxtogether.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.auth.jwt.JwtUtil;
import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.dto.user.request.LoginRequest;
import kr.codeit.relaxtogether.dto.user.request.UpdateUserRequest;
import kr.codeit.relaxtogether.dto.user.response.UserDetailsResponse;
import kr.codeit.relaxtogether.entity.JwtToken;
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

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody EmailCheckRequest emailCheckRequest) {
        return ResponseEntity.ok(userService.checkEmail(emailCheckRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody JoinUserRequest joinUserRequest) {
        userService.signup(joinUserRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("success");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> loginUserDetails(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDetailsResponse userDetailsResponse = userService.getUserDetails(userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDetailsResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<String> update(@Valid @RequestBody UpdateUserRequest updateUserRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.update(updateUserRequest, userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body("success");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword(), null);

        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
            String jwt = jwtUtil.createJwt(userDetails.getUsername());
            jwtTokenRepository.save(JwtToken.builder()
                .token(jwt)
                .build());
            return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"token\":\"" + jwt + "\"}");
        } catch (AuthenticationException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("잘못된 이메일 또는 비밀번호입니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        jwtTokenRepository.deleteByToken(token);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("success");
    }
}
