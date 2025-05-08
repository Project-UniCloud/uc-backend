package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.Group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepositoryPort {

    Group save(Group group);

    Optional<Group> findById(UUID id);

    List<Group> findAll(int offset, int size);

    long count();
}
