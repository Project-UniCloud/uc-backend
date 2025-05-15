package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.UserDTO;
import com.unicloudapp.user.application.UserDomainDtoMapper;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserRestController {

    private final CreateLecturerUseCase createLecturerUseCase;
    private final CreateStudentUseCase createStudentUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UserDomainDtoMapper userDomainDtoMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/lecturers")
    @ResponseStatus(HttpStatus.CREATED)
    CreatedLecturerResponse createStudent(
            @Valid
            @RequestBody
            CreateLecturerRequest createLecturerRequest
    ) {
        CreateLecturerCommand createLecturerCommand = CreateLecturerCommand.builder()
                .login(createLecturerRequest.userIndexNumber())
                .firstName(createLecturerRequest.firstName())
                .lastName(createLecturerRequest.lastName())
                .build();
        User createdLecturer = createLecturerUseCase.createLecturer(createLecturerCommand);
        return CreatedLecturerResponse.builder()
                .lecturerId(createdLecturer.getUserId().getValue())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    CreatedStudentResponse createStudent(
            @Valid
            @RequestBody
            CreateStudentRequest request
    ) {
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder()
                .login(request.userIndexNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
        User createdLecturer = createStudentUseCase.createStudent(createStudentCommand);
        return CreatedStudentResponse.builder()
                .studentId(createdLecturer.getUserId().getValue())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserDTO getUserById(@PathVariable("userId") UUID userId) {
        User user = findUserUseCase.findUserById(UserId.of(userId));
        return userDomainDtoMapper.toDto(user);
    }
}

