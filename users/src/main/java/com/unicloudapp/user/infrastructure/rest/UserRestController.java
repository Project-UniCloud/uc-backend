package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.application.port.in.SearchLecturerUserCase;
import com.unicloudapp.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserRestController {

    private final CreateLecturerUseCase createLecturerUseCase;
    private final CreateStudentUseCase createStudentUseCase;
    private final FindUserUseCase findUserUseCase;
    private final SearchLecturerUserCase searchLecturerUserCase;
    private final UserToUserFoundResponseMapper userDomainDtoMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/lecturers")
    @ResponseStatus(HttpStatus.CREATED)
    LecturerCreatedResponse createStudent(
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
        return LecturerCreatedResponse.builder()
                .lecturerId(createdLecturer.getUserId().getValue())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    StudentCreatedResponse createStudent(
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
        return StudentCreatedResponse.builder()
                .studentId(createdLecturer.getUserId().getValue())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserFoundResponse getUserById(@PathVariable("userId") UUID userId) {
        User user = findUserUseCase.findUserById(UserId.of(userId));
        return userDomainDtoMapper.toUserFoundResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lecturers/search")
    @ResponseStatus(HttpStatus.OK)
    List<LecturerFullNameResponse> getLecturersByIds(
            @RequestParam String containsQuery
    ) {
        return searchLecturerUserCase.searchLecturers(containsQuery)
                .stream()
                .map(user -> new LecturerFullNameResponse(
                        user.getUuid(), user.getFirstName(), user.getLastName())
                ).toList();
    }
}

