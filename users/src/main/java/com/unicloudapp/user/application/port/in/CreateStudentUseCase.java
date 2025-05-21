package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.domain.User;
import jakarta.validation.Valid;

public interface CreateStudentUseCase {

    User createStudent(@Valid CreateStudentCommand command);
}
