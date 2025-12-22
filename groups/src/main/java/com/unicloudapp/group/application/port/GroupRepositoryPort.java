package com.unicloudapp.group.application.port;

import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.group.application.GroupDetailsProjection;
import com.unicloudapp.group.application.GroupFilterCriteria;
import com.unicloudapp.group.application.GroupRowProjection;
import com.unicloudapp.group.domain.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    List<GroupCloudDto> findActiveGroups();

    GroupDto findByCloudResourceAccessId(CloudResourceAccessId cloudResourceAccessId);
}
