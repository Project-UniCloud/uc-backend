package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.user.domain.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class UserToUserFoundResponseTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should detect all violations when all required fields are missing (built with empty builder)")
    void testValidation_whenAllRequiredFieldsAreNullOrBlank() {
        // Arrange
        UserFoundResponse user = UserFoundResponse.builder().build();

        // Act
        Set<ConstraintViolation<UserFoundResponse>> violations = validator.validate(user);

        // Assert
        assertThat(violations)
                .hasSize(5)
                .extracting(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessageTemplate
                )
                .containsExactlyInAnyOrder(
                        tuple("userId", "{jakarta.validation.constraints.NotNull.message}"),
                        tuple("login", "{jakarta.validation.constraints.NotBlank.message}"),
                        tuple("firstName", "{jakarta.validation.constraints.NotBlank.message}"),
                        tuple("lastName", "{jakarta.validation.constraints.NotBlank.message}"),
                        tuple("userRole", "{jakarta.validation.constraints.NotNull.message}")
                );
    }

    @ParameterizedTest(name = "[{index}] {0} - expected violation on '{2}'")
    @MethodSource("singleFieldViolationProvider")
    @DisplayName("Should detect single violation for invalid field")
    void testValidation_forSingleInvalidField(String testCaseName, UserFoundResponse userDtoToValidate, String expectedInvalidPropertyName, String expectedMessageTemplate) {
        // Act
        Set<ConstraintViolation<UserFoundResponse>> violations = validator.validate(userDtoToValidate);

        // Assert
        assertThat(violations)
                .hasSize(1)
                .extracting(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessageTemplate
                )
                .containsExactly(
                        tuple(expectedInvalidPropertyName, expectedMessageTemplate)
                );
    }

    private static Stream<Arguments> singleFieldViolationProvider() {
        UUID defaultUserId = UUID.randomUUID();
        String defaultLogin = "validLogin";
        String defaultFirstName = "ValidFirstName";
        String defaultLastName = "ValidLastName";
        UserRole.Type defaultUserRole = UserRole.Type.ADMIN;

        return Stream.of(
                Arguments.of(
                        "Login is missing (null)",
                        UserFoundResponse.builder().userId(defaultUserId).firstName(defaultFirstName).lastName(defaultLastName).userRole(defaultUserRole)/*.login(null) jest domyślne*/.build(),
                        "login",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),
                Arguments.of(
                        "Login is empty string",
                        UserFoundResponse.builder().userId(defaultUserId).login("").firstName(defaultFirstName).lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "login",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),
                Arguments.of(
                        "Login is whitespace only",
                        UserFoundResponse.builder().userId(defaultUserId).login("   ").firstName(defaultFirstName).lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "login",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),

                // Przypadki dla pola 'firstName'
                Arguments.of(
                        "FirstName is missing (null)",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "firstName",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),
                Arguments.of(
                        "FirstName is empty string",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).firstName("").lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "firstName",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),
                Arguments.of(
                        "FirstName is whitespace only",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).firstName("   ").lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "firstName",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),

                // Przypadki dla pola 'lastName' (dodaj, jeśli potrzebne)
                Arguments.of(
                        "LastName is missing (null)",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).firstName(defaultFirstName).userRole(defaultUserRole).build(),
                        "lastName",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),
                Arguments.of(
                        "LastName is empty string",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).firstName(defaultFirstName).lastName("").userRole(defaultUserRole).build(),
                        "lastName",
                        "{jakarta.validation.constraints.NotBlank.message}"
                ),


                // Przypadki dla pola 'userId'
                Arguments.of(
                        "UserId is missing (null)",
                        UserFoundResponse.builder().login(defaultLogin).firstName(defaultFirstName).lastName(defaultLastName).userRole(defaultUserRole).build(),
                        "userId",
                        "{jakarta.validation.constraints.NotNull.message}"
                ),

                // Przypadki dla pola 'userRole'
                Arguments.of(
                        "UserRole is missing (null)",
                        UserFoundResponse.builder().userId(defaultUserId).login(defaultLogin).firstName(defaultFirstName).lastName(defaultLastName).build(),
                        "userRole",
                        "{jakarta.validation.constraints.NotNull.message}"
                )
        );
    }

    @Test
    @DisplayName("Should have no violations when all required fields are present and valid")
    void testValidation_whenAllRequiredFieldsAreValid() {
        // Arrange
        UserFoundResponse user = UserFoundResponse.builder()
                .userId(UUID.randomUUID())
                .login("validLogin")
                .firstName("ValidFirstName")
                .lastName("ValidLastName")
                .userRole(UserRole.Type.ADMIN)
                .email("test@example.com")
                .build();

        // Act
        Set<ConstraintViolation<UserFoundResponse>> violations = validator.validate(user);

        // Assert
        assertThat(violations).isEmpty();
    }
}
