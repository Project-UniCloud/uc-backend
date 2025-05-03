package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccess;

public interface CloudAccessRepositoryPort {

    CloudAccess save(CloudAccess cloudAccess);
}
