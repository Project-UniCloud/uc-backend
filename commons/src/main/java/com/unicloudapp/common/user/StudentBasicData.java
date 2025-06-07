package com.unicloudapp.common.user;

import com.unicloudapp.common.validation.FirstName;
import com.unicloudapp.common.validation.LastName;
import com.unicloudapp.common.validation.UserEmail;
import com.unicloudapp.common.validation.UserLogin;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentBasicData {

    @FirstName private String firstName;
    @LastName private String lastName;
    @UserLogin private String login;
    @UserEmail
    private String email;
}
