package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;

public class GroupService {

    private final GroupRepositoryPort groupRepository;
    private final GroupFactory groupFactory;

    public GroupService(GroupRepositoryPort groupRepository, GroupFactory groupFactory) {
        this.groupRepository = groupRepository;
        this.groupFactory = groupFactory;
    }

    public Group createGroup(GroupDTO groupDTO) {
        Group group = groupFactory.create(
                groupDTO.name(),
                groupDTO.semester(),
                groupDTO.lecturers(),
                groupDTO.startDate(),
                groupDTO.endDate()
        );
        return groupRepository.save(group);
    }
}
