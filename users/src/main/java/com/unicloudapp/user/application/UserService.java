package com.unicloudapp.user.application;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.*;
import com.unicloudapp.common.exception.user.UserAlreadyExistsException;
import com.unicloudapp.common.exception.user.UserNotFoundException;
import com.unicloudapp.common.user.*;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.application.port.in.SearchLecturerUserCase;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.application.projection.UserFullNameProjection;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Validated
class UserService 
implements UserValidationService, 
        CreateStudentUseCase, 
        CreateLecturerUseCase,
        FindUserUseCase,
        UserQueryService,
        SearchLecturerUserCase,
        UserCommandService {

    private final UserRepositoryPort userRepository;
    private final UserFactory userFactory;

    @Override
    public User createLecturer(@Valid CreateLecturerCommand command) {
        if (userRepository.existsByLogin(command.login())) {
            throw new UserAlreadyExistsException(command.login());
        }
        User user = userFactory.create(
                UserId.of(UUID.randomUUID()),
                UserLogin.of(command.login()),
                FirstName.of(command.firstName()),
                LastName.of(command.lastName()),
                Email.of(command.email()),
                UserRole.of(UserRole.Type.LECTURER)
        );
        return userRepository.save(user);
    }

    @Override
    public User createStudent(@Valid CreateStudentCommand command) {
        User user = userFactory.create(
                UserId.of(UUID.randomUUID()),
                UserLogin.of(command.login()),
                FirstName.of(command.firstName()),
                LastName.of(command.lastName()),
                Email.of(command.email()),
                UserRole.of(UserRole.Type.STUDENT)
        );
        return userRepository.save(user);
    }

    @Override
    public boolean isUserStudent(UserId userId) {
        return userRepository.findById(userId)
                .map(user -> user.getUserRole().getValue().equals(UserRole.Type.STUDENT))
                .orElse(false);
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

    @Override
    public Map<UserId, UserFullName> getFullNameForUserIds(List<UserId> userIds) {
        return userRepository.findFullNamesByIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        projection -> UserId.of(projection.getUuid()),
                        projection -> UserFullName.of(
                                FirstName.of(projection.getFirstName()),
                                LastName.of(projection.getLastName())
                        )));
    }

    @Override
    public List<UserDetails> getUserDetailsByIds(Set<UserId> userIds, int offset, int size) {
        return userRepository.findUserRowByIds(userIds, offset, size)
                .stream()
                .map(userRowProjection -> UserDetails.builder()
                        .userId(UserId.of(userRowProjection.getUuid()))
                        .login(UserLogin.of(userRowProjection.getLogin()))
                        .firstName(FirstName.of(userRowProjection.getFirstName()))
                        .lastName(LastName.of(userRowProjection.getLastName()))
                        .email(Email.of(userRowProjection.getEmail()))
                        .build())
                .toList();
    }

    @Override
    public long countUsersByIds(Set<UserId> userIds) {
        return userRepository.countByUuidIn(userIds.stream().map(UserId::getValue).collect(Collectors.toSet()));
    }

    @Override
    public List<UserLogin> getUserLoginsByIds(Set<UserId> userIds) {
        return userRepository.findAllLoginsByIds(userIds);
    }

    @Override
    public List<UserFullNameProjection> searchLecturers(String containsQuery) {
        return userRepository.searchUserByName(containsQuery, UserRole.Type.LECTURER);
    }

    @Override
    public List<UserId> importStudents(List<StudentBasicData> studentBasicData) {
        List<User> students = studentBasicData.stream()
                .map(data -> userFactory.create(
                        UserId.of(UUID.randomUUID()),
                        UserLogin.of(data.getLogin()),
                        FirstName.of(data.getFirstName()),
                        LastName.of(data.getLastName()),
                        Email.of(data.getEmail()),
                        UserRole.of(UserRole.Type.STUDENT)
                ))
                .toList();
        return userRepository.saveAll(students);
    }

    @Override
    public UserId createStudent(StudentBasicData studentBasicData) {
        User user = userFactory.create(
                UserId.of(UUID.randomUUID()),
                UserLogin.of(studentBasicData.getLogin()),
                FirstName.of(studentBasicData.getFirstName()),
                LastName.of(studentBasicData.getLastName()),
                Email.of(studentBasicData.getEmail()),
                UserRole.of(UserRole.Type.STUDENT)
        );
        return userRepository.save(user).getUserId();
    }
}
