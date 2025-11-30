package com.unicloudapp.cloud.infrastructure.grpc;

class GrpcRuntimeException extends RuntimeException {

    GrpcRuntimeException(String message) {
        super(message);
    }
}
