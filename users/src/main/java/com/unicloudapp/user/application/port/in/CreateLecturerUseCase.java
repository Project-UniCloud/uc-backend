package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.domain.User;

public interface CreateLecturerUseCase {

    User createLecturer(CreateLecturerCommand command);
}
