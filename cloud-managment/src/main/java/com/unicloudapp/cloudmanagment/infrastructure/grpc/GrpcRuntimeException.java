package com.unicloudapp.cloudmanagment.infrastructure.grpc;

class GrpcRuntimeException extends RuntimeException {

    GrpcRuntimeException(String message) {
        super(message);
    }
}
