package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.Group;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepositoryPort {

    Group save(Group group);

    Optional<Group> findById(UUID id);
}
