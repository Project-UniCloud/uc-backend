package com.unicloudapp.user.domain

import com.unicloudapp.common.domain.Email
import com.unicloudapp.common.domain.user.*
import spock.lang.Specification

import java.time.LocalDateTime

class UserFactorySpec extends Specification {

    UserFactory factory = new UserFactory()

    def "create should build user without email and lastLogin"() {
        given:
        UserId userId = UserId.of(UUID.randomUUID())
        UserLogin indexNumber = UserLogin.of("12345")
        FirstName firstName = FirstName.of("Jan")
        LastName lastName = LastName.of("Kowalski")
        Email email = Email.of("test@email.com")
        UserRole roleType = UserRole.of(UserRole.Type.STUDENT)

        when:
        User user = factory.create(userId, indexNumber, firstName, lastName, email, roleType)

        then:
        user.userId == userId
        user.userLogin == indexNumber
        user.firstName == firstName
        user.lastName == lastName
        user.email == Email.empty()
        user.lastLoginAt == LastLoginAt.empty()
        user.userRole == roleType
    }

    def "restore should build user with email and lastLogin"() {
        given:
        UserId userId = UserId.of(UUID.randomUUID())
        UserLogin indexNumber = UserLogin.of("54321")
        FirstName firstName = FirstName.of("Anna")
        LastName lastName = LastName.of("Nowak")
        UserRole roleType = UserRole.of(UserRole.Type.ADMIN)
        Email email = Email.of("anna.nowak@example.com")
        LastLoginAt lastLogin = LastLoginAt.of(
                LocalDateTime.of(2024, 5, 20, 10, 0)
        )

        when:
        User user = factory.restore(userId, indexNumber, firstName, lastName, roleType, email, lastLogin)

        then:
        user.userId == userId
        user.userLogin == indexNumber
        user.firstName == firstName
        user.lastName == lastName
        user.email == email
        user.lastLoginAt == lastLogin
        user.userRole == roleType
    }
}
