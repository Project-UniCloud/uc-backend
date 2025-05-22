package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudAccessQueryService;
import com.unicloudapp.common.domain.user.FullName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.user.UserQueryService;
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

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GroupService {

    private final GroupRepositoryPort groupRepository;
    private final GroupFactory groupFactory;
    private final GroupToDtoMapper groupMapper;
    private final UserValidationService userValidationService;
    private final UserQueryService userQueryService;
    private final CloudAccessQueryService cloudAccessQueryService;

    public Group createGroup(GroupDTO groupDTO) {
        Group group = groupFactory.create(
                groupDTO.name(),
                groupDTO.semester(),
                groupDTO.lecturers(),
                groupDTO.startDate(),
                groupDTO.endDate(),
                groupDTO.description()
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

    public Page<GroupRowView> getAllGroupsByStatus(
            Pageable pageable,
            GroupStatus.Type status
    ) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        int offset = page * size;
        List<GroupRowProjection> groups = groupRepository.findAllByStatus(offset, size, status);
        Map<UUID, FullName> userFullNames = userQueryService.getFullNameForUserIds(
                groups.stream()
                        .flatMap(g -> g.getLecturers().stream().map(UserId::of))
                        .toList()
        );
        Map<String, String> cloudAccessesNames = cloudAccessQueryService.getCloudAccessNames(
                groups.stream()
                        .flatMap(g -> g.getCloudAccesses().stream())
                        .collect(Collectors.toSet())
        );
        List<GroupRowView> groupViews = groups.stream()
                .map(group -> {
                    String joinedLecturers = group.getLecturers()
                            .stream()
                            .map(userFullNames::get)
                            .filter(Objects::nonNull)
                            .map(FullName::getFullName)
                            .collect(Collectors.joining(", "));

                    String joinedAccessList = group.getCloudAccesses()
                            .stream()
                            .map(cloudAccessesNames::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", "));

                    return new GroupRowView(
                            group.getUuid(),
                            group.getName(),
                            group.getSemester(),
                            group.getEndDate(),
                            joinedLecturers,
                            joinedAccessList
                    );
                })
                .toList();
        long total = groupRepository.countByStatus(status);
        return new PageImpl<>(groupViews, PageRequest.of(page, size), total);
    }

    public GroupDetailsView findById(UUID groupId) {
        GroupDetailsProjection details = groupRepository.findGroupDetailsByUuid(groupId);
        Set<String> lecturers = new HashSet<>(userQueryService.getFullNameForUserIds(
                        details.getLecturers()
                                .stream()
                                .map(UserId::of)
                                .collect(Collectors.toList())
                ).values()).stream().map(FullName::getFullName).collect(Collectors.toSet());
        return GroupDetailsView.builder()
                .groupId(details.getUuid())
                .lecturerFullNames(lecturers)
                .status(details.getGroupStatus().getDisplayName())
                .description(details.getDescription())
                .endDate(details.getEndDate())
                .startDate(details.getStartDate())
                .semester(details.getSemester())
                .build();
    }
}
