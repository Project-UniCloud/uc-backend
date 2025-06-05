package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceTypeRowView;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.*;
import com.unicloudapp.group.domain.*;
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
    private final CloudResourceAccessQueryService cloudResourceAccessQueryService;
    private final CloudResourceAccessCommandService cloudResourceAccessCommandService;
    private final UserCommandService userCommandService;

    @Transactional
    public Group createGroup(GroupDTO groupDTO) {
        boolean isGroupWithSameNameAndSemesterExists = groupRepository.existsByNameAndSemester(
                GroupName.of(groupDTO.name()),
                Semester.of(groupDTO.semester())
        );
        boolean isGroupWithSameIdExists = groupRepository.existsByNameAndSemester(
                GroupName.of(groupDTO.name()),
                Semester.of(groupDTO.semester())
        );
        if (isGroupWithSameNameAndSemesterExists) {
            throw new RuntimeException("Group with name " + groupDTO.name() + " and semester " + groupDTO.semester() + " already exists.");
        }
        if (isGroupWithSameIdExists) {
            throw new RuntimeException("Group with id " + groupDTO.groupId() + " already exists.");
        }
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
    public void addAttender(GroupId groupId, StudentBasicData studentBasicData) {
        UserId userId = userCommandService.createStudent(studentBasicData);
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow();
        group.addAttender(userId);
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
        Map<UserId, UserFullName> userFullNames = userQueryService.getFullNameForUserIds(
                groups.stream()
                        .flatMap(g -> g.getLecturers().stream().map(UserId::of))
                        .toList()
        );
        Map<UUID, Set<CloudResourceType>> cloudResourceTypes = groups.stream()
                .collect(Collectors.toMap(
                        GroupRowProjection::getUuid,
                        group ->
                                cloudResourceAccessQueryService.getCloudResourceTypes(
                                        group.getCloudResourceAccesses()
                                                .stream()
                                                .map(CloudResourceAccessId::of)
                                                .collect(Collectors.toSet())
                                )
                ));

        List<GroupRowView> groupViews = groups.stream()
                .map(group -> {
                    String joinedLecturers = group.getLecturers()
                            .stream()
                            .map(uuid -> userFullNames.get(UserId.of(uuid)))
                            .filter(Objects::nonNull)
                            .map(UserFullName::getFullName)
                            .collect(Collectors.joining(", "));

                    String joinedAccessList = cloudResourceTypes.entrySet()
                            .stream()
                            .filter(entry -> entry.getKey().equals(group.getUuid()))
                            .flatMap(entry -> entry.getValue().stream().map(CloudResourceType::getName))
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
                                .toList()
                ).values()).stream().map(UserFullName::getFullName).collect(Collectors.toSet());
        return GroupDetailsView.builder()
                .groupId(details.getUuid())
                .name(details.getName())
                .lecturerFullNames(lecturers)
                .status(details.getGroupStatus().getDisplayName())
                .description(details.getDescription())
                .endDate(details.getEndDate())
                .startDate(details.getStartDate())
                .semester(details.getSemester())
                .build();
    }

    public Page<UserDetails> getAttendersDetailsByGroupId(GroupId groupId, Pageable pageable) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        int offset = page * size;
        List<UserDetails> userDetailsByIds = userQueryService.getUserDetailsByIds(
                group.getAttenders(), offset, size
        );
        return new PageImpl<>(userDetailsByIds,
                PageRequest.of(page,
                        size
                ),
                userQueryService.countUsersByIds(group.getLecturers())
        );
    }

    @Transactional
    public void addAttenders(GroupId groupId, List<StudentBasicData> studentBasicData) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        List<UserId> importedStudents = userCommandService.importStudents(studentBasicData);
        importedStudents.forEach(group::addAttender);
        groupRepository.save(group);
    }

    public CloudResourceAccessId giveCloudResourceAccess(
            GroupId groupId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType
    ) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(group.getName())
                .semester(group.getSemester())
                .build();
        List<UserLogin> lecturerLogins = userQueryService.getUserLoginsByIds(
                group.getLecturers()
        );
        if (!cloudResourceAccessQueryService.isCloudGroupExists(groupUniqueName, cloudAccessClientId)) {
            cloudResourceAccessCommandService.createGroup(groupUniqueName, cloudAccessClientId, lecturerLogins, cloudResourceType);
        }
        CloudResourceAccessId cloudResourceAccessId = cloudResourceAccessCommandService.giveGroupCloudResourceAccess(
                cloudAccessClientId,
                cloudResourceType,
                groupUniqueName
        );
        group.giveCloudResourceAccess(cloudResourceAccessId);
        groupRepository.save(group);
        return cloudResourceAccessId;
    }

    public List<CloudResourceTypeRowView> getCloudResourceAccesses(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        Set<CloudResourceAccessId> cloudResourceAccesses = group.getCloudResourceAccesses();
        return cloudResourceAccessQueryService.getCloudResourceTypesDetails(cloudResourceAccesses);
    }

    public void updateGroup(GroupId groupId, GroupDTO groupDTO) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        group.update(
                GroupName.of(groupDTO.name()),
                groupDTO.lecturers().stream().map(UserId::of).collect(Collectors.toSet()),
                StartDate.of(groupDTO.startDate()),
                EndDate.of(groupDTO.endDate()),
                Description.of(groupDTO.description())
        );
        groupRepository.save(group);
    }
}
