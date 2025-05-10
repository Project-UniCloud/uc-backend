package com.unicloudapp.user.application;

import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort userRepository;
    private final UserFactory userFactory;

    public User createLecturer(UserDTO userDTO) {
        return createUser(userDTO,
                UserRole.Type.LECTURER
        );
    }

    public User createStudent(UserDTO userDTO) {
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

    private User createUser(UserDTO userDTO,
                            UserRole.Type student
    ) {
        User user = userFactory.create(
                UUID.randomUUID(),
                userDTO.getLogin(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                student
        );
        return userRepository.save(user);
    }


    public User findUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow();
    }
}
