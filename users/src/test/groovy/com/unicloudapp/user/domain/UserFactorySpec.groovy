package com.unicloudapp.user.domain

import com.unicloudapp.common.domain.Email
import com.unicloudapp.common.domain.user.*
import spock.lang.Specification

import java.time.LocalDateTime

class UserFactorySpec extends Specification {

    UserFactory factory = new UserFactory()

    def "create should build user without email and lastLogin"() {
        given:
        UUID id = UUID.randomUUID()
        String indexNumber = "12345"
        String firstName = "Jan"
        String lastName = "Kowalski"
        UserRole.Type roleType = UserRole.Type.STUDENT

        when:
        User user = factory.create(id, indexNumber, firstName, lastName, roleType)

        then:
        user.userId == UserId.of(id)
        user.userLogin == UserLogin.of(indexNumber)
        user.firstName == FirstName.of(firstName)
        user.lastName == LastName.of(lastName)
        user.email == Email.empty()
        user.lastLoginAt == LastLoginAt.empty()
        user.userRole == UserRole.of(roleType)
    }

    def "restore should build user with email and lastLogin"() {
        given:
        UUID id = UUID.randomUUID()
        String indexNumber = "54321"
        String firstName = "Anna"
        String lastName = "Nowak"
        UserRole.Type roleType = UserRole.Type.ADMIN
        String email = "anna.nowak@example.com"
        LocalDateTime lastLogin = LocalDateTime.of(2024, 5, 20, 10, 0)

        when:
        User user = factory.restore(id, indexNumber, firstName, lastName, roleType, email, lastLogin)

        then:
        user.userId == UserId.of(id)
        user.userLogin == UserLogin.of(indexNumber)
        user.firstName == FirstName.of(firstName)
        user.lastName == LastName.of(lastName)
        user.email == Email.of(email)
        user.lastLoginAt == LastLoginAt.of(lastLogin)
        user.userRole == UserRole.of(roleType)
    }
}
