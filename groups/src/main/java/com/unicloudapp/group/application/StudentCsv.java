package com.unicloudapp.group.application;

import com.opencsv.bean.CsvBindByName;
import com.unicloudapp.common.validation.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentCsv {

    @CsvBindByName(column = "nr_albumu")
    @Pattern(regexp = "\\d{6}")
    private String login;

    @CsvBindByName(column = "nazwisko")
    @LastName
    private String lastName;

    @CsvBindByName(column = "imie")
    @FirstName
    private String firstName;

    @CsvBindByName(column = "email")
    @UserEmail
    private String email;
}
