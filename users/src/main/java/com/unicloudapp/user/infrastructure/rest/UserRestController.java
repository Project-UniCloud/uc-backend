package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.user.application.UserDTO;
import com.unicloudapp.user.application.UserService;
import com.unicloudapp.user.application.ports.out.AuthenticationPort;
import com.unicloudapp.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserRestController {

    private final AuthenticationPort authenticationPort;
    private final UserService userService;

    @PostMapping("/auth")
    ResponseEntity<Void> authenticate(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        authenticationPort.authenticate(authenticateRequest.login(),
                authenticateRequest.password()
        );
        return ResponseEntity.status(200)
                .build();
    }

    @PostMapping("/lecturers")
    @ResponseStatus(HttpStatus.CREATED)
    CreatedLecturerResponse createLecturer(
            @Valid
            @RequestBody
            CreateLecturerRequest createLecturerRequest
    ) {
        var userDTO = UserDTO.builder()
                .userIndexNumber(createLecturerRequest.userIndexNumber())
                .firstName(createLecturerRequest.firstName())
                .lastName(createLecturerRequest.lastName())
                .build();
        User createdLecturer = userService.createLecturer(userDTO);
        return CreatedLecturerResponse.builder()
                .lecturerId(createdLecturer.getUserId().getValue())
                .build();
    }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    CreatedStudentResponse createLecturer(
            @Valid
            @RequestBody
            CreateStudentRequest request
    ) {
        var userDTO = UserDTO.builder()
                .userIndexNumber(request.userIndexNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
        User createdLecturer = userService.createStudent(userDTO);
        return CreatedStudentResponse.builder()
                .studentId(createdLecturer.getUserId().getValue())
                .build();
    }
}

