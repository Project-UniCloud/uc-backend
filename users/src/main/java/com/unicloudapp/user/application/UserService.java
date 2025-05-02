package com.unicloudapp.user.application;

import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepositoryPort userRepository;

    public User createLecturer(UserDTO userDTO) {
        User user = UserFactory.createUser(
                UUID.randomUUID(),
                userDTO.getUserIndexNumber(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                UserRole.Type.LECTURER
        );
        return userRepository.save(user);
    }

    // TODO refactor
    public User createStudent(UserDTO userDTO) {
        User user = UserFactory.createUser(
                UUID.randomUUID(),
                userDTO.getUserIndexNumber(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                UserRole.Type.STUDENT
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
        return userRepository.findById(userId)
                .orElseThrow();
    }
}
