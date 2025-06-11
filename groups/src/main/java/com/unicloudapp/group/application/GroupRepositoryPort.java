package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepositoryPort {

    Group save(Group group);

    Optional<Group> findById(UUID id);

    List<Group> findAll(int offset, int size);

    long count();

    List<GroupRowProjection> findAllByStatus(int offset,
                                int size,
                                GroupStatus.Type status
    );

    long countByStatus(GroupStatus.Type status);

    GroupDetailsProjection findGroupDetailsByUuid(UUID uuid);

    boolean existsByNameAndSemester(GroupName name,
                                    Semester semester
    );

    boolean existsById(UUID id);

    List<GroupRowProjection> findAllByStatusAndNameLike(int offset,
                                                        int size,
                                                        GroupStatus.Type status,
                                                        String groupName
    );

    long countByStatusAndNameLike(GroupStatus.Type status,
                                  String groupName
    );
}
