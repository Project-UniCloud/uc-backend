package com.unicloudapp.user.application.ports.in;

import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.domain.User;

public interface CreateStudentUseCase {

    User createStudent(CreateStudentCommand command);
}
