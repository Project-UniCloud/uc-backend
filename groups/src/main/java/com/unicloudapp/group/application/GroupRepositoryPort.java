package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.Group;

public interface GroupRepositoryPort {

    Group save(Group group);
}
