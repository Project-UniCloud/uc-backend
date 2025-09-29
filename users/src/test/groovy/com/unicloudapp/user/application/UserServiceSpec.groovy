package com.unicloudapp.user.application

import com.unicloudapp.common.domain.Email
import com.unicloudapp.common.domain.user.FirstName
import com.unicloudapp.common.domain.user.LastName
import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.common.domain.user.UserLogin
import com.unicloudapp.common.domain.user.UserRole
import com.unicloudapp.common.exception.user.UserAlreadyExistsException
import com.unicloudapp.common.exception.user.UserNotFoundException
import com.unicloudapp.common.user.StudentBasicData
import com.unicloudapp.user.application.command.CreateLecturerCommand
import com.unicloudapp.user.application.command.CreateStudentCommand
import com.unicloudapp.user.application.port.out.UserRepositoryPort
import com.unicloudapp.common.user.UserFullNameAndLoginProjection
import com.unicloudapp.user.application.projection.UserRowProjection
import com.unicloudapp.user.domain.User
import com.unicloudapp.user.domain.UserFactory
import org.springframework.data.domain.PageImpl
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
    def email = "test@email.com"

    @Subject
    UserService userService = new UserService(userRepository, userFactory)

    def "createLecturer should create Lecturer"() {
        given:
        def command = createLecturerCommand()
        def user = Mock(User.class)

        when:
        def result = userService.createLecturer(command)

        then:
        1 * userFactory.create(_, _, _, _, _, _) >> user
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
        1 * userFactory.create(_, _, _, _, _, _) >> user
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

        def projection1 = Mock(UserFullNameAndLoginProjection) {
            uuid() >> id1
            firstName() >> "John"
            lastName() >> "Doe"
        }

        def projection2 = Mock(UserFullNameAndLoginProjection) {
            uuid() >> id2
            firstName() >> "Alice"
            lastName() >> "Smith"
        }

        def userIds = [UserId.of(id1), UserId.of(id2)]

        when:
        def result = userService.getFullNameForUserIds(userIds)

        then:
        1 * userRepository.findFullNamesByIds(userIds) >> [projection1, projection2]
        result.size() == 2
        result[userIds[0]].firstName.value == "John"
        result[userIds[0]].lastName.value == "Doe"
        result[userIds[1]].firstName.value == "Alice"
        result[userIds[1]].lastName.value == "Smith"
    }

    def "should return list of lecturer projections matching query"() {
        given:
        def query = "Jo"
        def projection1 = Mock(UserFullNameAndLoginProjection)
        def projection2 = Mock(UserFullNameAndLoginProjection)

        when:
        def result = userService.searchLecturers(query)

        then:
        1 * userRepository.searchUserByNameOrLogin(query, UserRole.Type.LECTURER) >> [projection1, projection2]
        result == [projection1, projection2]
    }

    def "importStudents should create users and return their IDs"() {
        given:
        def student1 = new StudentBasicData( "John", "Doe", "login1","john.doe@example.com")
        def student2 = new StudentBasicData( "Jane", "Smith", "login2","jane.smith@example.com")
        List<StudentBasicData> students = [student1, student2]

        def user1 = Mock(User)
        def user2 = Mock(User)

        1 * userFactory.create(
                _ as UserId,
                UserLogin.of("login1"),
                FirstName.of("John"),
                LastName.of("Doe"),
                Email.of("john.doe@example.com"),
                UserRole.of(UserRole.Type.STUDENT)
        ) >> user1
        1 * userFactory.create(
                _ as UserId,
                UserLogin.of("login2"),
                FirstName.of("Jane"),
                LastName.of("Smith"),
                Email.of("jane.smith@example.com"),
                UserRole.of(UserRole.Type.STUDENT)
        ) >> user2

        def id1 = UserId.of(UUID.randomUUID())
        def id2 = UserId.of(UUID.randomUUID())
        user1.getUserId() >> id1
        user2.getUserId() >> id2

        1 * userRepository.saveAll(_) >> [id1, id2]

        when:
        def result = userService.importStudents(students)

        then:
        result.size() == 2
        result.containsAll([id1, id2])
    }

    def "getUserDetailsByIds should return UserDetails list"() {
        given:
        def userId1 = UserId.of(UUID.randomUUID())
        def userId2 = UserId.of(UUID.randomUUID())
        def userIds = [userId1, userId2] as Set

        def userRowProjection = Mock(UserRowProjection.class)
        userRowProjection.getUuid() >> userId1.getValue()
        userRowProjection.getLogin() >> "login1"
        userRowProjection.getFirstName() >> "John"
        userRowProjection.getLastName() >> "Doe"
        userRowProjection.getEmail() >> "john.doe@example.com"
        userRowProjection.getRole() >> "STUDENT"

        userRepository.findUserRowByIds(userIds, 0, 10) >> new PageImpl([userRowProjection])

        when:
        def userDetails = userService.getUserDetailsByIds(userIds, 0, 10)

        then:
        userDetails.size() == 1
        userDetails[0].userId == userId1
        userDetails[0].login == UserLogin.of("login1")
        userDetails[0].firstName == FirstName.of("John")
        userDetails[0].lastName == LastName.of("Doe")
        userDetails[0].email == Email.of("john.doe@example.com")
        userDetails[0].role == UserRole.of(UserRole.Type.STUDENT)
    }

    def "createStudent should create and save a student user and return their ID"() {
        given:
        def studentData = new StudentBasicData("Jan", "Kowalski", "studentLogin",  "jan.kowalski@example.com")
        def mockUser = Mock(User)
        def generatedId = UserId.of(UUID.randomUUID())
        1 * userFactory.create(
                _ as UserId,
                UserLogin.of("studentLogin"),
                FirstName.of("Jan"),
                LastName.of("Kowalski"),
                Email.of("jan.kowalski@example.com"),
                UserRole.of(UserRole.Type.STUDENT)
        ) >> mockUser
        1 * userRepository.save(_) >> {
            mockUser.getUserId() >> generatedId
            return mockUser
        }

        when:
        def result = userService.createStudent(studentData)

        then:
        result == generatedId
    }


    def createStudentCommand() {
        CreateStudentCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .login(login)
                .email(email)
                .build()
    }

    def createLecturerCommand() {
        CreateLecturerCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .login(login)
                .email(email)
                .build()
    }
}
