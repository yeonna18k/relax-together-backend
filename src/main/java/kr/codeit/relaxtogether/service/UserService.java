package kr.codeit.relaxtogether.service;

import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
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
        return userRepository.existsByEmail(emailCheckRequest.getEmail());
    }

    @Transactional
    public void signup(JoinUserRequest joinUserRequest) {
        userRepository.save(joinUserRequest.toEntity(passwordEncoder));
    }
}
