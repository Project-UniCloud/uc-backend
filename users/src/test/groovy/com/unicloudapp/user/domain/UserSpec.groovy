package com.unicloudapp.user.domain

import com.unicloudapp.common.domain.Email
import com.unicloudapp.common.domain.user.*
import spock.lang.Specification

import java.time.LocalDateTime

class UserSpec extends Specification {

    def "should build user with correct fields"() {
        given:
        def userId = UserId.of(UUID.randomUUID())
        def userLogin = UserLogin.of("123456")
        def firstName = FirstName.of("Jan")
        def lastName = LastName.of("Kowalski")
        def email = Email.of("jan.kowalski@example.com")
        def lastLogin = LastLoginAt.of(LocalDateTime.now().minusDays(1))
        def role = UserRole.of(UserRole.Type.STUDENT)

        when:
        def user = User.builder()
                .userId(userId)
                .userLogin(userLogin)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLoginAt(lastLogin)
                .userRole(role)
                .build()

        then:
        user.userId == userId
        user.userLogin == userLogin
        user.firstName == firstName
        user.lastName == lastName
        user.email == email
        user.lastLoginAt == lastLogin
        user.userRole == role
    }

    def "should update lastLoginAt if new login is after previous"() {
        given:
        def previousLogin = LastLoginAt.of(LocalDateTime.of(2024, 5, 1, 10, 0))
        def newLogin = LastLoginAt.of(LocalDateTime.of(2024, 5, 2, 10, 0))

        def user = User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .userLogin(UserLogin.of("654321"))
                .firstName(FirstName.of("Anna"))
                .lastName(LastName.of("Nowak"))
                .email(Email.of("anna@example.com"))
                .lastLoginAt(previousLogin)
                .userRole(UserRole.of(UserRole.Type.STUDENT))
                .build()

        when:
        user.logIn(newLogin)

        then:
        user.lastLoginAt == newLogin
    }

    def "should throw exception when logging in with older timestamp"() {
        given:
        def previousLogin = LastLoginAt.of(LocalDateTime.of(2024, 5, 3, 12, 0))
        def newLogin = LastLoginAt.of(LocalDateTime.of(2024, 5, 1, 10, 0))

        def user = User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .userLogin(UserLogin.of("777777"))
                .firstName(FirstName.of("Kasia"))
                .lastName(LastName.of("Lis"))
                .email(Email.of("kasia@example.com"))
                .lastLoginAt(previousLogin)
                .userRole(UserRole.of(UserRole.Type.ADMIN))
                .build()

        when:
        user.logIn(newLogin)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Last login time cannot be null" // <- Twój błąd ma trochę niepasującą treść, możesz poprawić
    }

    def "should allow login when user has never been logged in"() {
        given:
        def newLogin = LastLoginAt.of(LocalDateTime.of(2024, 5, 10, 15, 30))

        def user = User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .userLogin(UserLogin.of("888888"))
                .firstName(FirstName.of("Tomek"))
                .lastName(LastName.of("Baran"))
                .email(Email.of("tomek@example.com"))
                .lastLoginAt(LastLoginAt.empty())
                .userRole(UserRole.of(UserRole.Type.STUDENT))
                .build()

        when:
        user.logIn(newLogin)

        then:
        user.lastLoginAt == newLogin
    }
}
