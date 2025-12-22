package com.unicloudapp.group.application;

import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class BasicGroupQueryService implements GroupQueryService {

    private final GroupRepositoryPort groupRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupCloudDto> getActiveGroups() {
        return groupRepository.findActiveGroups();
    }

    @Override
    public GroupDto getGroupByCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId) {
        return groupRepository.findByCloudResourceAccessId(cloudResourceAccessId);
    }
}
