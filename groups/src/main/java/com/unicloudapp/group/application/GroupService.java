package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.GroupId;
import com.unicloudapp.group.domain.GroupStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class GroupService {

    private final GroupRepositoryPort groupRepository;
    private final GroupFactory groupFactory;
    private final GroupToDtoMapper groupMapper;
    private final UserValidationService userValidationService;

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

    @Transactional
    public void addAttender(GroupId groupId, UserId attenderId) {
        boolean isStudent = userValidationService.isUserStudent(attenderId);
        if (!isStudent) {
            throw new RuntimeException("User " + attenderId.getValue() + " is not a student or does not exist.");
        }
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow();
        group.addAttender(attenderId);
        groupRepository.save(group);
    }

    public Page<GroupDTO> getAllGroups(Pageable pageable) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        int offset = page * size;
        List<Group> groups = groupRepository.findAll(offset, size);
        long total = groupRepository.count();

        List<GroupDTO> groupDTOList = groups.stream()
                .map(groupMapper::toDto)
                .toList();

        return new PageImpl<>(groupDTOList, PageRequest.of(page, size), total);
    }

    public Page<GroupDTO> getAllGroupsByStatus(
            Pageable pageable,
            GroupStatus.Type status
    ) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        int offset = page * size;
        List<Group> groups = groupRepository.findAllByStatus(offset, size, status);
        long total = groupRepository.countByStatus(status);
        List<GroupDTO> groupDTOList = groups.stream()
                .map(groupMapper::toDto)
                .toList();
        return new PageImpl<>(groupDTOList, PageRequest.of(page, size), total);
    }
}
