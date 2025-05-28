package com.unicloudapp.user.application


import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.common.domain.user.UserRole
import com.unicloudapp.common.exception.user.UserAlreadyExistsException
import com.unicloudapp.common.exception.user.UserNotFoundException
import com.unicloudapp.user.application.command.CreateLecturerCommand
import com.unicloudapp.user.application.command.CreateStudentCommand
import com.unicloudapp.user.application.port.out.UserRepositoryPort
import com.unicloudapp.user.application.projection.UserFullNameProjection
import com.unicloudapp.user.domain.User
import com.unicloudapp.user.domain.UserFactory
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("UserService unit tests")
class UserServiceSpec extends Specification {

    def userFactory = Mock(UserFactory.class)
    def userRepository = Mock(UserRepositoryPort.class)
    def userId = UUID.randomUUID()
    def login = "jane.doe"
    def firstName = "Jane"
    def lastName = "Doe"

    @Subject
    UserService userService = new UserService(userRepository, userFactory)

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
        1 * userRepository.save(user) >> user
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
        1 * userRepository.save(user) >> user
        result == user
    }

    def "findUserById should return user if found"() {
        given:
        def user = Mock(User)
        userRepository.findById(_ as UserId) >> Optional.of(user)

        when:
        def result = userService.findUserById(UserId.of(this.userId))

        then:
        result == user
    }

    def "findUserById should throw if not found"() {
        given:
        userRepository.findById(_ as UserId) >> Optional.empty()

        when:
        userService.findUserById(UserId.of(this.userId))

        then:
        thrown(UserNotFoundException)
    }

    def "isUserStudent should return true for student role"() {
        given:
        def user = Mock(User)
        user.getUserRole() >> UserRole.of(UserRole.Type.STUDENT)
        def userId = UserId.of(UUID.randomUUID())
        userRepository.findById(userId) >> Optional.of(user)

        when:
        def result = userService.isUserStudent(userId)

        then:
        result
    }

    def "isUserStudent should return false for role different than student"() {
        given:
        def lecturer = Mock(User)
        lecturer.getUserRole() >> UserRole.of(UserRole.Type.LECTURER)
        def lecturerId = UserId.of(UUID.randomUUID())
        userRepository.findById(lecturerId) >> Optional.of(lecturer)
        def admin = Mock(User)
        admin.getUserRole() >> UserRole.of(UserRole.Type.LECTURER)
        def adminId = UserId.of(UUID.randomUUID())
        userRepository.findById(adminId) >> Optional.of(admin)

        when:
        def resultLecturer = userService.isUserStudent(lecturerId)
        def resultAdmin = userService.isUserStudent(adminId)

        then:
        !resultLecturer
        !resultAdmin
    }

    def "isUserExists should return true if user exists in repository"() {
        given:
        def userId = UserId.of(UUID.randomUUID())
        userRepository.existsById(userId) >> true

        when:
        def result = userService.isUserExists(userId)

        then:
        result
    }

    def "isUserExists should return false if user does not exist in repository"() {
        given:
        def userId = UserId.of(UUID.randomUUID())
        userRepository.existsById(userId) >> false

        when:
        def result = userService.isUserExists(userId)

        then:
        !result
    }

    def "createLecturer should throw UserAlreadyExistsException if user with login already exists"() {
        given:
        def command = createLecturerCommand()
        userRepository.existsByLogin(command.login()) >> true

        when:
        userService.createLecturer(command)

        then:
        thrown(UserAlreadyExistsException.class)
    }

    def "should return map of full names by user ids"() {
        given:
        def id1 = UUID.randomUUID()
        def id2 = UUID.randomUUID()

        def projection1 = Mock(UserFullNameProjection) {
            getUuid() >> id1
            getFirstName() >> "John"
            getLastName() >> "Doe"
        }

        def projection2 = Mock(UserFullNameProjection) {
            getUuid() >> id2
            getFirstName() >> "Alice"
            getLastName() >> "Smith"
        }

        def userIds = [UserId.of(id1), UserId.of(id2)]

        when:
        def result = userService.getFullNameForUserIds(userIds)

        then:
        1 * userRepository.findFullNamesByIds(userIds) >> [projection1, projection2]
        result.size() == 2
        result[id1].firstName.value == "John"
        result[id2].lastName.value == "Smith"
    }

    def "should return list of lecturer projections matching query"() {
        given:
        def query = "Jo"
        def projection1 = Mock(UserFullNameProjection)
        def projection2 = Mock(UserFullNameProjection)

        when:
        def result = userService.searchLecturers(query)

        then:
        1 * userRepository.searchUserByName(query, UserRole.Type.LECTURER) >> [projection1, projection2]
        result == [projection1, projection2]
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
