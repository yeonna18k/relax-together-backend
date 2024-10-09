package kr.codeit.relaxtogether.service;

import static kr.codeit.relaxtogether.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static kr.codeit.relaxtogether.exception.ErrorCode.PASSWORD_MISMATCH_ON_UPDATE;
import static kr.codeit.relaxtogether.exception.ErrorCode.USER_NOT_FOUND;

import kr.codeit.relaxtogether.dto.user.request.ChangePasswordRequest;
import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.dto.user.request.UpdateUserRequest;
import kr.codeit.relaxtogether.dto.user.response.UserDetailsResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public boolean checkEmail(EmailCheckRequest emailCheckRequest) {
        if (userRepository.existsByEmail(emailCheckRequest.getEmail())) {
            throw new ApiException(EMAIL_ALREADY_EXISTS);
        }
        return true;
    }

    public UserDetailsResponse getUserDetails(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        return UserDetailsResponse.of(user);
    }

    @Transactional
    public void signup(JoinUserRequest joinUserRequest) {
        userRepository.save(joinUserRequest.toEntity(passwordEncoder));
    }

    @Transactional
    public void update(UpdateUserRequest updateUserRequest, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        user.update(updateUserRequest.getCompanyName(), updateUserRequest.getProfileImage());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest passwordRequest) {
        String newPassword = passwordRequest.getNewPassword();
        String passwordCheck = passwordRequest.getPasswordCheck();
        if (!newPassword.equals(passwordCheck)) {
            throw new ApiException(PASSWORD_MISMATCH_ON_UPDATE);
        }

        User user = userRepository.findByEmail(passwordRequest.getEmail())
            .orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        user.changePassword(passwordEncoder.encode(newPassword));
    }
}
