package com.unicloudapp.management.infrastructure.grpc;

class GrpcRuntimeException extends RuntimeException {

    GrpcRuntimeException(String message) {
        super(message);
    }
}
