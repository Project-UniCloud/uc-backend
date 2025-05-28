package com.unicloudapp.user.infrastructure.rest

import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.user.application.projection.UserFullNameProjection
import com.unicloudapp.user.application.command.CreateLecturerCommand
import com.unicloudapp.user.application.command.CreateStudentCommand
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase
import com.unicloudapp.user.application.port.in.CreateStudentUseCase
import com.unicloudapp.user.application.port.in.FindUserUseCase
import com.unicloudapp.user.application.port.in.SearchLecturerUserCase
import com.unicloudapp.user.domain.User
import spock.lang.Specification

class UserRestControllerSpec extends Specification {

    CreateLecturerUseCase createLecturerUseCase = Mock()
    CreateStudentUseCase createStudentUseCase = Mock()
    FindUserUseCase findUserUseCase = Mock()
    SearchLecturerUserCase searchLecturerUserCase = Mock()
    UserToUserFoundResponseMapper userDomainDtoMapper = Mock()

    UserRestController controller = new UserRestController(
            createLecturerUseCase,
            createStudentUseCase,
            findUserUseCase,
            searchLecturerUserCase,
            userDomainDtoMapper
    )

    def "should create lecturer and return created response"() {
        given:
        def request = Mock(CreateLecturerRequest) {
            userIndexNumber() >> "lecturer123"
            firstName() >> "Jan"
            lastName() >> "Kowalski"
        }
        def userId = UUID.randomUUID()
        def user = Mock(User) {
            getUserId() >> UserId.of(userId)
        }

        when:
        def response = controller.createStudent(request)

        then:
        1 * createLecturerUseCase.createLecturer(_ as CreateLecturerCommand) >> user
        response.lecturerId == userId
    }

    def "should create student and return created response"() {
        given:
        def request = Mock(CreateStudentRequest) {
            userIndexNumber() >> "student123"
            firstName() >> "Anna"
            lastName() >> "Nowak"
        }
        def userId = UUID.randomUUID()
        def user = Mock(User) {
            getUserId() >> UserId.of(userId)
        }

        when:
        def response = controller.createStudent(request)

        then:
        1 * createStudentUseCase.createStudent(_ as CreateStudentCommand) >> user
        response.studentId == userId
    }

    def "should get user by id and map to response"() {
        given:
        def userId = UUID.randomUUID()
        def user = Mock(User)
        def responseDto = Mock(UserFoundResponse)

        when:
        def response = controller.getUserById(userId)

        then:
        1 * findUserUseCase.findUserById(UserId.of(userId)) >> user
        1 * userDomainDtoMapper.toUserFoundResponse(user) >> responseDto
        response == responseDto
    }

    def "should search lecturers and return list of responses"() {
        given:
        def query = "Ja"
        def user1 = Stub(UserFullNameProjection) {
            getFirstName() >> "Jan"
            getLastName() >> "Kowalski"
        }
        def user2 = Stub(UserFullNameProjection) {
            getFirstName() >> "Janina"
            getLastName() >> "Nowak"
        }
        searchLecturerUserCase.searchLecturers(query) >> [user1, user2]

        when:
        def results = controller.getLecturersByIds(query)

        then:
        results.size() == 2
        results[0].firstName() == "Jan"
        results[0].lastName() == "Kowalski"
        results[1].firstName() == "Janina"
        results[1].lastName() == "Nowak"
    }
}
