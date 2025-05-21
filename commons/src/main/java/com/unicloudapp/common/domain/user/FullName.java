package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FullName {

    FirstName firstName;
    LastName lastName;

    public static FullName of(FirstName firstName, LastName lastName) {
        return new FullName(firstName, lastName);
    }

    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }
}
