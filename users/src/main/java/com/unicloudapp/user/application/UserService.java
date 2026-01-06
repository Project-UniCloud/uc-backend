package com.unicloudapp.user.application;

import com.unicloudapp.common.auth.AdminProperties;
import com.unicloudapp.common.exception.user.UserAlreadyExistsException;
import com.unicloudapp.common.exception.user.UserNotFoundException;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserCreateCommand;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserFullName;
import com.unicloudapp.common.user.UserFullNameAndLoginProjection;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.FirstName;
import com.unicloudapp.common.vo.user.LastName;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.vo.user.UserRole;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.command.UpdateUserCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindAllLecturersUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.application.port.in.SearchLecturerUserCase;
import com.unicloudapp.user.application.port.in.UpdateUserUseCase;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

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
                FindAllLecturersUseCase,
                UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final UserFactory userFactory;
    private final AdminProperties adminProperties;

    @Override
    public User createLecturer(@Valid CreateLecturerCommand command) {
        if (userRepository.existsByLogin(command.login())) {
            throw new UserAlreadyExistsException(command.login());
        }
        Set<UserRole.Type> roleTypes = new HashSet<>(List.of(UserRole.Type.LECTURER));
        if (adminProperties.getAdmins().contains(UserLogin.of(command.login()))) {
            roleTypes.add(UserRole.Type.ADMIN);
        }
        UserRole role = UserRole.of(roleTypes);
        User user = userFactory.create(
                UserId.of(UUID.randomUUID()),
                UserLogin.of(command.login()),
                FirstName.of(command.firstName()),
                LastName.of(command.lastName()),
                Email.of(command.email()),
                role);
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
                UserRole.of(UserRole.Type.STUDENT));
        return userRepository.save(user);
    }

    @Override
    public boolean isUserStudent(UserId userId) {
        return userRepository
                .findById(userId)
                .map(user -> user.getUserRole().hasRole(UserRole.Type.STUDENT))
                .orElse(false);
    }

    @Override
    public User findUserById(UserId userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public Map<UserId, UserFullName> getFullNameForUserIds(List<UserId> userIds) {
        return userRepository.findFullNamesByIds(userIds).stream()
                .collect(Collectors.toMap(
                        projection -> UserId.of(projection.getUuid()),
                        projection -> UserFullName.of(
                                UserId.of(projection.getUuid()),
                                FirstName.of(projection.getFirstName()),
                                LastName.of(projection.getLastName()))));
    }

    @Override
    public Page<@NotNull UserDetails> getUserDetailsByIds(Set<UserId> userIds, int pageNumber, int pageSize) {
        return userRepository
                .findUserRowByIds(userIds, pageNumber, pageSize)
                .map(userRowProjection -> UserDetails.builder()
                        .userId(UserId.of(userRowProjection.getUuid()))
                        .login(UserLogin.of(userRowProjection.getLogin()))
                        .firstName(FirstName.of(userRowProjection.getFirstName()))
                        .lastName(LastName.of(userRowProjection.getLastName()))
                        .email(Email.of(userRowProjection.getEmail()))
                        .roles(UserRole.of(userRowProjection.getRoles()))
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
        return userRepository.findByLogin(userLogin).map(user -> UserDetails.builder()
                .userId(user.getUserId())
                .login(user.getUserLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getUserRole())
                .build());
    }

    @Override
    public List<UserDetails> getAdmins() {
        return userRepository.findAllByRole(UserRole.of(UserRole.Type.ADMIN)).stream()
                .map(user -> UserDetails.builder()
                        .userId(user.getUserId())
                        .login(user.getUserLogin())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getUserRole())
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
                .map(data -> userRepository
                        .findByLogin(UserLogin.of(data.getLogin()))
                        .orElseGet(() -> userFactory.create(
                                UserId.of(UUID.randomUUID()),
                                UserLogin.of(data.getLogin()),
                                FirstName.of(data.getFirstName()),
                                LastName.of(data.getLastName()),
                                Email.of(data.getEmail()),
                                UserRole.of(UserRole.Type.STUDENT))))
                .toList();
        userRepository.saveAll(students.stream()
                .filter(user -> !existsByLogin(user.getUserLogin().getValue()))
                .collect(Collectors.toList()));
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
                UserRole.of(UserRole.Type.STUDENT));
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
                userCreateCommand.userRole());
        userRepository.save(user);
    }

    @Override
    public Page<@NotNull UserRowProjection> findAllLecturers(
            int pageNumber, int pageSize, String lecturerFirstOrLastName) {
        return userRepository.findAllUsersByRoleAndFirstNameOrLastName(
                pageNumber, pageSize, UserRole.Type.LECTURER, lecturerFirstOrLastName);
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserCommand user) {
        User existingUser =
                userRepository.findById(user.userId()).orElseThrow(() -> new UserNotFoundException(user.userId()));
        existingUser.setEmail(user.email());
        existingUser.setLastName(user.lastName());
        existingUser.setFirstName(user.firstName());
        userRepository.save(existingUser);
    }
}
