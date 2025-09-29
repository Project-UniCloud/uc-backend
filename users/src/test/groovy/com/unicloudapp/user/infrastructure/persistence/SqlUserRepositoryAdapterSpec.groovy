package com.unicloudapp.user.infrastructure.persistence

import com.unicloudapp.common.domain.user.UserRole
import com.unicloudapp.common.user.UserFullNameAndLoginProjection
import com.unicloudapp.user.domain.User
import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.user.domain.UserFactory
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class SqlUserRepositoryAdapterSpec extends Specification {

    UserRepositoryJpa userRepositoryJpa = Mock()
    UserMapper userMapper = Mock()
    UserFactory userFactory = Mock()
    SqlUserRepositoryAdapter adapter = new SqlUserRepositoryAdapter(userRepositoryJpa, userMapper, userFactory)

    def "should save user and return mapped entity"() {
        given: "a user to save"
        def user = Mock(User)
        def userEntity = Mock(UserEntity)
        def savedEntity = Mock(UserEntity)

        and: "mocks setup"
        1 * userMapper.userToEntity(user) >> userEntity
        1 * userRepositoryJpa.save(userEntity) >> savedEntity
        1 * userMapper.entityToUser(savedEntity, userFactory) >> user

        when: "saving the user"
        def result = adapter.save(user)

        then: "the result should be the mapped user"
        result == user

        and: "all interactions happened exactly once"
    }


    def "should find user by id"() {
        given:
        UUID uuid = UUID.randomUUID()
        UserId userId = UserId.of(uuid)
        UserEntity entity = Mock()
        User user = Mock()

        1 * userRepositoryJpa.findById(uuid) >> Optional.of(entity)
        1 * userMapper.entityToUser(entity, userFactory) >> user

        when:
        Optional<User> result = adapter.findById(userId)

        then:
        result.isPresent()
        result.get() == user
    }

    def "should return empty when user not found by id"() {
        given:
        UUID uuid = UUID.randomUUID()
        UserId userId = UserId.of(uuid)

        1 * userRepositoryJpa.findById(uuid) >> Optional.empty()

        when:
        Optional<User> result = adapter.findById(userId)

        then:
        !result.isPresent()
        0 * userMapper.entityToUser(_)
    }

    def "should check if user exists by id"() {
        given:
        UUID uuid = UUID.randomUUID()
        UserId userId = UserId.of(uuid)

        1 * userRepositoryJpa.existsById(uuid) >> true

        when:
        boolean exists = adapter.existsById(userId)

        then:
        exists
    }

    def "should check if user exists by login"() {
        given:
        String login = "userLogin"

        1 * userRepositoryJpa.existsByLogin(login) >> true

        when:
        boolean exists = adapter.existsByLogin(login)

        then:
        exists
    }

    def "should find full names by user ids"() {
        given:
        List<UserId> userIds = [UserId.of(UUID.randomUUID()), UserId.of(UUID.randomUUID())]
        List<UUID> uuids = userIds*.getValue()
        List<UserFullNameAndLoginProjection> projections = [Mock(UserFullNameAndLoginProjection), Mock(UserFullNameAndLoginProjection)]

        1 * userRepositoryJpa.findAllByUuidIn(uuids) >> projections

        when:
        def result = adapter.findFullNamesByIds(userIds)

        then:
        result == projections
    }

    def "should search lecturers by name query"() {
        given:
        String query = "abc"
        UserRole.Type role = UserRole.Type.LECTURER
        List<UserFullNameAndLoginProjection> projections = [Mock(UserFullNameAndLoginProjection), Mock(UserFullNameAndLoginProjection)]

        1 * userRepositoryJpa.searchUserByNameOrLogin(query, role, PageRequest.of(0, 10)) >> projections

        when:
        def result = adapter.searchUserByNameOrLogin(query, role)

        then:
        result == projections
    }
}
