package com.unicloudapp.user.infrastructure.persistence

import com.unicloudapp.common.vo.user.UserLogin
import com.unicloudapp.common.vo.user.UserRole
import com.unicloudapp.common.user.UserFullNameAndLoginProjection
import com.unicloudapp.user.domain.User
import com.unicloudapp.common.vo.user.UserId
import com.unicloudapp.user.domain.UserFactory
import com.unicloudapp.user.application.projection.UserRowProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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

    def "findUserRowByIds should delegate with PageRequest.of(pageNumber,pageSize) and UUID set"() {
        given:
        def id1 = UserId.of(UUID.randomUUID())
        def id2 = UserId.of(UUID.randomUUID())
        def ids = [id1, id2] as Set
        def pageNumber = 2
        def pageSize = 5
        Page<UserRowProjection> expected = Mock(Page)

        1 * userRepositoryJpa.getUserEntitiesByUuidIn({ it == ids*.value.toSet() }, PageRequest.of(pageNumber, pageSize)) >> expected

        when:
        def result = adapter.findUserRowByIds(ids, pageNumber, pageSize)

        then:
        result.is(expected)
    }

    def "findAllUsersByRoleAndFirstNameOrLastName without filter calls findAllProjectedByRole"() {
        given:
        def pageNumber = 1
        def size = 20
        def role = UserRole.Type.LECTURER
        Page<UserRowProjection> expected = Mock(Page)

        1 * userRepositoryJpa.findAllProjectedByRole(role, PageRequest.of(pageNumber, size)) >> expected

        when:
        def result = adapter.findAllUsersByRoleAndFirstNameOrLastName(pageNumber, size, role, null)

        then:
        result.is(expected)

        when:
        // blank should also route to findAllProjectedByRole
        def result2 = adapter.findAllUsersByRoleAndFirstNameOrLastName(pageNumber, size, role, "   ")

        then:
        1 * userRepositoryJpa.findAllProjectedByRole(role, PageRequest.of(pageNumber, size)) >> expected
        result2.is(expected)
    }

    def "findAllUsersByRoleAndFirstNameOrLastName with filter calls like query"() {
        given:
        def pageNumber = 0
        def size = 10
        def role = UserRole.Type.LECTURER
        def query = "john"
        Page<UserRowProjection> expected = Mock(Page)

        1 * userRepositoryJpa.findAllByRoleAndFirstNameOrLastNameLike(role, query, PageRequest.of(pageNumber, size)) >> expected

        when:
        def result = adapter.findAllUsersByRoleAndFirstNameOrLastName(pageNumber, size, role, query)

        then:
        result.is(expected)
    }

    def "saveAll should map users to entities and return their IDs"() {
        given:
        def u1 = Mock(User)
        def u2 = Mock(User)
        def e1 = Mock(UserEntity)
        def e2 = Mock(UserEntity)
        def saved1 = Mock(UserEntity)
        def saved2 = Mock(UserEntity)
        def id1 = UUID.randomUUID()
        def id2 = UUID.randomUUID()

        1 * userMapper.userToEntity(u1) >> e1
        1 * userMapper.userToEntity(u2) >> e2
        1 * userRepositoryJpa.saveAll([e1, e2]) >> [saved1, saved2]
        1 * saved1.getUuid() >> id1
        1 * saved2.getUuid() >> id2

        when:
        def result = adapter.saveAll([u1, u2])

        then:
        result == [UserId.of(id1), UserId.of(id2)]
    }

    def "findAllLoginsByIds should map entities to UserLogin values"() {
        given:
        def id1 = UserId.of(UUID.randomUUID())
        def id2 = UserId.of(UUID.randomUUID())
        def e1 = Mock(UserEntity)
        def e2 = Mock(UserEntity)

        1 * userRepositoryJpa.findAllById([id1.value, id2.value] as Set) >> [e1, e2]
        1 * e1.getLogin() >> "login1"
        1 * e2.getLogin() >> "login2"

        when:
        def result = adapter.findAllLoginsByIds([id1, id2] as Set)

        then:
        result*.value == ["login1", "login2"]
    }

    def "findAllLoginsAndEmailsByIds should return entries of UserLogin and Email"() {
        given:
        def id1 = UserId.of(UUID.randomUUID())
        def id2 = UserId.of(UUID.randomUUID())
        def e1 = Mock(UserEntity)
        def e2 = Mock(UserEntity)

        1 * userRepositoryJpa.findAllById([id1.value, id2.value] as Set) >> [e1, e2]
        1 * e1.getLogin() >> "l1"
        1 * e1.getEmail() >> "e1@example.com"
        1 * e2.getLogin() >> "l2"
        1 * e2.getEmail() >> "e2@example.com"

        when:
        def result = adapter.findAllLoginsAndEmailsByIds([id1, id2] as Set)

        then:
        result.size() == 2
        result[0].key.value in ["l1", "l2"]
        result[1].key.value in ["l1", "l2"]
        result.collect { it.value.value }.toSet() == ["e1@example.com", "e2@example.com"].toSet()
    }

    def "findByLogin maps optional via mapper"() {
        given:
        def entity = Mock(UserEntity)
        def user = Mock(User)

        1 * userRepositoryJpa.findByLogin("john") >> Optional.of(entity)
        1 * userMapper.entityToUser(entity, userFactory) >> user

        when:
        def result = adapter.findByLogin(UserLogin.of("john"))

        then:
        result.isPresent()
        result.get().is(user)
    }

    def "findAllByRole should map entities to domain users"() {
        given:
        def role = UserRole.of(UserRole.Type.ADMIN)
        def e1 = Mock(UserEntity)
        def e2 = Mock(UserEntity)
        def u1 = Mock(User)
        def u2 = Mock(User)

        1 * userRepositoryJpa.findAllByRole(UserRole.Type.ADMIN) >> [e1, e2]
        1 * userMapper.entityToUser(e1, userFactory) >> u1
        1 * userMapper.entityToUser(e2, userFactory) >> u2

        when:
        def result = adapter.findAllByRole(role)

        then:
        result == [u1, u2]
    }

    def "findAllByRole should return empty list when no users found"() {
        given:
        def role = UserRole.of(UserRole.Type.STUDENT)

        1 * userRepositoryJpa.findAllByRole(UserRole.Type.STUDENT) >> []

        when:
        def result = adapter.findAllByRole(role)

        then:
        result.isEmpty()
        0 * userMapper.entityToUser(*_)
    }
}
