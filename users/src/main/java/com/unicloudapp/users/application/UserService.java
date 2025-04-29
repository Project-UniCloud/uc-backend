package com.unicloudapp.users.application;

import com.unicloudapp.commons.domain.FirstName;
import com.unicloudapp.users.application.ports.out.UserRepositoryPort;
import com.unicloudapp.users.domain.User;
import com.unicloudapp.users.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepositoryPort userRepository;

    public User createUser(UserDTO userDTO) {
        User user = User.createUser(userDTO.getUserId(),
                userDTO.getUserIndexNumber(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getUserRole()
        );
        return userRepository.save(user);
    }

    @Transactional
    public void updateFirstName(UserId id,
                                FirstName newFirstName
    ) {
        userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(""))
                .updateFirstName(newFirstName);
    }

    public User findUserById(UserId userId) {
        return userRepository.findById(userId);
    }
}
