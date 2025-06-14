package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.group.domain.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GroupRepositoryPort {

    Group save(Group group);

    Optional<Group> findById(UUID id);

    GroupDetailsProjection findGroupDetailsByUuid(UUID uuid);

    boolean existsByNameAndSemester(GroupName name,
                                    Semester semester
    );

    Page<GroupRowProjection> findAllByCriteria(
            GroupFilterCriteria criteria,
            Pageable pageable
    );

    Page<GroupRowProjection> findAllByCriteriaAndContainsCloudResourceAccess(
            GroupFilterCriteria criteria,
            Pageable pageable,
            Set<CloudResourceAccessId> cloudResourceAccesses
    );
}
