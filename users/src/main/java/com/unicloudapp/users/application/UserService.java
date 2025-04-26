package com.unicloudapp.users.application;

import com.unicloudapp.users.application.ports.out.UserRepositoryPort;
import com.unicloudapp.users.domain.User;
import com.unicloudapp.users.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepositoryPort userRepository;

    public User createUser(UserDTO userDTO) {
        User user = User.createUser(userDTO.getUserId(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getUserRole()
        );
        return userRepository.save(user);
    }

    public User findUserById(UserId userId) {
        return userRepository.findById(userId);
    }
}
