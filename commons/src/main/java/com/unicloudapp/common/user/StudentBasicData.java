package com.unicloudapp.common.user;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentBasicData {

    private String firstName;
    private String lastName;
    private String login;
    private String email;
}
