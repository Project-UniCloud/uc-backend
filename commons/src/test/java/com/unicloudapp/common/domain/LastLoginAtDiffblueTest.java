package com.unicloudapp.common.domain;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.unicloudapp.common.domain.user.LastLoginAt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class LastLoginAtDiffblueTest {

    /**
     * Test {@link LastLoginAt#of(LocalDateTime)}.
     * <ul>
     *   <li>Then return Value is {@link LocalDate} with {@code 1970} and one and one atStartOfDay.</li>
     * </ul>
     * <p>
     * Method under test: {@link LastLoginAt#of(LocalDateTime)}
     */
    @Test
    @DisplayName("Test of(LocalDateTime); then return Value is LocalDate with '1970' and one and one atStartOfDay")
    @Tag("MaintainedByDiffblue")
    void testOf_thenReturnValueIsLocalDateWith1970AndOneAndOneAtStartOfDay() {
        // Arrange
        LocalDateTime lastLogin = LocalDate.of(1970,
                        1,
                        1
                )
                .atStartOfDay();

        // Act and Assert
        assertSame(lastLogin,
                LastLoginAt.of(lastLogin)
                        .getValue()
        );
    }

    /**
     * Test {@link LastLoginAt#of(LocalDateTime)}.
     * <ul>
     *   <li>When {@code null}.</li>
     *   <li>Then return Value is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LastLoginAt#of(LocalDateTime)}
     */
    @Test
    @DisplayName("Test of(LocalDateTime); when 'null'; then return Value is 'null'")
    @Tag("MaintainedByDiffblue")
    void testOf_whenNull_thenReturnValueIsNull() {
        // Arrange, Act and Assert
        assertNull(LastLoginAt.of(null)
                .getValue());
    }

    /**
     * Test {@link LastLoginAt#empty()}.
     * <p>
     * Method under test: {@link LastLoginAt#empty()}
     */
    @Test
    @DisplayName("Test neverBeenLoggedIn()")
    @Tag("MaintainedByDiffblue")
    void testEmpty() {
        // Arrange, Act and Assert
        assertNull(LastLoginAt.empty()
                .getValue());
    }
}
