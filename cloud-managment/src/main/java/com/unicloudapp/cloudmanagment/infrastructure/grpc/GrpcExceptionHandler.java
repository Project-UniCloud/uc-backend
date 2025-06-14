package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static io.grpc.Status.*;

@RestControllerAdvice
class GrpcExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    void handle(StatusRuntimeException statusRuntimeException) {
        //TODO
    }
}
