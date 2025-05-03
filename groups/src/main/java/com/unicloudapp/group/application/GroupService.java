package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.GroupId;
import jakarta.transaction.Transactional;

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

    // WARNING! - What if user with attenderId is not exists or is not a student?
    // If problem add QueryEvent with user validation
    @Transactional
    public void addAttender(GroupId groupId, UserId attenderId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow();
        group.addAttender(attenderId);
        groupRepository.save(group);
    }
}
