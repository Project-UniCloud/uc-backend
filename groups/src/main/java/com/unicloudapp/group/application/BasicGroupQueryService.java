package com.unicloudapp.group.application;

import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class BasicGroupQueryService implements GroupQueryService {

    private final GroupRepositoryPort groupRepository;

    @Override
    public List<GroupCloudDto> getGroupCloudDto() {
        return groupRepository.findAllGroupCloudDto();
    }
}
