package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.domain.User;
import jakarta.validation.Valid;

public interface CreateLecturerUseCase {

    User createLecturer(@Valid CreateLecturerCommand command);
}
