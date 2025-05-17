package com.unicloudapp.user.application;

import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.exception.user.UserAlreadyExistsException;
import com.unicloudapp.common.exception.user.UserNotFoundException;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.common.domain.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@RequiredArgsConstructor
@Validated
class UserService 
implements UserValidationService, 
        CreateStudentUseCase, 
        CreateLecturerUseCase,
        FindUserUseCase {

    private final UserRepositoryPort userRepository;
    private final UserFactory userFactory;

    @Override
    public User createLecturer(@Valid CreateLecturerCommand command) {
        if (userRepository.existsByLogin(command.login())) {
            throw new UserAlreadyExistsException(command.login());
        }
        User user = userFactory.create(
                UUID.randomUUID(),
                command.login(),
                command.firstName(),
                command.lastName(),
                UserRole.Type.LECTURER
        );
        return userRepository.save(user);
    }

    @Override
    public User createStudent(@Valid CreateStudentCommand command) {
        User user = userFactory.create(
                UUID.randomUUID(),
                command.login(),
                command.firstName(),
                command.lastName(),
                UserRole.Type.STUDENT
        );
        return userRepository.save(user);
    }

    @Override
    public boolean isUserStudent(UserId userId) {
        return findUserById(userId).getUserRole()
                .getValue() == UserRole.Type.STUDENT;
    }

    @Override
    public User findUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean isUserExists(UserId userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void updateFirstName(UserId id,
                                FirstName newFirstName
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.updateFirstName(newFirstName);
        userRepository.save(user);
    }
}
