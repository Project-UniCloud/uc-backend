package com.unicloudapp.user.application;

import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.user.domain.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepositoryPort userRepository;
    private final UserFactory userFactory;

    public User createLecturer(@Valid UserDTO userDTO) {
        return createUser(userDTO,
                UserRole.Type.LECTURER
        );
    }

    public User createStudent(@Valid UserDTO userDTO) {
        return createUser(userDTO,
                UserRole.Type.STUDENT
        );
    }

    @Transactional
    public void updateFirstName(UserId id,
                                FirstName newFirstName
    ) {
        userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(""))
                .updateFirstName(newFirstName);
    }

    private User createUser(@Valid UserDTO userDTO,
                            UserRole.Type student
    ) {
        User user = userFactory.create(
                UUID.randomUUID(),
                userDTO.login(),
                userDTO.firstName(),
                userDTO.lastName(),
                student
        );
        return userRepository.save(user);
    }


    public User findUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow();
    }
}
