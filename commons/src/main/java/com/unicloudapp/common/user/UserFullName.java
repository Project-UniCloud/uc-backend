package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.LastName;

public record UserFullName(FirstName firstName, LastName lastName) {

    public static UserFullName of(FirstName firstName, LastName lastName) {
        return new UserFullName(firstName, lastName);
    }

    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }
}
