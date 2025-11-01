package com.unicloudapp.management.application;

import com.unicloudapp.management.domain.CloudAccessClientController;

public interface CloudAccessClientControllerFactoryPort {

    CloudAccessClientController create(String host, int port);
}
