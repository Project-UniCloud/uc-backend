package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;

public interface CloudAccessClientControllerFactoryPort {

    CloudAccessClientController create(String host, int port);
}
