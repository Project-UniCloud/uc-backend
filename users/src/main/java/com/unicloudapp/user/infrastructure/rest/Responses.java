package com.unicloudapp.user.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
record CreatedLecturerResponse(UUID lecturerId) {

}

@Builder
record CreatedStudentResponse(UUID studentId) {

}
