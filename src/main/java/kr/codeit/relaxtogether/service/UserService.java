package kr.codeit.relaxtogether.service;

import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean checkEmail(EmailCheckRequest emailCheckRequest) {
        return userRepository.existsByEmail(emailCheckRequest.getEmail());
    }
}
