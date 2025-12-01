package com.unicloudapp.cloud.infrastructure.grpc;

import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GrpcExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    void handle(StatusRuntimeException statusRuntimeException) {
        //TODO
    }
}
