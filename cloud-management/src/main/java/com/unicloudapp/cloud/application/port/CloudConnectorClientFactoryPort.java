package com.unicloudapp.cloud.application.port;

public interface CloudConnectorClientFactoryPort {

    CloudConnectorClientPort create(String host, int port);
}
