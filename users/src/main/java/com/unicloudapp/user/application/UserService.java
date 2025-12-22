package com.unicloudapp.user.application;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.*;
import com.unicloudapp.common.exception.user.UserAlreadyExistsException;
import com.unicloudapp.common.exception.user.UserNotFoundException;
import com.unicloudapp.common.user.*;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.*;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.common.user.UserFullNameAndLoginProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.*;
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
        UserCommandService,
        FindAllLecturersUseCase {

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
        if (userRepository.existsByLogin(command.login())) {
            throw new UserAlreadyExistsException(command.login());
        }
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
                .map(user -> user.getUserRole().getValue() == UserRole.Type.STUDENT)
                .orElse(false);
    }

    @Override
    public User findUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public Map<UserId, UserFullName> getFullNameForUserIds(List<UserId> userIds) {
        return userRepository.findFullNamesByIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        projection -> UserId.of(projection.getUuid()),
                        projection -> UserFullName.of(
                                UserId.of(projection.getUuid()),
                                FirstName.of(projection.getFirstName()),
                                LastName.of(projection.getLastName())
                        )));
    }

    @Override
    public Page<UserDetails> getUserDetailsByIds(Set<UserId> userIds, int pageNumber, int pageSize) {
        return userRepository.findUserRowByIds(userIds, pageNumber, pageSize)
                .map(userRowProjection -> UserDetails.builder()
                        .userId(UserId.of(userRowProjection.uuid()))
                        .login(UserLogin.of(userRowProjection.login()))
                        .firstName(FirstName.of(userRowProjection.firstName()))
                        .lastName(LastName.of(userRowProjection.lastName()))
                        .email(Email.of(userRowProjection.email()))
                        .role(UserRole.of(userRowProjection.role()))
                        .build());
    }

    @Override
    public List<UserLogin> getUserLoginsByIds(Set<UserId> userIds) {
        return userRepository.findAllLoginsByIds(userIds);
    }

    @Override
    public List<Map.Entry<UserLogin, Email>> getUserLoginsAndEmailsByIds(Set<UserId> userIds) {
        return userRepository.findAllLoginsAndEmailsByIds(userIds);
    }

    @Override
    public Optional<UserDetails> getUserDetailsByUsername(UserLogin userLogin) {
        return userRepository.findByLogin(userLogin)
                .map(user -> UserDetails.builder()
                        .userId(user.getUserId())
                        .login(user.getUserLogin())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getUserRole())
                        .build());
    }

    @Override
    public List<UserDetails> getAdmins() {
        return userRepository.findAllByRole(UserRole.of(UserRole.Type.ADMIN))
                .stream()
                .map(user -> UserDetails.builder()
                        .userId(user.getUserId())
                        .login(user.getUserLogin())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getUserRole())
                        .build())
                .toList();
    }

    @Override
    public List<UserFullNameAndLoginProjection> searchLecturers(String containsQuery) {
        return userRepository.searchUserByNameOrLogin(containsQuery, UserRole.Type.LECTURER);
    }

    @Override
    public List<UserId> importStudents(List<StudentBasicData> studentBasicData) {
        List<User> students = studentBasicData.stream()
                .map(data -> userRepository.findByLogin(UserLogin.of(data.getLogin()))
                        .orElseGet(() -> userFactory.create(
                                UserId.of(UUID.randomUUID()),
                                UserLogin.of(data.getLogin()),
                                FirstName.of(data.getFirstName()),
                                LastName.of(data.getLastName()),
                                Email.of(data.getEmail()),
                                UserRole.of(UserRole.Type.STUDENT)
                        ))
                )
                .toList();
        userRepository.saveAll(
                students.stream()
                        .filter(user -> !existsByLogin(user.getUserLogin().getValue()))
                        .collect(Collectors.toList())
        );
        return students.stream().map(User::getUserId).collect(Collectors.toList());
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

    @Override
    public void createUser(UserCreateCommand userCreateCommand) {
        User user = userFactory.create(
                UserId.of(UUID.randomUUID()),
                userCreateCommand.userLogin(),
                userCreateCommand.firstName(),
                userCreateCommand.lastName(),
                userCreateCommand.email(),
                userCreateCommand.userRole()
        );
        userRepository.save(user);
    }

    @Override
    public Page<UserRowProjection> findAllLecturers(
            int pageNumber,
            int pageSize,
            String lecturerFirstOrLastName
    ) {
        return userRepository.findAllUsersByRoleAndFirstNameOrLastName(
                pageNumber,
                pageSize,
                UserRole.Type.LECTURER,
                lecturerFirstOrLastName
        );
    }
}
