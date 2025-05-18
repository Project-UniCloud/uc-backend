package com.unicloudapp.user.application

import com.unicloudapp.common.domain.user.FirstName
import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.common.domain.user.UserRole
import com.unicloudapp.common.exception.user.UserNotFoundException
import com.unicloudapp.user.application.command.CreateLecturerCommand
import com.unicloudapp.user.application.command.CreateStudentCommand
import com.unicloudapp.user.application.port.out.UserRepositoryPort
import com.unicloudapp.user.domain.User
import com.unicloudapp.user.domain.UserFactory
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("UserService unit tests")
class UserServiceSpec extends Specification {

    def userFactory = Mock(UserFactory.class)
    def userRepositoryPort = Mock(UserRepositoryPort.class)
    def userId = UUID.randomUUID()
    def login = "jane.doe"
    def firstName = "Jane"
    def lastName = "Doe"

    @Subject
    UserService userService = new UserService(userRepositoryPort, userFactory)

    def "createLecturer should create Lecturer"() {
        given:
        def command = createLecturerCommand()
        def user = Mock(User.class)

        when:
        def result = userService.createLecturer(command)

        then:
        1 * userFactory.create(
                _,
                command.login(),
                command.firstName(),
                command.lastName(),
                UserRole.Type.LECTURER
        ) >> user
        1 * userRepositoryPort.save(user) >> user
        result == user
    }

    def "createStudent should create Student"() {
        given:
        def command = createStudentCommand()
        def user = Mock(User.class)

        when:
        def result = userService.createStudent(command)

        then:
        1 * userFactory.create(
                _,
                command.login(),
                command.firstName(),
                command.lastName(),
                UserRole.Type.STUDENT
        ) >> user
        1 * userRepositoryPort.save(user) >> user
        result == user
    }

    def "updateFirstName should call updateFirstName on user"() {
        given:
        def user = Mock(User)

        when:
        userService.updateFirstName(UserId.of(UUID.randomUUID()), FirstName.of(firstName))

        then:
        1 * user.updateFirstName(_)
        1 * userRepositoryPort.save(user)
        1 * userRepositoryPort.findById(_) >> Optional.of(user)
    }

    def "updateFirstName should throw if user not found"() {
        given:
        userRepositoryPort.findById(_) >> Optional.empty()

        when:
        userService.updateFirstName(UserId.of(userId), FirstName.of("42"))

        then:
        thrown(UserNotFoundException)
    }

    def "findUserById should return user if found"() {
        given:
        def user = Mock(User)
        userRepositoryPort.findById(_) >> Optional.of(user)

        when:
        def result = userService.findUserById(UserId.of(userId))

        then:
        result == user
    }

    def "findUserById should throw if not found"() {
        given:
        userRepositoryPort.findById(_) >> Optional.empty()

        when:
        userService.findUserById(UserId.of(userId))

        then:
        thrown(UserNotFoundException)
    }

    def createStudentCommand() {
        CreateStudentCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .login(login)
                .build()
    }

    def createLecturerCommand() {
        CreateLecturerCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .login(login)
                .build()
    }
}
