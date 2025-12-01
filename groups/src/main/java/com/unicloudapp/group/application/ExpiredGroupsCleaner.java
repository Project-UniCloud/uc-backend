package com.unicloudapp.group.application;

import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.vo.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ExpiredGroupsCleaner {

    private final GroupRepositoryPort groupRepository;
    private final GroupService groupService;

    @Scheduled(cron = "${groups.archiveExpiredGroupsCron}")
    protected void archiveExpiredGroups() {
        GroupFilterCriteria groupFilterCriteria = GroupFilterCriteria.builder()
                .status(GroupStatus.of(GroupStatus.Type.ACTIVE))
                .pastExpiresDate(true)
                .build();
        Page<GroupRowProjection> allByCriteria = groupRepository.findAllByCriteria(groupFilterCriteria, Pageable.unpaged());
        allByCriteria.forEach(group -> groupService.archive(GroupId.of(group.getUuid())));
    }
}
