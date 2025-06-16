package com.unicloudapp.common.user;

import com.unicloudapp.common.validation.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentBasicData {

    @FirstName private String firstName;
    @LastName private String lastName;
    @StudentLogin
    private String login;
    @UserEmail
    private String email;
}
