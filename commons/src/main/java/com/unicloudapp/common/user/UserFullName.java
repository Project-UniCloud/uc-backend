package com.unicloudapp.common.user;

import com.unicloudapp.common.vo.user.FirstName;
import com.unicloudapp.common.vo.user.LastName;
import com.unicloudapp.common.vo.user.UserId;

public record UserFullName(UserId userId, FirstName firstName, LastName lastName) {

    public static UserFullName of(UserId userId, FirstName firstName, LastName lastName) {
        return new UserFullName(userId, firstName, lastName);
    }

    public String getFullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }
}
